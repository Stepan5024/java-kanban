package storage.managers.impl;



import model.Task;
import service.impl.LongGenerateIdServiceImpl;
import storage.history.HistoryRepository;
import storage.managers.TaskRepository;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskRepository {

    private final HistoryRepository historyManager;
    private final Set<Task> prioritizedTasks = new TreeSet<>(new TaskStartTimeComparator());



    public InMemoryTaskManager(HistoryRepository historyManager) {
        this.historyManager = historyManager;

    }

    @Override
    public HistoryRepository getHistoryManager() {
        return historyManager;
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    @Override
    public Task getTaskById(long id) {
        return null;
    }

    @Override
    public List<Task> getListOfAllEntities() {
        return prioritizedTasks.stream().collect(Collectors.toList());
    }

    @Override
    public Task getEntityById(long id) {
        System.out.println("prioritizedTasks = " + prioritizedTasks);
        // ТЗ 2.C Получение по идентификатору задачи, эпика, подзадачи
        return prioritizedTasks.stream()
                .filter(obj -> obj != null && ((Task) obj).getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void addTask(Task task) {
        prioritizedTasks.add(task);
    }

    @Override
    public boolean updateTask(Task task) {
        return false;
    }

    @Override
    public boolean deleteTask(Task task) {
        return prioritizedTasks.remove(task);
    }

    @Override
    public List<Task> getHistory() {
        return List.of();
    }

    @Override
    public void clear() {

    }
}
