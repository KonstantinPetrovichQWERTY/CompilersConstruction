package lexicalanalyzer;

public class IdentifierToken extends Token{
    
    private final String value;
    
    public IdentifierToken(String value) {
        super(TokenType.IDENTIFIER);
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
