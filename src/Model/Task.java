package Model;

import Controller.TaskManager;

public class Task {
    String title;
    String description;
    long id;
    TaskStatus status;

    public Task(String title, String description, long id, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.id = TaskManager.generateId();
        this.status = status;
    }
}
