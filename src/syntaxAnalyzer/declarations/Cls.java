package syntaxanalyzer.declarations;

import java.util.List;
import java.util.Objects;
import lexicalanalyzer.Token;
import lexicalanalyzer.TokenType;

public class Cls extends Declaration {
    String name;
    String baseClass;

    ClsBody body;

    public String getName() {
        return name;
    }

    public String getBaseClass() {
        return baseClass;
    }

    public ClsBody getBody() {
        return body;
    }

    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        ensureToken(tokens, index, TokenType.KEYWORD_CLASS);
        index += 1;

        name = ensureToken(tokens, index, TokenType.IDENTIFIER).getValue();
        index += 1;


        if (Objects.requireNonNull(tokens.get(index).getToken()) == TokenType.KEYWORD_EXTENDS) {
            index += 1;
            baseClass = ensureToken(tokens, index, TokenType.IDENTIFIER).getValue();
            index += 1;
        }

        ensureToken(tokens, index, TokenType.KEYWORD_IS);
        body = new ClsBody();
        index = body.parse(tokens, index);
        ensureToken(tokens, index, TokenType.KEYWORD_END);
        return index;
    }
}
