package syntaxanalyzer.declarations;

import java.util.ArrayList;
import java.util.List;
import lexicalanalyzer.Token;
import lexicalanalyzer.TokenType;
import syntaxanalyzer.utils.ParameterDeclaration;

public class Method extends Declaration {
    private String name;
    private String returnType;

    private final Parameters parameters = new Parameters();
    private Block body;

    public String getName() {
        return name;
    }

    public String getReturnType() {
        return returnType;
    }

    public List<ParameterDeclaration> getParameters() {
        return parameters.getParameters();
    }

    public Block getBody() {
        return body;
    }

    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        ensureToken(tokens, index, TokenType.KEYWORD_METHOD);
        index += 1;

        name = ensureToken(tokens, index, TokenType.IDENTIFIER).getValue();
        index += 1;

        ensureToken(tokens, index, TokenType.PUNCTUATION_LEFT_PARENTHESIS);
        index = parameters.parse(tokens, index);

        ensureToken(tokens, index, TokenType.PUNCTUATION_RIGHT_PARENTHESIS);
        index += 1;

        // Expect ':' and return type (OPTIONAL)
        if (tokens.get(index).getToken() == TokenType.PUNCTUATION_SEMICOLON) {
            ensureToken(tokens, index, TokenType.PUNCTUATION_SEMICOLON);
            index += 1; // Move past ':'

            returnType = ensureToken(tokens, index, TokenType.IDENTIFIER).getValue();
            index += 1;
        }

        ensureToken(tokens, index, TokenType.KEYWORD_IS);
        index +=1;

        body = new Block();
        index = body.parse(tokens, index);

        ensureToken(tokens, index, TokenType.KEYWORD_END);
        return index;
    }
}
