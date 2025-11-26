package lexicalAnalyzer;

public class Token {
    private final TokenType token;

    public Token(TokenType token){
        this.token = token;
    }

    public TokenType getToken(){
        return token;
    }

    public String getValue() {
        return this.token.getValue();
    } 

    public Object getTypedValue() {
        return 0;
    }

    public Token performOperation(TokenType method, Object obj) {
        throw new UnsupportedOperationException("Unimplemented method 'performOperation'");
    }
}
