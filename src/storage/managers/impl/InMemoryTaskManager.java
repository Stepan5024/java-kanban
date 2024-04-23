package storage.managers.impl;


import model.Epic;
import model.Subtask;
import model.Task;
import storage.history.HistoryRepository;
import storage.managers.TaskRepository;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskRepository {
    protected static HistoryRepository historyRepository;

    public InMemoryTaskManager(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    private final Set<Task> prioritizedTasks = new TreeSet<>(new TaskStartTimeComparator());


    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }


    @Override
    public List<Task> getListOfAllEntities() {
        return prioritizedTasks.stream().collect(Collectors.toList());
    }

    @Override
    public Task getTaskById(Long id) {
        return getEntityById(id);
    }

    @Override
    public Subtask getSubtaskById(Long id) {
        return (Subtask) getEntityById(id);
    }

    @Override
    public Epic getEpicById(Long id) {
        return (Epic) getEntityById(id);
    }

    @Override
    public Task getEntityById(Long id) {

        // ТЗ 2.C Получение по идентификатору задачи, эпика, подзадачи
        return prioritizedTasks.stream()
                .filter(obj -> obj != null && ((Task) obj).getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean addTask(Task task) {
        return prioritizedTasks.add(task);
    }


    @Override
    public boolean deleteTask(Task task) {
        return prioritizedTasks.remove(task);
    }

    @Override
    public int deleteListOfTask(List<Task> list) {
        long initialCount = list.size();

        list.forEach(this::deleteTask); // deleteTask(task) needs to handle the deletion internally.

        long deletedCount = initialCount - list.size();
        System.out.println("After deleted size " + deletedCount);
        return (int) deletedCount;
    }


    @Override
    public List<Task> getAllEntitiesByClass(Class<?> aClass) {
        // ТЗ 2.A Получение списка всех задач, подзадач, эпиков
        return getListOfAllEntities().stream()
                .filter(aClass::isInstance)
                .collect(Collectors.toList());
    }
}
