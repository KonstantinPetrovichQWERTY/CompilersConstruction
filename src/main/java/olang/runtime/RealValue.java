package olang.runtime;

public final class RealValue implements Value {
    private final double value;

    public RealValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String display() {
        return Double.toString(value);
    }
}
