package syntaxanalyzer.declarations;

import java.util.ArrayList;
import java.util.List;
import lexicalanalyzer.Token;
import lexicalanalyzer.TokenType;

public class Constructor extends Declaration {

    private List<Parameter> parameters = new ArrayList<>();
    private Block body;
    
    public List<Parameter> getParameters() {
        return parameters;
    }
    
    public Block getBody() {
        return body;
    }

    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        if (tokens.get(index).getToken() == TokenType.KEYWORD_THIS) {
            index += 1; // Move past the 'this' keyword
        } else {
            throw new RuntimeException("Expected 'this' keyword, found: " + tokens.get(index).getToken());
        }


        // Expect '(' for parameters
        if (tokens.get(index).getToken() == TokenType.PUNCTUATION_LEFT_PARENTHESIS) {
            index += 1; // Move past '('
            index = parseParameters(tokens, index); // Parse parameters
        } else {
            throw new RuntimeException("Expected '(', found: " + tokens.get(index).getToken());
        }

        // Expect 'is' for method body start
        if (tokens.get(index).getToken() == TokenType.KEYWORD_IS) {
            index += 1; // Move past 'is'
        } else {
            throw new RuntimeException("Expected 'is', found: " + tokens.get(index).getToken());
        }

        // Parse method body (redirect to Block)
        body = new Block();
        index = body.parse(tokens, index) + 1;

        // Expect 'end' to finish method declaration
        if (tokens.get(index).getToken() == TokenType.KEYWORD_END) {
            return index;
        } else {
            throw new RuntimeException("Expected 'end', found: " + tokens.get(index).getToken());
        }
    }

    private Integer parseParameters(List<Token> tokens, Integer index) {
        while (tokens.get(index).getToken() != TokenType.PUNCTUATION_RIGHT_PARENTHESIS) {
            if (tokens.get(index).getToken() == TokenType.IDENTIFIER) {
                String paramName = tokens.get(index).getValue();
                index += 1;

                if (tokens.get(index).getToken() == TokenType.PUNCTUATION_SEMICOLON) {
                    index += 1; // Move past ':'

                    if (tokens.get(index).getToken() == TokenType.IDENTIFIER) {
                        String paramType = tokens.get(index).getValue();
                        parameters.add(new Parameter(paramName, paramType));
                        index += 1;
                    } else {
                        throw new RuntimeException("Expected parameter type, found: " + tokens.get(index).getToken());
                    }
                } else {
                    throw new RuntimeException("Expected ':', found: " + tokens.get(index).getToken());
                }

                // Check for commas separating parameters
                if (tokens.get(index).getToken() == TokenType.PUNCTUATION_COMMA) {
                    index += 1; // Move past ','
                }
            } else {
                throw new RuntimeException("Expected parameter name (identifier), found: " + tokens.get(index).getToken());
            }
        }
        index += 1; // Move past ')'
        return index;
    }
}
