package lexicalanalyzer;

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

    @Override
    public String toString() {
        return  token.name() +
                ", " + getValue();
    }

}
