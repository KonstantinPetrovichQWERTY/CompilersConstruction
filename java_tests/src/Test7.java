// Test 7: Interfaces
class Test7 {
    public static void main(String[] args) {
        Drawable drawable = new CircleDrawable(5);
        drawable.draw(); // Output: Drawing a circle
    }
}

interface Drawable {
    void draw();
}

class CircleDrawable implements Drawable {
    private double radius;

    public CircleDrawable(double radius) {
        this.radius = radius;
    }

    @Override
    public void draw() {
        System.out.println("Drawing a circle");
    }
}
