package syntaxanalyzer.declarations;

import java.util.ArrayList;
import java.util.List;
import lexicalanalyzer.Token;
import lexicalanalyzer.TokenType;
import syntaxanalyzer.utils.ParameterDeclaration;

public class Constructor extends Declaration {

    private Block body;
    private Parameters parameters;
    public List<ParameterDeclaration> getParameters() {
        return parameters.getParameters();
    }
    
    public Block getBody() {
        return body;
    }

    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        ensureToken(tokens, index, TokenType.KEYWORD_THIS);
        index+=1;

        ensureToken(tokens, index, TokenType.PUNCTUATION_LEFT_PARENTHESIS);

        parameters = new Parameters();
        index = parameters.parse(tokens, index);

        ensureToken(tokens, index, TokenType.PUNCTUATION_RIGHT_PARENTHESIS);

        index += 1;
        ensureToken(tokens, index, TokenType.KEYWORD_IS);

        index += 1;
        // Parse method body (redirect to Block)
        body = new Block();
        index = body.parse(tokens, index);

        ensureToken(tokens, index, TokenType.KEYWORD_END);
        return index;
    }

}
