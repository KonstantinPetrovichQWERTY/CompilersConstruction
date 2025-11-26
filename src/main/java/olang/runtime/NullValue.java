package olang.runtime;

/**
 * Represents the absence of a value.
 */
public enum NullValue implements Value {
    INSTANCE;

    @Override
    public String display() {
        return "null";
    }
}
