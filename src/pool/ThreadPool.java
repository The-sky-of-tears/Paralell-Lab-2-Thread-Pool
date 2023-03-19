package pool;

import pool.task.Task;
import pool.task.TaskQueue;

import java.util.ArrayList;
import java.util.List;

public class ThreadPool extends Thread{
    private final List<PoolWorker> threadPool;

    private final TaskQueue queue;

    public ThreadPool(int numOfThreads, long qTimeLim) {
        queue = new TaskQueue(qTimeLim);

        threadPool = new ArrayList<>(numOfThreads);
        for (int i = 0; i < numOfThreads; i++) {
            threadPool.add(new PoolWorker());
        }
    }

    @Override
    public void run() {
        threadPool.forEach(Thread::start);

        threadPool.forEach(poolWorker -> {
            try {
                poolWorker.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public boolean addTask(Task newTask) {
        synchronized (queue) {
            if (queue.isEmpty()) {
                queue.notify();
            }

            return queue.addTask(newTask);
        }
    }

    public int getQSize() {
        return queue.getQSize();
    }

    private class PoolWorker extends Thread{
        private static int nextId = 0;
        private final int id;

        public PoolWorker() {
            id = nextId++;
        }

        @Override
        public void run() {
            while(!Thread.interrupted()) {
                synchronized (queue) {
                    while (queue.isEmpty()) {
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            System.out.printf("PoolWorker id %d is interrupted while waiting\n", id);
                            break;
                        }
                    }
                }

                try {
                    Task currentTask = queue.getNextTask();
                    System.out.printf("PoolWorker %d has taken %s\n", id, currentTask);

                    currentTask.doRoutine();

                    System.out.printf("PoolWorker %d has finished %s\n", id, currentTask);
                } catch (InterruptedException e) {
                    System.out.printf("PoolWorker id %d is interrupted in action\n", id);
                    break;
                }
            }
        }
    }
}
