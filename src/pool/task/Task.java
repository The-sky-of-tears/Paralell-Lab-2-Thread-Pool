package pool.task;

import java.util.Random;

public class Task implements Comparable<Task>{
    //main task's attribute that defines its position in PriorityQueue
    private final long requiredTime; //in millis

    private static int nextId = 0;
    private final int id;

    //random generator for requiredTime
    private static final Random random = new Random(System.currentTimeMillis());

    @Override
    public int compareTo(Task task)
    {
        return Long.compare(this.requiredTime, task.requiredTime);
    }

    @Override
    public String toString() {
        return "Task ID: " + id + ", required time: " + requiredTime;
    }

    public Task() {
        id = nextId++;
        requiredTime = random.nextInt(6, 13) * 1000L;
    }

    public long getRequiredTime() {
        return requiredTime;
    }

    public int getId() {
        return id;
    }

    //simulation of performing a real task
    public void doRoutine() throws InterruptedException{
        Thread.sleep(requiredTime);
    }
}
