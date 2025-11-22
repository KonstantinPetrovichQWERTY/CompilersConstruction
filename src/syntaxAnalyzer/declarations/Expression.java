package syntaxAnalyzer.declarations;

import java.util.List;
import lexicalAnalyzer.Token;
import lexicalAnalyzer.TokenType;

public class Expression extends Declaration {
    private Token exprToken;

    public TokenType getExprToken() {
        return exprToken.getToken();
    }

    public Object getExprValue() {
        return exprToken.getValue();
    }

    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
