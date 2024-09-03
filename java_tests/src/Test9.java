// Test 9: Collections - ArrayList
import java.util.ArrayList;

class Test9 {
    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>();
        list.add("One");
        list.add("Two");
        list.add("Three");
        for (String item : list) {
            System.out.println(item); // Output: One, Two, Three
        }
    }
}
