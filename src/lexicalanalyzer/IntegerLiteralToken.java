package lexicalanalyzer;

public class IntegerLiteralToken extends Token{

    private final int value;
    
    public IntegerLiteralToken(int value, Span span) {
        super(TokenCode.LITERAL_INTEGER, String.valueOf(value), span);
        this.value = value;
    }

    @Override
    public String getValue() {
        return String.valueOf(value);
    }

    public int getTypedValue() {
        return value;
    }
}
