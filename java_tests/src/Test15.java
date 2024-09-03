// Test 15: Recursion
class Test15 {
    public static void main(String[] args) {
        System.out.println(factorial(5)); // Output: 120
    }

    public static int factorial(int n) {
        if (n == 0) {
            return 1;
        }
        return n * factorial(n - 1);
    }
}
