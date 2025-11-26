package syntaxAnalyzer.declarations;

import java.util.List;
import lexicalAnalyzer.Token;
import lexicalAnalyzer.TokenType;

public class Variable extends Declaration {
    private Cls cls;
    private String name;
    private Expression expression;

    public Variable(Cls cls) {
        this.cls = cls;
    }
    
    public Variable(Cls cls, Expression expr, String name) {
        this.cls = cls;
        this.name = name;
        this.expression = expr;
    }

    public Cls getCls() {
        return cls;
    }
    
    public String getName() {
        return name;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        if (tokens.get(index).getToken() == TokenType.KEYWORD_VAR) {
            index += 1; // Move past the 'var' keyword
        } else {
            throw new RuntimeException("Expected 'var' keyword, found: " + tokens.get(index).getToken());
        }

        // Parse the variable name (identifier)
        if (tokens.get(index).getToken() == TokenType.IDENTIFIER) {
            name = tokens.get(index).getValue();
            index += 1;
        } else {
            throw new RuntimeException("Expected variable name (identifier), found: " + tokens.get(index).getToken());
        }

        // Expect ':' (colon)
        if (tokens.get(index).getToken() == TokenType.PUNCTUATION_SEMICOLON) {
            index += 1; // Move past the ':'
        } else {
            throw new RuntimeException("Expected ':', found: " + tokens.get(index).getToken());
        }

        expression = new Expression(cls);
        index = expression.parse(tokens, index);

        return index; // Return the updated index
    }
}
