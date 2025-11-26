package olang.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents an instance of a user-defined class.
 */
public final class InstanceValue implements Value {
    private final RuntimeClass runtimeClass;
    private final Map<String, Value> fields = new HashMap<>();

    public InstanceValue(RuntimeClass runtimeClass) {
        this.runtimeClass = Objects.requireNonNull(runtimeClass, "runtimeClass");
    }

    public RuntimeClass getRuntimeClass() {
        return runtimeClass;
    }

    public Map<String, Value> getFields() {
        return fields;
    }

    public Value getField(String name) {
        return fields.getOrDefault(name, NullValue.INSTANCE);
    }

    public void setField(String name, Value value) {
        fields.put(name, value);
    }

    public boolean hasField(String name) {
        return runtimeClass.hasField(name);
    }

    @Override
    public String display() {
        return runtimeClass.getName();
    }
}
