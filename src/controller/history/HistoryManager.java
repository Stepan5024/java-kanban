package controller.history;

import model.Task;

import java.util.ArrayList;

public interface HistoryManager {
    void add(Task task);

    void remove(long id);

    ArrayList<Task> getHistory();
}
