package olang.runtime;

public final class IntValue implements Value {
    private final int value;

    public IntValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String display() {
        return Integer.toString(value);
    }
}
