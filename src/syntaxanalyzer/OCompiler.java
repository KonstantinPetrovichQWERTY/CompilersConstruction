package syntaxanalyzer;

import java.util.List;
import lexicalanalyzer.LexicalAnalyzer;
import lexicalanalyzer.Token;
import syntaxanalyzer.declarations.Cls;

public class OCompiler {

    public static void main(String[] args) {
        if (args.length < 2) {
            printUsageAndExit();
        }

        String command = args[0];
        String filePath = args[1];

        switch (command) {
            case "parse" -> parseCommand(filePath);
            case "build" -> {
                System.err.println("build: not implemented yet");
                System.exit(1);
            }
            case "run" -> {
                System.err.println("run: not implemented yet");
                System.exit(1);
            }
            default -> printUsageAndExit();
        }
    }

    private static void parseCommand(String filePath) {
        List<Token> tokens = LexicalAnalyzer.getTokens(filePath);
        List<Cls> classes = SyntaxAnalyzer.analyzeTokens(tokens);
        AstPrinter.print(classes);
    }

    private static void printUsageAndExit() {
        System.err.println("Usage: o <command> <file>");
        System.err.println("Commands:");
        System.err.println("  parse <file>   - parse file and print AST");
        System.err.println("  build <file>   - compile file (not implemented yet)");
        System.err.println("  run <file>     - compile and run file (not implemented yet)");
        System.exit(1);
    }
}
