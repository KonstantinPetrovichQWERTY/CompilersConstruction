package syntaxanalyzer.declarations;

import java.util.List;

import lexicalanalyzer.Token;
import lexicalanalyzer.TokenType;

public class Variable extends Declaration {
    private String name;
    private String type;
    private String initialValue;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getInitialValue() {
        return initialValue;
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

        // Parse the type (e.g., Integer, String, etc.)
        // TODO: SHOULD BE ANY CLASS, or Expression
        // TODO: Expression class
        if (tokens.get(index).getToken().name().startsWith("KEYWORD_")) {
            type = tokens.get(index).getValue();
            index += 1;
        } else {
            throw new RuntimeException("Expected variable type, found: " + tokens.get(index).getToken());
        }

        // Expect '(' for the initial value
        if (tokens.get(index).getToken() == TokenType.PUNCTUATION_LEFT_PARENTHESIS) {
            index += 1; // Move past '('
        } else {
            throw new RuntimeException("Expected '(', found: " + tokens.get(index).getToken());
        }

        // Parse the initial value (e.g., 100)
        if (tokens.get(index).getToken() == TokenType.LITERAL_INTEGER) {
            initialValue = tokens.get(index).getValue();
            index += 1;
        } else {
            throw new RuntimeException("Expected literal integer for initial value, found: " + tokens.get(index).getToken());
        }

        // Expect ')' to close the initial value
        if (tokens.get(index).getToken() == TokenType.PUNCTUATION_RIGHT_PARENTHESIS) {
//            index += 1; // Move past ')'
        } else {
            throw new RuntimeException("Expected ')', found: " + tokens.get(index).getToken());
        }

        return index; // Return the updated index
    }
}
