package storage.managers.impl;



import model.Task;
import storage.history.HistoryRepository;
import storage.managers.TaskRepository;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskRepository {


    private final Set<Task> prioritizedTasks = new TreeSet<>(new TaskStartTimeComparator());



    public InMemoryTaskManager() {


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
    public boolean updateTask(Task task) {
        return false;
    }

    @Override
    public boolean deleteTask(Task task) {
        return prioritizedTasks.remove(task);
    }

    @Override
    public int deleteListOfTask(List<Task> list) {
        long initialCount = list.size();

        list.forEach(this::deleteTask); // deleteTask(task) needs to handle the deletion internally.

        // Calculate how many tasks were deleted by comparing the sizes.
        long deletedCount = initialCount - list.size();
        System.out.println("After deleted size " + deletedCount);
        return (int) deletedCount;
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
        // ТЗ 2.A Получение списка всех задач, подзадач, эпиков
        return getListOfAllEntities().stream()
                .filter(aClass::isInstance)
                .collect(Collectors.toList());
    }
}
