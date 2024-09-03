// Test 6: Abstract Classes
abstract class Test6 {
    public static void main(String[] args) {
        Shape circle = new Circle(5);
        System.out.println(circle.getArea()); // Output: Area of the circle
    }
}

abstract class Shape {
    abstract double getArea();
}

class Circle extends Shape {
    private double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    @Override
    double getArea() {
        return Math.PI * radius * radius;
    }
}
