package syntaxanalyzer;

import java.util.ArrayList;
import java.util.List;

import lexicalanalyzer.Token;
import lexicalanalyzer.TokenCode;

public class TokenFilter {

    public static List<Token> filterTokens(List<Token> tokens) {
        List<Token> filteredTokens = new ArrayList<>();

        for (Token tk : tokens) {
            switch (tk.getToken()) {
                case TokenCode.PUNCTUATION_SPACE, 
                     TokenCode.PUNCTUATION_TABULATION, 
                     TokenCode.PUNCTUATION_LINE_BREAK -> {
                    continue;  // Skip these tokens
                }
                default -> filteredTokens.add(tk);  // Add all other tokens
            }
        }

        return filteredTokens;
    }
}
