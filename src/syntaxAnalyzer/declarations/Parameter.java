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

        // Expect ':' for type declaration (Optional)
        if (tokens.get(index).getToken() == TokenType.PUNCTUATION_SEMICOLON) {
            index += 1; // Move past ':'
        }

        // Expect type (e.g., Integer, String, etc.) (Optional)
        // TODO: Not identifier, int_keyword, str_keyword ...
        if (tokens.get(index).getToken().name().startsWith("LITERAL_")) {
            type = tokens.get(index).getValue();
            index += 1;
        }

        return index; // Return updated index
    }
}
