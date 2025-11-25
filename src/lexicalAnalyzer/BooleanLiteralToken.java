package lexicalAnalyzer;

public class BooleanLiteralToken extends Token {
    
    private final Boolean value;
    
    public BooleanLiteralToken(Boolean value) {
        super(TokenType.LITERAL_BOOLEAN);
        this.value = value;
    }

    @Override
    public String getValue() {
        return String.valueOf(value);
    }

    public Boolean getTypedValue() {
        return value;
    }
}
