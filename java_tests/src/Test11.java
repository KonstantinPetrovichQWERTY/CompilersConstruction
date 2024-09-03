// Test 11: Threads
class Test11 {
    public static void main(String[] args) {
        Thread thread = new Thread(new RunnableTask());
        thread.start();
    }
}

class RunnableTask implements Runnable {
    public void run() {
        System.out.println("Thread is running");
    }
}
