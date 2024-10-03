package lexicalAnalyzer;

public class RealLiteralToken extends Token{

    private double value;
    
    public RealLiteralToken(double value) {
        super(TokenType.LITERAL_REAL);
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
