// Test 3: Method Overloading
class Test3 {
    public static void main(String[] args) {
        Calculator calc = new Calculator();
        System.out.println(calc.add(5, 10)); // Output: 15
        System.out.println(calc.add(5.5, 10.5)); // Output: 16.0
    }
}

class Calculator {
    public int add(int a, int b) {
        return a + b;
    }

    public double add(double a, double b) {
        return a + b;
    }
}
