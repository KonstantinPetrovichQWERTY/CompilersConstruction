package syntaxAnalyzer;

import java.util.ArrayList;
import java.util.List;
import lexicalAnalyzer.LexicalAnalyzer;
import lexicalAnalyzer.Token;
import lexicalAnalyzer.TokenType;

public class SyntaxAnalyzer {
    // private String fileName;
    // List<Token> tokens;

    // public SyntaxAnalyzer(String fileName, List<Token> tokens) {
    //     this.fileName = fileName;
    //     this.tokens = tokens;
    // }

    public static void main(String[] args) {
        List<Token> tokens = LexicalAnalyzer.getTokens("src/test/testOLang/ifStatment.o");
        
        List<Token> tokens2 = new ArrayList<>();

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

        AST rootNode = new AST();
        rootNode.parse(tokens2, 0);

    }   

    public static void errorMessage(String err) {
        System.out.println(err);
        System.exit(1);
    }
    
}
