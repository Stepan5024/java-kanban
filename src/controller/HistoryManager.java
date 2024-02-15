package controller;

import model.Task;

public interface HistoryManager {
    void add(Task task);

    Task getHistory();
}
