package lexicalAnalyzer;
public class IntegerLiteralToken extends Token{

    private final int value;
    
    public IntegerLiteralToken(int value) {
        super(TokenType.LITERAL_INTEGER);
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
