package lexicalAnalyzer;

public class Token {
    private TokenType token;

    public Token(TokenType token){
        this.token = token;
    }

    public TokenType getToken(){
        return token;
    }
}