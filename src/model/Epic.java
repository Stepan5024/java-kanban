package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Epic extends Task {

    public Epic(String title, String description, TaskStatus taskStatus) {
        super(title, description, taskStatus);
    }

    public Epic(String title, String description, TaskStatus taskStatus, long id) {
        super(title, description, taskStatus, id);
    }


    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

}
