package syntaxanalyzer.semantic;

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
}
