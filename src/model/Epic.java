package model;


import java.time.LocalDateTime;

public class Epic extends Task {

    private LocalDateTime endTime; // Время завершения эпика

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

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
                ", endTime=" + endTime +
                '}';
    }

}
