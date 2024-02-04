package Model;

import java.util.ArrayList;
import java.util.Arrays;

public class Epic extends Task{
    ArrayList<Subtask> subtasks = new ArrayList<>();

    public Epic(String title, String description, long id, TaskStatus status, ArrayList<Subtask> subtasks) {
        super(title, description, id, status);
        this.subtasks = subtasks;
    }
}
