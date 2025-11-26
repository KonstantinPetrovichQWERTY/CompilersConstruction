package main.java.olang.runtime;

/**
 * Marker interface for all runtime values produced by the O interpreter.
 */
public interface Value {
    /**
     * Human readable representation used by the builtin {@code print} routine.
     */
    String display();
}
