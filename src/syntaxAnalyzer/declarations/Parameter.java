package syntaxAnalyzer.declarations;

import java.util.List;
import lexicalAnalyzer.Token;
import lexicalAnalyzer.TokenType;

public class Parameter extends Declaration {
    private String name;
    private String type;

    // Constructor to initialize the parameter name and type
    public Parameter(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        // Expect identifier (parameter name)
        if (tokens.get(index).getToken() == TokenType.IDENTIFIER) {
            name = tokens.get(index).getValue();
            index += 1;
        } else {
            throw new RuntimeException("Expected parameter name (identifier), found: " + tokens.get(index).getToken());
        }

        // Expect ':' for type declaration
        if (tokens.get(index).getToken() == TokenType.PUNCTUATION_SEMICOLON) {
            index += 1; // Move past ':'
        } else {
            throw new RuntimeException("Expected ':', found: " + tokens.get(index).getToken());
        }

        // Expect type (e.g., Integer, String, etc.)
        // TODO: Not identifier, int_keyword, str_keyword ...
        if (tokens.get(index).getToken() == TokenType.IDENTIFIER) {
            type = tokens.get(index).getValue();
            index += 1;
        } else {
            throw new RuntimeException("Expected parameter type (IDENTIFIER), found: " + tokens.get(index).getToken());
        }

        return index; // Return updated index
    }
}
