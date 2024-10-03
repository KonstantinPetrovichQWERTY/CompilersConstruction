package lexicalAnalyzer;

public class ErrorToken extends Token{

    private String value;
    
    public ErrorToken(String value) {
        super(TokenType.ERROR);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
