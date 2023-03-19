package pool;

import pool.task.Task;
import pool.task.TaskQueue;

import java.util.ArrayList;
import java.util.List;

public class ThreadPool extends Thread{
    private final List<PoolWorker> threadPool;

    private final TaskQueue queue;

    private boolean isTerminated = false;

    public ThreadPool(int numOfThreads, long qTimeLim) {
        queue = new TaskQueue(qTimeLim);

        threadPool = new ArrayList<>(numOfThreads);
        for (int i = 0; i < numOfThreads; i++) {
            threadPool.add(new PoolWorker(i));
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
        synchronized (this) {
            if (isTerminated) {
                return false;
            }
        }

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

    synchronized public boolean isTerminated() {
        return isTerminated;
    }

    synchronized public void terminateNow() {
        isTerminated = true;
        threadPool.forEach(Thread::interrupt);

        queue.clear();
    }

    synchronized public void terminateAfter() {
        isTerminated = true;

        synchronized (queue) {
            queue.notifyAll();
        }
    }

    private class PoolWorker extends Thread{
        public final int id;

        public PoolWorker(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            while(!Thread.interrupted()) {
                synchronized (queue) {
                    while (queue.isEmpty() && !isTerminated()) {
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            System.out.printf("PoolWorker id %d is interrupted while waiting\n", id);
                            return;
                        }
                    }

                    if (queue.isEmpty() && isTerminated()) {
                        return;
                    }
                }

                try {
                    Task currentTask = queue.getNextTask();
                    System.out.printf("PoolWorker %d has taken %s\n", id, currentTask);

                    currentTask.doRoutine();

                    System.out.printf("PoolWorker %d has finished %s\n", id, currentTask);
                } catch (InterruptedException e) {
                    System.out.printf("PoolWorker id %d is interrupted in action\n", id);
                    return;
                }
            }
        }
    }
}
