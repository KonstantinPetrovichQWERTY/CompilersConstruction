package syntaxanalyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lexicalanalyzer.LexicalAnalyzer;
import lexicalanalyzer.Token;
import lexicalanalyzer.TokenCode;
import syntaxanalyzer.declarations.Cls;
import syntaxanalyzer.semantic.SemanticAnalyzer;
import syntaxanalyzer.semantic.Type;

public class OCompiler {

    private static final Path GENERATED_CLASS_DIR = Path.of("build", "o", "classes");
    private static final SemanticAnalyzer SEMANTIC_ANALYZER = new SemanticAnalyzer();
    private static final CodeGenerator CODE_GENERATOR = new CodeGenerator();

    public static void main(String[] args) {
        if (args.length < 2) {
            printUsageAndExit();
        }

        String command = args[0];
        String filePath = args[1];
        boolean filterTokens = false;

        if (args.length > 2) {
            if (!"tokenize".equals(command) || args.length != 3 || !"--filter".equals(args[2])) {
                printUsageAndExit();
            }
            filterTokens = true;
        }

        switch (command) {
            case "tokenize" -> tokenizeCommand(filePath, filterTokens);
            case "parse" -> parseCommand(filePath);
            case "build" -> buildCommand(filePath);
            case "run" -> runCommand(filePath);
            default -> printUsageAndExit();
        }
    }

    private static void parseCommand(String filePath) {
        SyntaxException.setCurrentSource(filePath);
        List<Token> tokens = LexicalAnalyzer.getTokens(filePath);
        List<Cls> classes = SyntaxAnalyzer.analyzeTokens(tokens);
        AstPrinter.print(classes);
    }

    private static void tokenizeCommand(String filePath, boolean filterTokens) {
        SyntaxException.setCurrentSource(filePath);
        List<Token> tokens = LexicalAnalyzer.getTokens(filePath);
        if (filterTokens) {
            tokens = TokenFilter.filterTokens(tokens);
        }

        for (int index = 0; index < tokens.size(); index++) {
            Token token = tokens.get(index);
            String lexeme = formatLexeme(token);
            System.out.printf("%d. %s %s%n", index, token.getToken().name(), lexeme);
        }
    }

    private static String formatLexeme(Token token) {
        TokenCode tokenCode = token.getToken();
        if (tokenCode == TokenCode.PUNCTUATION_TABULATION) {
            return "\\t";
        }
        if (tokenCode == TokenCode.PUNCTUATION_LINE_BREAK) {
            return "\\n";
        }
        if (tokenCode == TokenCode.EOF) {
            return "<EOF>";
        }
        return token.getLexeme();
    }

    private static void printUsageAndExit() {
        System.err.println("Usage: o <command> <file> [options]");
        System.err.println("Commands:");
        System.err.println("  tokenize <file> [--filter] - tokenize file and print tokens");
        System.err.println("  parse <file>               - parse file and print AST");
        System.err.println("  build <file>               - compile file and emit bytecode");
        System.err.println("  run <file>                 - compile file, emit bytecode, and execute it");
        System.exit(1);
    }

    private static void buildCommand(String filePath) {
        BuildResult result = buildArtifact(filePath);
        System.out.println("Generated class file: " + result.aliasClassFile());
    }

