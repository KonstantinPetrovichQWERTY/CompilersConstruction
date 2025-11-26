package syntaxanalyzer;

import lexicalanalyzer.Token;

/**
 * Utility exception that enriches parser errors with token position data.
 */
public final class SyntaxException extends RuntimeException {

    private static volatile String currentSource = "<unknown>";

    private SyntaxException(String message) {
        super(message);
    }

    public static void setCurrentSource(String source) {
        currentSource = source != null ? source : "<unknown>";
    }

    public static SyntaxException at(String message, Token token) {
        String source = currentSource != null ? currentSource : "<unknown>";
        if (token == null) {
            return new SyntaxException(String.format("%s: %s", source, message));
        }
        Token.Span span = token.getSpan();
        if (span == null) {
            return new SyntaxException(String.format("%s: %s", source, message));
        }
        if (span.lineNum() >= 0) {
            String formatted = String.format("%s:%d:%d %s", source, span.lineNum(), span.posBegin(), message);
            return new SyntaxException(formatted);
        }
        return new SyntaxException(String.format("%s: %s", source, message));
    }
}
