package lexicalAnalyzer;

public class RealLiteralToken extends Token{

    private final double value;
    
    public RealLiteralToken(double value) {
        super(TokenType.LITERAL_REAL);
        this.value = value;
    }

    @Override
    public String getValue() {
        return Double.toString(value);
    }

    @Override
    public Object getTypedValue() {
        return this.value;
    }
}
