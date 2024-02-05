package Model;

public class Subtask extends Task {
    long epicId;

    public Subtask(String title, String description, TaskStatus status, long epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public long getEpicId() {
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
                '}';
    }

    public Subtask(Task task, long epicId) {
        super(task.getTitle(), task.getDescription(), task.getStatus());
        this.epicId = epicId;
    }
}
