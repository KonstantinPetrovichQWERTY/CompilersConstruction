package syntaxanalyzer;

import java.util.List;
import lexicalanalyzer.LexicalAnalyzer;
import lexicalanalyzer.Token;
import lexicalanalyzer.TokenCode;
import syntaxanalyzer.declarations.Cls;

public class OCompiler {

    public static void main(String[] args) {
        if (args.length < 2) {
            printUsageAndExit();
        }

        String command = args[0];
        String filePath = args[1];

        switch (command) {
            case "tokenize" -> tokenizeCommand(filePath);
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
        SyntaxException.setCurrentSource(filePath);
        List<Token> tokens = LexicalAnalyzer.getTokens(filePath);
        List<Cls> classes = SyntaxAnalyzer.analyzeTokens(tokens);
        AstPrinter.print(classes);
    }

    private static void tokenizeCommand(String filePath) {
        SyntaxException.setCurrentSource(filePath);
        List<Token> tokens = LexicalAnalyzer.getTokens(filePath);

        for (int index = 0; index < tokens.size(); index++) {
            Token token = tokens.get(index);
            String lexeme = formatLexeme(token);
            System.out.printf("%d. %s %s%n", index + 1, token.getToken().name(), lexeme);
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
        System.err.println("Usage: o <command> <file>");
        System.err.println("Commands:");
        System.err.println("  tokenize <file> - tokenize file and print tokens");
        System.err.println("  parse <file>    - parse file and print AST");
        System.err.println("  build <file>    - compile file (not implemented yet)");
        System.err.println("  run <file>      - compile and run file (not implemented yet)");
        System.exit(1);
    }
}
