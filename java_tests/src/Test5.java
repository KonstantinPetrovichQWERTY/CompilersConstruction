// Test 5: Class with Constructors
class Test5 {
    public static void main(String[] args) {
        Car car1 = new Car("Toyota");
        Car car2 = new Car("Honda", "Civic");
        System.out.println(car1.getBrand()); // Output: Toyota
        System.out.println(car2.getBrand() + " " + car2.getModel()); // Output: Honda Civic
    }
}

class Car {
    private String brand;
    private String model;

    public Car(String brand) {
        this.brand = brand;
        this.model = "Unknown";
    }

    public Car(String brand, String model) {
        this.brand = brand;
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }
}
