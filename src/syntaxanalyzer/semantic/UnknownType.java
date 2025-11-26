package syntaxanalyzer.semantic;

/**
 * Placeholder type used while the type system is not implemented.
 */
public final class UnknownType implements Type {
    public static final UnknownType INSTANCE = new UnknownType();

    private UnknownType() {
    }

    @Override
    public String toString() {
        return "UnknownType";
    }
}
