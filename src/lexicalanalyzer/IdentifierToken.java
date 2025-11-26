package lexicalanalyzer;

public class IdentifierToken extends Token{
    public IdentifierToken(String lexeme, Span span) {
        super(TokenCode.IDENTIFIER, lexeme, span);
    }
}
