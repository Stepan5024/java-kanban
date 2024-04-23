package model;


import manager.Managers;
import storage.managers.TaskRepository;
import storage.managers.impl.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;


public class Task {


    String title;
    String description;
    Long id;
    TaskStatus status;
    Duration duration;
    LocalDateTime startTime;

    public Task(String title, String description, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.duration = Duration.ZERO;
        this.startTime = null;
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

        this.title = title;
        this.description = description;
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

    public static boolean tasksOverlap(LocalDateTime start1, Duration duration1, LocalDateTime start2, Duration duration2) {
        if (start1 == null || start2 == null) {
            return false;
        }
        LocalDateTime end1 = start1.plus(duration1);
        LocalDateTime end2 = start2.plus(duration2);

        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    public Long getId() {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) &&
                Objects.equals(title, task.title) &&
                Objects.equals(description, task.description)
                && status == task.status &&
                Objects.equals(duration, task.duration) &&
                Objects.equals(startTime, task.startTime);
    }



    @Override
    public int hashCode() {
        return Objects.hash(title, description, id, status, duration, startTime);

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
