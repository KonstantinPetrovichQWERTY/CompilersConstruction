// Test 8: Exception Handling
class Test8 {
    public static void main(String[] args) {
        try {
            int result = divide(10, 0);
            System.out.println(result);
        } catch (ArithmeticException e) {
            System.out.println("Cannot divide by zero"); // Output: Cannot divide by zero
        }
    }

    public static int divide(int a, int b) {
        return a / b;
    }
}
