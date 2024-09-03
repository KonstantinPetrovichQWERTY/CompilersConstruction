// Test 1: Basic Class and Object Creation
class Test1 {
    public static void main(String[] args) {
        Person p = new Person("Alice", 30);
        System.out.println(p.getName()); // Output: Alice
        System.out.println(p.getAge());  // Output: 30
    }
}

class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
