package lexicalAnalyzer;

public class IntegerLiteralToken extends Token{

    private final int value;
    
    public IntegerLiteralToken(int value) {
        super(TokenType.LITERAL_INTEGER);
        this.value = value;
    }

    @Override
    public String getValue() {
        return String.valueOf(value);
    }

    @Override
    public Object getTypedValue() {
        return value;
    }

    public int min(int other) {
        return Math.min(this.value, other);
    }

    public int max(int other) {
        return Math.max(this.value, other);
    }

    public double toReal() {
        return (double) this.value;
    }

    public boolean toBoolean() {
        return this.value != 0;
    }

    public int toInteger() {
        return this.value;
    }

    public int unaryMinus() {
        return -this.value;
    }

    public int plus(int other) {
        return this.value + other;
    }

    public int minus(int other) {
        return this.value - other;
    }

    public int mult(int other) {
        return this.value * other;
    }

    public int div(int other) {
        if (other == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return this.value / other;
    }

    public int rem(int other) {
        if (other == 0) {
            throw new ArithmeticException("Remainder by zero");
        }
        return this.value % other;
    }

    public boolean less(int other) {
        return this.value < other;
    }

    public boolean lessEqual(int other) {
        return this.value <= other;
    }

    public boolean greater(int other) {
        return this.value > other;
    }

    public boolean greaterEqual(int other) {
        return this.value >= other;
    }

    public boolean equal(int other) {
        return this.value == other;
    }

    @Override
    public Token performOperation(TokenType method, Object obj) {
        int arg = (Integer) obj;
        switch (method) {
            case METHOD_MIN:
                return new IntegerLiteralToken(min(arg));
            case METHOD_MAX:
                return new IntegerLiteralToken(max(arg));
            case METHOD_TO_REAL:
                return new RealLiteralToken(toReal());
            case METHOD_TO_BOOLEAN:
                return new BooleanLiteralToken(toBoolean());
            case METHOD_TO_INTEGER:
                return new IntegerLiteralToken(toInteger());
            case METHOD_UNARY_MINUS:
                return new IntegerLiteralToken(unaryMinus());
            case METHOD_PLUS:
                return new IntegerLiteralToken(plus(arg));
            case METHOD_MINUS:
                return new IntegerLiteralToken(minus(arg));
            case METHOD_MULT:
                return new IntegerLiteralToken(mult(arg));
            case METHOD_DIV:
                return new IntegerLiteralToken(div(arg));
            case METHOD_REM:
                return new IntegerLiteralToken(rem(arg));
            case METHOD_LESS:
                return new BooleanLiteralToken(less(arg));
            case METHOD_LESS_EQUAL:
                return new BooleanLiteralToken(lessEqual(arg));
            case METHOD_GREATER:
                return new BooleanLiteralToken(greater(arg));
            case METHOD_GREATER_EQUAL:
                return new BooleanLiteralToken(greaterEqual(arg));
            case METHOD_EQUAL:
                return new BooleanLiteralToken(equal(arg));
            default:
                throw new IllegalArgumentException("Unknown method: " + method);
        }
    }
}
