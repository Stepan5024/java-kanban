package repository.impl;

import model.Task;
import repository.TaskRepository;

import java.util.ArrayList;
import java.util.List;

public class TaskRepositoryImpl implements TaskRepository {
    private final List<Object> listOfAllTasks = new ArrayList<>();

    @Override
    public void add(Object task) {
        listOfAllTasks.add(task);
    }

    @Override
    public List<Object> getTasks() {
        return listOfAllTasks;
    }

    @Override
    public Object updateTask(Task newTask, int id) {
        listOfAllTasks.add(id, newTask);
        return newTask;
    }


}
