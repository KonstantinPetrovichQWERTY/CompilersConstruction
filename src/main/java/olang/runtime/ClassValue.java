package olang.runtime;

import java.util.List;
import java.util.Objects;

/**
 * Reference to a class that can be invoked like a constructor.
 */
public final class ClassValue implements Value {
    private final String name;
    private final RuntimeClass runtimeClass; // null for builtin classes handled separately
    private final BuiltinClass builtin;

    public enum BuiltinClass {
        INTEGER,
        REAL,
        BOOLEAN,
        STRING,
        CHARACTER
    }

    public ClassValue(String name, RuntimeClass runtimeClass) {
        this.name = Objects.requireNonNull(name, "name");
        this.runtimeClass = runtimeClass;
        this.builtin = null;
    }

    public ClassValue(BuiltinClass builtin, String name) {
        this.name = Objects.requireNonNull(name, "name");
        this.runtimeClass = null;
        this.builtin = Objects.requireNonNull(builtin, "builtin");
    }

    public String getName() {
        return name;
    }

    public boolean isBuiltin() {
        return builtin != null;
    }

    public BuiltinClass getBuiltin() {
        return builtin;
    }

    public RuntimeClass getRuntimeClass() {
        return runtimeClass;
    }

    @Override
    public String display() {
        return name;
    }
}
