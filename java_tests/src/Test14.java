// Test 14: Lambda Expressions
import java.util.function.Function;

class Test14 {
    public static void main(String[] args) {
        Function<Integer, Integer> square = x -> x * x;
        System.out.println(square.apply(5)); // Output: 25
    }
}
