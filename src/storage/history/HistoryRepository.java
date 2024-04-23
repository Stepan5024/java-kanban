package storage.history;

import model.Task;

import java.util.List;

public interface HistoryRepository {

    void addTask(Task task);

    boolean removeTask(Long id);

    List<Task> getHistory();
}
