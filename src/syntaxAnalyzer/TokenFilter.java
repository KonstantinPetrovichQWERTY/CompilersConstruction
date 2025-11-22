package syntaxAnalyzer;

import java.util.ArrayList;
import java.util.List;

import lexicalAnalyzer.Token;
import lexicalAnalyzer.TokenType;

public class TokenFilter {

    public static List<Token> filterTokens(List<Token> tokens) {
        List<Token> filteredTokens = new ArrayList<>();

        for (Token tk : tokens) {
            switch (tk.getToken()) {
                case TokenType.PUNCTUATION_SPACE, 
                     TokenType.PUNCTUATION_TABULATION, 
                     TokenType.PUNCTUATION_LINE_BREAK -> {
                    continue;  // Skip these tokens
                }
                default -> filteredTokens.add(tk);  // Add all other tokens
            }
        }

        return filteredTokens;
    }
}
