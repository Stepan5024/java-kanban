package Controller;

public class TaskManager {
    private static long taskId;

    public static long generateId(){
        return taskId + 1;
    }
}
