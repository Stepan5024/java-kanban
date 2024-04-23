package service;

import model.Task;

import java.util.List;

public interface IHistoryService {

    void addTask(Task task);

    List<Task> getHistory();

    boolean removeTask(Long id);

}
