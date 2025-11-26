package syntaxanalyzer.declarations;

import java.util.List;
import lexicalanalyzer.Token;
import lexicalanalyzer.TokenCode;

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
        if (tokens.get(index).getToken() == TokenCode.IDENTIFIER) {
            name = tokens.get(index).getLexeme();
            index += 1; // Move forward
        } else {
            throw new RuntimeException("Expected assignment variable name (identifier), found: " + tokens.get(index).getToken());
        }

        // Expect ':=' for condition
        if (tokens.get(index).getToken() == TokenCode.PUNCTUATION_SEMICOLON_EQUAL) {
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
