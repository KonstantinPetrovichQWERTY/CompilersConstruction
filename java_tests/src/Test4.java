// Test 4: Control Structures:Loops and Conditionals
class Test4 {
    public static void main(String[] args) {
        int[] numbers = {1, 2, 3, 4, 5};
        int sum = 0;
        for (int i = 0; i < numbers.length; i++) {
            sum += numbers[i];
        }
        System.out.println("Sum: " + sum); // Output: Sum: 15

        if (sum > 10) {
            System.out.println("Sum is greater than 10"); // Output: Sum is greater than 10
        }
    }
}
