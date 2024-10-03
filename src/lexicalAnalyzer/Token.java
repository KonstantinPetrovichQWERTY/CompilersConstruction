package lexicalAnalyzer;

public class Token {
    private TokenType token;

    public Token(TokenType token){
        this.token = token;
    }

    public TokenType getToken(){
        return token;
    }

    public String getValue() {
        return this.token.getValue();
    } 
}
