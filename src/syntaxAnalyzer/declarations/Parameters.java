package syntaxanalyzer.declarations;

import lexicalanalyzer.Token;
import lexicalanalyzer.TokenType;
import syntaxanalyzer.utils.ParameterDeclaration;

import java.util.ArrayList;
import java.util.List;

public class Parameters extends Declaration
{
    private final List<ParameterDeclaration> parameters = new ArrayList<ParameterDeclaration>();

    public List<ParameterDeclaration> getParameters() {
        return parameters;
    }


    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        ensureToken(tokens, index++, TokenType.PUNCTUATION_LEFT_PARENTHESIS);
        while (index < tokens.size()) {
            TokenType currentTokenType = tokens.get(index).getToken();
            if (currentTokenType == TokenType.IDENTIFIER) {
                String name = ensureToken(tokens, index++, TokenType.IDENTIFIER).getValue();
                ensureToken(tokens, index++, TokenType.PUNCTUATION_SEMICOLON);
                String type = ensureToken(tokens, index++, TokenType.IDENTIFIER).getValue();
                parameters.add(new ParameterDeclaration(name, type));
                if (tokens.get(index).getToken() == TokenType.PUNCTUATION_RIGHT_PARENTHESIS) {
                    return index;
                }
                ensureToken(tokens, index++, TokenType.PUNCTUATION_COMMA);
            } else if (currentTokenType == TokenType.PUNCTUATION_RIGHT_PARENTHESIS) {
                return index;
            } else {
                throw new RuntimeException("Unexpected token: " + currentTokenType);
            }
        }
        throw new RuntimeException("Parameters declaration is not finished");
    }


}


