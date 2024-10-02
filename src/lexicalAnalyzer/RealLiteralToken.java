package lexicalAnalyzer;
public class RealLiteralToken extends Token{

    private double value;
    
    public RealLiteralToken(double value) {
        super(TokenType.LITERAL_REAL);
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
