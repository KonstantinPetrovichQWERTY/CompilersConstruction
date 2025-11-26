package lexicalanalyzer;

public class Token {
    public record Span(long lineNum, int posBegin) {}

    private final TokenCode token;
    private final Span span;
    private final String lexeme;

    protected Token(TokenCode token, String lexeme, Span span) {
        this.token = token;
        this.span = span;
        this.lexeme = lexeme;
    }

    protected Token(TokenCode token, Span span) {
        this(token, token.getLexeme(), span);
    }

    public TokenCode getToken() {
        return token;
    }

    public Span getSpan() {
        return span;
    }

    public String getLexeme() {
        return lexeme;
    }

    public String getValue() {
        return lexeme;
    }

    @Override
    public String toString() {
        return token.name();
    }
}
