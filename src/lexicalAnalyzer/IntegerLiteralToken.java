package lexicalAnalyzer;
public class IntegerLiteralToken extends Token{

    private int value;
    
    public IntegerLiteralToken(int value) {
        super(TokenType.LITERAL_INTEGER);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
