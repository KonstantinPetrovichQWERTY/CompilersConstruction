package main.java.olang.runtime;

import syntaxanalyzer.declarations.Cls;
import syntaxanalyzer.declarations.Constructor;
import syntaxanalyzer.declarations.Method;
import syntaxanalyzer.declarations.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class RuntimeClass {
    private final String name;
    private final List<Variable> fields = new ArrayList<>();
    private final List<Constructor> constructors = new ArrayList<>();
    private final List<Method> methods = new ArrayList<>();

    public RuntimeClass(Cls cls) {
        Objects.requireNonNull(cls, "cls");
        this.name = cls.getName();
        if (cls.getBody() != null) {
            fields.addAll(cls.getBody().getVariables());
            constructors.addAll(cls.getBody().getConstructors());
            methods.addAll(cls.getBody().getMethods());
        }
    }

    public String getName() {
        return name;
    }

    public List<Variable> getFields() {
        return fields;
    }

    public List<Constructor> getConstructors() {
        return constructors;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public Constructor findConstructor(int argCount) {
        return constructors.stream()
                .filter(c -> c.getParameters().size() == argCount)
                .findFirst()
                .orElse(null);
    }

    public Method findMethod(String name, int argCount) {
        for (Method method : methods) {
            if (method.getName().equals(name) && method.getParameters().size() == argCount) {
                return method;
            }
        }
        return null;
    }

    public boolean hasField(String name) {
        return fields.stream().anyMatch(v -> v.getName().equals(name));
    }
}
