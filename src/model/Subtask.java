package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    long epicId;

    public Subtask(String title, String description, TaskStatus status, long epicId,
                   LocalDateTime startTime, Duration duration) {
        super(title, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, TaskStatus status, long epicId, long id,
                   LocalDateTime startTime, Duration duration) {
        super(title, description, status, id, startTime, duration);
        this.epicId = epicId;
    }

    public Long getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", epicId=" + epicId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }

}
