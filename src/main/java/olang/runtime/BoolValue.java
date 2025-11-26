package main.java.olang.runtime;

public final class BoolValue implements Value {
    private final boolean value;

    public BoolValue(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String display() {
        return Boolean.toString(value);
    }
}
