package syntaxanalyzer;

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
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-cp", result.classesDir().toString(), result.entryClassName());
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

    private static BuildResult buildArtifact(String filePath) {
        SyntaxException.setCurrentSource(filePath);
        List<Token> tokens = LexicalAnalyzer.getTokens(filePath);
        List<Cls> classes = SyntaxAnalyzer.analyzeTokens(tokens);
        List<Type> types = SEMANTIC_ANALYZER.analyze(classes);

        Path sourcePath = Path.of(filePath);
        String entryClassName = deriveClassName(sourcePath.getFileName().toString());
        byte[] bytecode = CODE_GENERATOR.generate(entryClassName, classes, types);

        try {
            Files.createDirectories(GENERATED_CLASS_DIR);
            Path generatedClass = GENERATED_CLASS_DIR.resolve(entryClassName + ".class");
            Files.write(generatedClass, bytecode);

            Path aliasDir = sourcePath.getParent();
            if (aliasDir == null) {
                aliasDir = Path.of(".");
            }
            Files.createDirectories(aliasDir);
            Path aliasClass = aliasDir.resolve(sourcePath.getFileName() + ".class");
            Files.write(aliasClass, bytecode);

            return new BuildResult(entryClassName, GENERATED_CLASS_DIR, aliasClass);
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
