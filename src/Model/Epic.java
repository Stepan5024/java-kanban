package Model;

import java.util.ArrayList;
import java.util.Arrays;

public class Epic extends Task{
    ArrayList<Subtask> subtasks = new ArrayList<>();

    public Epic(String title, String description, TaskStatus status, ArrayList<Subtask> subtasks) {
        super(title, description, status);
        this.subtasks = subtasks;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", subtasks=" + subtasks +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    public Epic(Task task) {
        super(task.getTitle(), task.getDescription(), task.getStatus());
    }
}
