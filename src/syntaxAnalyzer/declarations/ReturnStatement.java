package syntaxanalyzer.declarations;

import java.util.List;
import lexicalanalyzer.Token;
import lexicalanalyzer.TokenType;

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
        if (tokens.get(index).getToken() == TokenType.KEYWORD_RETURN) {
            index += 1; // Move past the 'return' keyword
        } else {
            throw new RuntimeException("Expected 'return' keyword, found: " + tokens.get(index).getToken());
        }
        
        // TODO: empy return statement?
        value = new Expression();
        index = value.parse(tokens, index) + 1; // TODO: Нужен ли +1?

        return index;
    }
}
