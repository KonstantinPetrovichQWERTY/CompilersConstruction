package syntaxAnalyzer.declarations;

import java.util.List;
import lexicalAnalyzer.Token;
import lexicalAnalyzer.TokenType;

public class ReturnStatement extends Declaration {
    private Cls cls;
    
    private String type;
    private Expression value;
    
    public ReturnStatement(Cls cls) {
        this.cls = cls;
    }

    public Cls getCls() {
        return cls;
    }
    public String getType() {
        return type;
    }

    public Expression getValue() {
        return value;
    }

    // return x.Mult(x)

    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        if (tokens.get(index).getToken() == TokenType.KEYWORD_RETURN) {
            index += 1; // Move past the 'return' keyword
        } else {
            throw new RuntimeException("Expected 'return' keyword, found: " + tokens.get(index).getToken());
        }
        
        // TODO: empy return statement?
        value = new Expression(cls);
        index = value.parse(tokens, index) + 1; // TODO: Нужен ли +1?

        return index;
    }
}
