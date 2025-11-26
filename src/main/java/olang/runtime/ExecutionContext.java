package olang.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Holds execution state for a single method/constructor invocation.
 */
public final class ExecutionContext {
    private final Interpreter interpreter;
    private final InstanceValue thisInstance;
    private final Map<String, Value> locals = new HashMap<>();

    public ExecutionContext(Interpreter interpreter, InstanceValue thisInstance) {
        this.interpreter = Objects.requireNonNull(interpreter, "interpreter");
        this.thisInstance = thisInstance;
    }

    public Interpreter interpreter() {
        return interpreter;
    }

    public InstanceValue thisInstance() {
        return thisInstance;
    }

    public Map<String, Value> locals() {
        return locals;
    }
}
