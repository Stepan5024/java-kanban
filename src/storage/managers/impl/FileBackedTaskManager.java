package storage.managers.impl;

import model.Task;
import storage.history.HistoryRepository;
import storage.managers.TaskRepository;

import java.util.List;
import java.util.Set;

public class FileBackedTaskManager implements TaskRepository {


    @Override
    public Set<Task> getPrioritizedTasks() {
        return Set.of();
    }

    @Override
    public Task getTaskById(long id) {
        return null;
    }

    @Override
    public List<Task> getListOfAllEntities() {
        return List.of();
    }

    @Override
    public Task getEntityById(long id) {
        return null;
    }

    @Override
    public boolean addTask(Task task) {
        return false;
    }

    @Override
    public boolean updateTask(Task task) {
        return false;
    }

    @Override
    public boolean deleteTask(Task task) {
        return false;
    }

    @Override
    public int deleteListOfTask(List<Task> list) {
        return 0;
    }


    @Override
    public List<Task> getHistory() {
        return List.of();
    }

    @Override
    public void clear() {

    }

    @Override
    public List<Task> getAllEntitiesByClass(Class<?> aClass) {
        return null;
    }
}
