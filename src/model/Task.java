package model;

import controller.managers.InMemoryTaskManager;
import controller.managers.TaskManager;

import manager.Managers;


public class Task {


    String title;
    String description;
    long id;
    TaskStatus status;


    public Task(String title, String description, TaskStatus taskStatus, long id) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = taskStatus;
    }

    public Task(String title, String description, TaskStatus status) {
        TaskManager memoryTaskManager = new InMemoryTaskManager(Managers.getDefaultHistory());

        this.title = title;
        this.description = description;
        this.id = memoryTaskManager.generateId();
        this.status = status;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Task other = (Task) obj;

        // Сравнение полей объектов
        return id == other.id;

    }

    public void setId(long id) {
        this.id = id;
    }
}
