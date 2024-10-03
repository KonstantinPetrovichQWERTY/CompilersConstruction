package lexicalAnalyzer;

public class StringLiteralToken extends Token{

    private final String value;
    
    public StringLiteralToken(String value) {
        super(TokenType.LITERAL_STRING);
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
