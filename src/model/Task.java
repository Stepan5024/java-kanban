package model;

import controller.managers.InMemoryTaskManager;
import controller.managers.TaskManager;

import manager.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;


public class Task {


    String title;
    String description;
    long id;
    TaskStatus status;
    Duration duration;
    LocalDateTime startTime;

    public Task(String title, String description, TaskStatus status) {
        TaskManager memoryTaskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
        this.title = title;
        this.description = description;
        this.status = status;
        this.duration = Duration.ZERO;
        this.startTime = null;
        this.id = memoryTaskManager.generateId();
    }

    public Task(String title, String description, TaskStatus taskStatus, long id,
                LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = taskStatus;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String title, String description, TaskStatus status,
                LocalDateTime startTime, Duration duration) {
        TaskManager memoryTaskManager = new InMemoryTaskManager(Managers.getDefaultHistory());

        this.title = title;
        this.description = description;
        this.id = memoryTaskManager.generateId();
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String title, String description, TaskStatus taskStatus, long id) {
        this.title = title;
        this.description = description;
        this.status = taskStatus;
        this.id = id;
        this.duration = Duration.ZERO;
        this.startTime = null;
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
                ", startTime=" + startTime +
                ", duration=" + duration +
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

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void setId(long id) {
        this.id = id;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plus(duration);
    }
}