    private static void runCommand(String filePath) {
        BuildResult result = buildArtifact(filePath);
        String classpath = result.classesDir().toString()
                + File.pathSeparator
                + Path.of("build", "install", "CompilersConstruction", "lib", "*");
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-cp", classpath, result.entryClassName());
        processBuilder.inheritIO();
        try {
            int exitCode = processBuilder.start().waitFor();
            System.exit(exitCode);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to invoke JVM for " + result.entryClassName(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Execution interrupted", e);
        }
    }

    /**
     * Builds the JVM artifact for a particular `.o` source file.
     *
     * Example walk-through for {@code main.o} that prints the numbers 0..9:
     * <ol>
     *     <li>{@link SyntaxException#setCurrentSource(String)} stores the file name so any parser error can report
     *     {@code main.o:line:column}.</li>
     *     <li>We lex + parse the file. For {@code main.o}, the AST root list contains a single {@code Cls} named
     *     {@code Main} whose constructor body holds a {@code while} loop.</li>
     *     <li>Semantic analysis is minimal; we only ensure the AST isn't empty.</li>
     *     <li>{@link #deriveClassName(String)} returns a safe JVM identifier. For {@code main.o} it becomes {@code main_o}.</li>
     *     <li>{@link CodeGenerator#generate(String, String, String, java.util.List, java.util.List)} receives:
     *         <ul>
     *             <li>{@code generatedClassName = "main_o"} – the JVM class we write to disk,</li>
     *             <li>{@code entryPointClassName = "Main"} – the O class whose constructor must run first,</li>
     *             <li>{@code sourceContent = "..."} – the exact contents of {@code main.o}, embedded verbatim,</li>
     *             <li>plus the AST + placeholder types.</li>
     *         </ul>
     *         The generator emits a tiny ASM bootstrap whose {@code main} loads the string literal (the O source) and
     *         calls {@code olang.runtime.Interpreter.runSource(source, "Main")}. At runtime this interpreter re-parses
     *         the string and executes the {@code while} loop.</li>
     *     <li>Finally, we persist the generated bytecode to:
     *         <ul>
     *             <li>{@code build/o/classes/main_o.class} – the canonical build artifact,</li>
     *             <li>{@code main.o.class} – a convenience copy next to the source file for easy inspection.</li>
     *         </ul>
     *     </li>
     * </ol>
     */
    private static BuildResult buildArtifact(String filePath) {
        SyntaxException.setCurrentSource(filePath);
        List<Token> tokens = LexicalAnalyzer.getTokens(filePath);
        List<Cls> classes = SyntaxAnalyzer.analyzeTokens(tokens);
        List<Type> types = SEMANTIC_ANALYZER.analyze(classes);

        Path sourcePath = Path.of(filePath);
        String sourceContent;
        try {
            sourceContent = Files.readString(sourcePath);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read source content", e);
        }
        String generatedClassName = deriveClassName(sourcePath.getFileName().toString());
        String entryPointClassName = classes.stream()
                .filter(cls -> "Main".equals(cls.getName()))
                .map(Cls::getName)
                .findFirst()
                .orElseGet(() -> classes.isEmpty() ? generatedClassName : classes.get(0).getName());
        byte[] bytecode = CODE_GENERATOR.generate(generatedClassName, entryPointClassName, sourceContent, classes, types);

        try {
            Files.createDirectories(GENERATED_CLASS_DIR);
            Path generatedClass = GENERATED_CLASS_DIR.resolve(generatedClassName + ".class");
            Files.write(generatedClass, bytecode);

            Path aliasDir = sourcePath.getParent();
            if (aliasDir == null) {
                aliasDir = Path.of(".");
            }
            Files.createDirectories(aliasDir);
            Path aliasClass = aliasDir.resolve(sourcePath.getFileName() + ".class");
            Files.write(aliasClass, bytecode);

            return new BuildResult(generatedClassName, GENERATED_CLASS_DIR, aliasClass);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to persist generated bytecode", e);
        }
    }

    private static String deriveClassName(String from) {
        if (from == null || from.isEmpty()) {
            return "GeneratedProgram";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < from.length(); i++) {
            char ch = from.charAt(i);
            if (Character.isJavaIdentifierPart(ch)) {
                builder.append(ch);
            } else {
                builder.append('_');
            }
        }
        if (builder.length() == 0) {
            builder.append("GeneratedProgram");
        }
        if (!Character.isJavaIdentifierStart(builder.charAt(0))) {
            builder.insert(0, '_');
        }
        return builder.toString();
    }

    private record BuildResult(String entryClassName, Path classesDir, Path aliasClassFile) {}
}
