package storage.history;

import model.Task;

import java.util.List;

public interface HistoryRepository {

    void addTask(Task task);

    void removeTask(long id);

    List<Task> getHistory();
}
