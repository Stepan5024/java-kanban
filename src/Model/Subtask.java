package Model;

public class Subtask extends Task {
    long epicId;

    public Subtask(String title, String description, long id, TaskStatus status, long epicId) {
        super(title, description, id, status);
        this.epicId = epicId;
    }
}
