package pool.task;

import java.util.concurrent.PriorityBlockingQueue;

public class TaskQueue {
    private final PriorityBlockingQueue<Task> queue;

    private final long qTimeLim; //millis
    private long timeLoad = 0; //millis

    public TaskQueue(Long qTimeLim) {
        this.qTimeLim = qTimeLim;

        queue = new PriorityBlockingQueue<>();
    }

    synchronized public boolean isEmpty() {
        return queue.isEmpty();
    }

    synchronized public boolean addTask(Task newTask) {
        if (timeLoad + newTask.getRequiredTime() > qTimeLim) {
            return false;
        } else {
            queue.put(newTask);
            timeLoad += newTask.getRequiredTime();
            return true;
        }
    }

    synchronized public Task getNextTask() throws InterruptedException{
        Task currentTask = queue.take();
        timeLoad -= currentTask.getRequiredTime();
        return currentTask;
    }

    synchronized public int getQSize() {
        return queue.size();
    }

    synchronized public void clear() {
        queue.clear();
    }
}