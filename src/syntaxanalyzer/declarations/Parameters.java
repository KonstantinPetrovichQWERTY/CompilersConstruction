package syntaxanalyzer.declarations;

import lexicalanalyzer.Token;
import lexicalanalyzer.TokenCode;
import syntaxanalyzer.utils.ParameterDeclaration;
import syntaxanalyzer.SyntaxException;

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
        ensureToken(tokens, index++, TokenCode.PUNCTUATION_LEFT_PARENTHESIS);
        while (index < tokens.size()) {
            TokenCode currentTokenType = tokens.get(index).getToken();
            if (currentTokenType == TokenCode.IDENTIFIER) {
                String name = ensureToken(tokens, index++, TokenCode.IDENTIFIER).getLexeme();
                ensureToken(tokens, index++, TokenCode.PUNCTUATION_SEMICOLON);
                String type = ensureToken(tokens, index++, TokenCode.IDENTIFIER).getLexeme();
                parameters.add(new ParameterDeclaration(name, type));
                if (tokens.get(index).getToken() == TokenCode.PUNCTUATION_RIGHT_PARENTHESIS) {
                    return index;
                }
                ensureToken(tokens, index++, TokenCode.PUNCTUATION_COMMA);
            } else if (currentTokenType == TokenCode.PUNCTUATION_RIGHT_PARENTHESIS) {
                return index;
            } else {
                Token current = tokens.get(index);
                throw SyntaxException.at("Unexpected token: " + currentTokenType, current);
            }
        }
        Token last = index > 0 ? tokens.get(index - 1) : null;
        throw SyntaxException.at("Parameters declaration is not finished", last);
    }


}
