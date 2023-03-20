import pool.ThreadPool;
import pool.task.TaskGenerator;

import java.util.Scanner;

public class Runner {
    public static Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        Test1();
    }

    // terminate immediately after N (10) seconds
    public static void Test1() {
        System.out.println("Enter number of threads: "); //4
        int numOfThreads = in.nextInt();

        System.out.println("Enter time limit for queue (in millis): "); //50_000
        long qTimeLim = in.nextLong();

        ThreadPool threadPool = new ThreadPool(numOfThreads, qTimeLim);
        TaskGenerator taskGenerator = new TaskGenerator(threadPool, 20, 1000);

        try {
            threadPool.start();
            taskGenerator.setDaemon(true);
            taskGenerator.start();

            Thread.sleep(10000);
            threadPool.terminateNow();

            System.out.printf("\nTermination called. Queue size: %d\n", threadPool.getQSize());

            threadPool.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    //terminate after all tasks are finished (add new tasks forbidden after termination called)
    public static void Test2() {
        System.out.println("Enter number of threads: "); //4
        int numOfThreads = in.nextInt();

        System.out.println("Enter time limit for queue (in millis): "); //50_000
        long qTimeLim = in.nextLong();

        ThreadPool threadPool = new ThreadPool(numOfThreads, qTimeLim);
        TaskGenerator taskGenerator = new TaskGenerator(threadPool, 20, 1000);

        try {
            threadPool.start();
            taskGenerator.setDaemon(true);
            taskGenerator.start();

            Thread.sleep(10000);
            threadPool.terminateAfter();

            System.out.printf("\nTermination called. Queue size: %d\n", threadPool.getQSize());

            threadPool.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}