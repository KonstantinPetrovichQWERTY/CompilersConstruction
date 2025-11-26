package lexicalanalyzer;

public class LexicalFragment {

    private final String content;
    private final Token.Span span;

    public LexicalFragment(String content, Token.Span span) {
        this.content = content;
        this.span = span;
    }

    public String getContent() {
        return content;
    }

    public Token.Span getSpan() {
        return span;
    }
}
