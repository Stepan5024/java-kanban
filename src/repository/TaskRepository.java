package repository;

import model.Task;

import java.util.List;

public interface TaskRepository {

    void add(Object task);

    List<Object> getTasks();

    Object updateTask(Task newTask, int id);
}
