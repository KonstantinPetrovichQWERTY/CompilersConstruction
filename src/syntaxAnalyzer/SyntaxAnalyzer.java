package syntaxanalyzer;

import java.util.List;
import lexicalanalyzer.LexicalAnalyzer;
import lexicalanalyzer.Token;

public class SyntaxAnalyzer {
    public static void main(String[] args) {
        List<Token> tokens = LexicalAnalyzer.getTokens("src/test/testOLang/whileLoop.o");

        AST rootNode = new AST();
        rootNode.parse(tokens, 0);


        for (Token tk : tokens) {
            switch (tk.getToken()) {
                case TokenType.PUNCTUATION_SPACE -> { 
                    continue;
                }
                case TokenType.PUNCTUATION_TABULATION -> {
                    continue;
                }
                case TokenType.PUNCTUATION_LINE_BREAK -> {
                    continue;
                }
                default -> tokens2.add(tk);
            }
        }

        

    }   

    public static void errorMessage(String err) {
        System.out.println(err);
        System.exit(1);
    }
    
}
