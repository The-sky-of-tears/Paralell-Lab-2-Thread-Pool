package pool.task;

import pool.ThreadPool;

public class TaskGenerator extends Thread{
    private ThreadPool threadPool;

    private int numOfTasks;

    private long period; //millis

    public TaskGenerator(ThreadPool threadPool, int numOfTasks, long period) {
        this.threadPool = threadPool;
        this.numOfTasks = numOfTasks;
        this.period = period;
    }

    @Override
    public void run() {
        int counter = 0;
        while(counter++ < numOfTasks) {
            Task newTask = new Task();

            if (threadPool.addTask(newTask)) {
                System.out.println(newTask + " added SUCCESSFUL");
            } else {
                System.out.println(newTask + " added UNSUCCESSFUL");
            }

            try {
                Thread.sleep(period);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
