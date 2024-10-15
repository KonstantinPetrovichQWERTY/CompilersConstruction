package syntaxanalyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lexicalanalyzer.Token;
import lexicalanalyzer.TokenType;
import syntaxanalyzer.declarations.Cls;


public class AST {
    List<Cls> classes = new ArrayList<>();

    public List<Cls> parse(List<Token> tokens) {
        List<Token> filteredTokens = TokenFilter.filterTokens(tokens);
        int i = 0;

        while (i < filteredTokens.size()) {
            Token currentToken = filteredTokens.get(i);
            if (currentToken.getToken() == TokenType.KEYWORD_CLASS) {
                Cls cls = new Cls();
                i = cls.parse(filteredTokens, i);
                classes.add(cls);
            } else if (currentToken.getToken() == TokenType.EOF) {
                break;
            }
            i = i + 1;
        }
        return classes;
    }
}