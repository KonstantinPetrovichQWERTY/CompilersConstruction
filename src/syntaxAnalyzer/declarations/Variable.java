package syntaxanalyzer.declarations;

import java.util.List;
import lexicalanalyzer.Token;
import lexicalanalyzer.TokenType;

public class Variable extends Declaration {
    private String name;
    private Expression expression;

    public String getName() {
        return name;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        ensureToken(tokens, index, TokenType.KEYWORD_VAR);
        index++;

        Token identifier = ensureToken(tokens, index, TokenType.IDENTIFIER);
        name = identifier.getValue();
        index++;

        ensureToken(tokens, index, TokenType.PUNCTUATION_SEMICOLON);
        index++;

        expression = new Expression();
        index = expression.parse(tokens, index);

        return index; // Return the updated index
    }


}
