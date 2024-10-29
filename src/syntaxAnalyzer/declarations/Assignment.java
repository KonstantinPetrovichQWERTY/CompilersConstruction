package syntaxanalyzer.declarations;

import java.util.List;
import lexicalanalyzer.Token;
import lexicalanalyzer.TokenType;

public class Assignment extends Declaration {

    private String name;
    private Expression expression;
    
    // Assignment : Identifier ':=' Expression

    public String getName() {
        return name;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        if (tokens.get(index).getToken() == TokenType.IDENTIFIER) {
            name = tokens.get(index).getValue();
            index += 1; // Move forward
        } else {
            throw new RuntimeException("Expected assignment variable name (identifier), found: " + tokens.get(index).getToken());
        }

        // Expect ':=' for condition
        if (tokens.get(index).getToken() == TokenType.PUNCTUATION_SEMICOLON_EQUAL) {
            index += 1; // Move past ':='
        } else {
            throw new RuntimeException("Expected ':=', found: " + tokens.get(index).getToken());
        }

        // Parse expression
        expression = new Expression();
        index = expression.parse(tokens, index);

        return index;
    }

    
}
