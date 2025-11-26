package syntaxanalyzer.declarations;

import java.util.List;
import lexicalanalyzer.Token;
import lexicalanalyzer.TokenCode;
import syntaxanalyzer.SyntaxException;

public class Assignment extends Declaration {

    private String name;
    private Token nameToken;
    private Expression expression;
    
    // Assignment : Identifier ':=' Expression

    public String getName() {
        return name;
    }

    public Token getNameToken() {
        return nameToken;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        Token current = tokens.get(index);
        if (current.getToken() == TokenCode.IDENTIFIER) {
            nameToken = current;
            name = current.getLexeme();
            index += 1; // Move forward
        } else {
            throw SyntaxException.at("Expected assignment variable name (identifier), found: " + current.getToken(), current);
        }

        // Expect ':=' for condition
        current = tokens.get(index);
        if (current.getToken() == TokenCode.PUNCTUATION_SEMICOLON_EQUAL) {
            index += 1; // Move past ':='
        } else {
            throw SyntaxException.at("Expected ':=', found: " + current.getToken(), current);
        }

        // Parse expression
        expression = new Expression();
        index = expression.parse(tokens, index);

        return index;
    }

    
}
