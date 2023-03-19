import pool.ThreadPool;
import pool.task.TaskGenerator;

import java.util.Scanner;

public class Runner {
    public static Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        Test1();
    }

    // terminate immediately after 9 seconds
    public static void Test1() {
        System.out.println("Enter number of threads: "); //4
        int numOfThreads = in.nextInt();

        System.out.println("Enter time limit for queue (in millis): "); //50_000
        long qTimeLim = in.nextLong();

        ThreadPool threadPool = new ThreadPool(numOfThreads, qTimeLim);
        TaskGenerator taskGenerator = new TaskGenerator(threadPool, 2, 1000);

        try {
            threadPool.start();
            taskGenerator.setDaemon(true);
            taskGenerator.start();

            threadPool.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    //terminate after all tasks are finished (add new tasks forbidden after termination called)
    public static void Test2() {
        System.out.println("Enter number of threads: "); //4
        int numOfThreads = in.nextInt();

        System.out.println("Enter time limit for queue (in seconds): "); //50
        int qTimeLim = in.nextInt();

        ThreadPool threadPool = new ThreadPool(numOfThreads, qTimeLim);
        TaskGenerator taskGenerator = new TaskGenerator(threadPool, 20, 1000);

        threadPool.start();
        taskGenerator.setDaemon(true);
        taskGenerator.start();

        try {
            Thread.sleep(9000);
            /*threadPool.terminateAfter();*/

            System.out.println("Queue size: " + threadPool.getQSize());
            System.out.println("Thread pool Alive status: " + threadPool.isAlive());

            threadPool.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}