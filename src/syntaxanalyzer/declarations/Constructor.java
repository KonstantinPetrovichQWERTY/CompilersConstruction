package syntaxanalyzer.declarations;

import java.util.ArrayList;
import java.util.List;
import lexicalanalyzer.Token;
import lexicalanalyzer.TokenType;
import syntaxanalyzer.utils.ParameterDeclaration;

public class Constructor extends Declaration {

    private final Block body = new Block();
    private final Parameters parameters = new Parameters();
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
        index = parameters.parse(tokens, index);
        ensureToken(tokens, index, TokenType.PUNCTUATION_RIGHT_PARENTHESIS);
        index += 1;
        ensureToken(tokens, index, TokenType.KEYWORD_IS);
        index += 1;
        index = body.parse(tokens, index);
        ensureToken(tokens, index, TokenType.KEYWORD_END);
        return index;
    }

}
