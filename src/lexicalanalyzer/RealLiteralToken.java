package lexicalanalyzer;

public class RealLiteralToken extends Token{

    private final double value;
    
    public RealLiteralToken(double value, Span span) {
        super(TokenCode.LITERAL_REAL, Double.toString(value), span);
        this.value = value;
    }

    @Override
    public String getValue() {
        return Double.toString(value);
    }

    public double getTypedValue() {
        return this.value;
    }
}
