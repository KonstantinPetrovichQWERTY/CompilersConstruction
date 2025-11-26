package syntaxanalyzer.declarations;

import java.util.List;
import java.util.Objects;
import lexicalanalyzer.Token;
import lexicalanalyzer.TokenCode;

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
        ensureToken(tokens, index++, TokenCode.KEYWORD_CLASS);
        name = ensureToken(tokens, index++, TokenCode.IDENTIFIER).getLexeme();
        if (Objects.requireNonNull(tokens.get(index).getToken()) == TokenCode.KEYWORD_EXTENDS) {
            index += 1;
            baseClass = ensureToken(tokens, index++, TokenCode.IDENTIFIER).getLexeme();
        }
        ensureToken(tokens, index, TokenCode.KEYWORD_IS);
        body = new ClsBody();
        index = body.parse(tokens, index);
        ensureToken(tokens, index, TokenCode.KEYWORD_END);
        return index;
    }
}
