package Model;

import Controller.TaskManager;

public class Task {
    String title;
    String description;
    long id;

    public long getId() {
        return id;
    }

    TaskStatus status;

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", title=" + title +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Task(String title, String description, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.id = TaskManager.generateId();
        this.status = status;
    }
}
