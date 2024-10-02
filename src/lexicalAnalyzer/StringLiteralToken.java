package lexicalAnalyzer;
public class StringLiteralToken extends Token{

    private String value;
    
    public StringLiteralToken(String value) {
        super(TokenType.LITERAL_STRING);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
