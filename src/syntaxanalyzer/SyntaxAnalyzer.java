package syntaxanalyzer;

public class SyntaxAnalyzer {

    /**
     * Perform syntax analysis over the provided token stream.
     */
    public java.util.List<syntaxanalyzer.declarations.Cls> analyze(java.util.List<lexicalanalyzer.Token> tokens) {
        AST rootNode = new AST();
        return rootNode.parse(tokens);
    }

    public static java.util.List<syntaxanalyzer.declarations.Cls> analyzeTokens(
            java.util.List<lexicalanalyzer.Token> tokens) {
        return new SyntaxAnalyzer().analyze(tokens);
    }
}
