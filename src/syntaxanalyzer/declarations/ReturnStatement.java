package syntaxanalyzer.declarations;

import java.util.List;
import lexicalanalyzer.Token;
import lexicalanalyzer.TokenCode;

public class ReturnStatement extends Declaration {
    private String type;
    private Expression value;

    public String getType() {
        return type;
    }

    public Expression getValue() {
        return value;
    }

    // return x.Mult(x)

    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        ensureToken(tokens, index++, TokenCode.KEYWORD_RETURN);
        if (tokens.get(index).getToken() == TokenCode.IDENTIFIER) {
            value = new Expression();
            index = value.parse(tokens, index);
        }
        return index;
    }
}
