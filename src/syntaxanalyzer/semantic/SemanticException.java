package syntaxanalyzer.semantic;

import lexicalanalyzer.Token;
import syntaxanalyzer.SyntaxException;

/**
 * Signals semantic analysis failures.
 */
public final class SemanticException extends RuntimeException {

    public SemanticException(String message) {
        super(message);
    }

    public SemanticException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Formats the message with token position info, mirroring {@link SyntaxException#at(String, Token)}.
     */
    public static SemanticException at(String message, Token token) {
        return new SemanticException(SyntaxException.at(message, token).getMessage());
    }
}
