package lexicalanalyzer;

public class StringLiteralToken extends Token{

    private final String value;
    
    public StringLiteralToken(String value, Span span) {
        super(TokenCode.LITERAL_STRING, value, span);
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
