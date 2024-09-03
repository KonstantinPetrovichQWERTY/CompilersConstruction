// Test 2: Inheritance and Method Overriding
class Test2 {
    public static void main(String[] args) {
        Animal a = new Dog("Buddy", "Golden Retriever");
        System.out.println(a.getName()); // Output: Buddy
        System.out.println(((Dog) a).getBreed()); // Output: Golden Retriever
    }
}

class Animal {
    private String name;

    public Animal(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class Dog extends Animal {
    private String breed;

    public Dog(String name, String breed) {
        super(name);
        this.breed = breed;
    }

    public String getBreed() {
        return breed;
    }
}
