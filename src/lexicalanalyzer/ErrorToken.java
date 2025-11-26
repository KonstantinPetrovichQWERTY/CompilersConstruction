package lexicalanalyzer;

public class ErrorToken extends Token{

    private final String value;
    
    public ErrorToken(String value, Span span) {
        super(TokenCode.ERROR, value, span);
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

}
