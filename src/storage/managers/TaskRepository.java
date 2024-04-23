package storage.managers;


import model.Epic;
import model.Subtask;
import model.Task;
import service.impl.LongGenerateIdServiceImpl;
import storage.history.HistoryRepository;

import java.util.List;
import java.util.Set;

public interface TaskRepository {

    LongGenerateIdServiceImpl generateId = new LongGenerateIdServiceImpl();

    /**
     * Generates and returns a unique identifier for a new task.
     *
     * @return a unique task ID
     */
    static Long generateId() {
        return generateId.generateId();
    }

    static Long getId() {
        return generateId.getId();
    }

    /**
     * Retrieves all tasks in a prioritized order.
     *
     * @return a sorted set of prioritized tasks
     */
    Set<Task> getPrioritizedTasks();

    List<Task> getListOfAllEntities();

    Task getTaskById(Long id);
    Subtask getSubtaskById(Long id);
    Epic getEpicById(Long id);

    Task getEntityById(Long id);

    /**
     * Adds a new task to the repository.
     *
     * @param task the task to add
     */
    boolean addTask(Task task);
    /**
     * Deletes a task from the repository by its ID.
     *
     * @param task the ID of the task to delete
     * @return true if the task was deleted, false if not found
     */
    boolean deleteTask(Task task);

    int deleteListOfTask(List<Task> list);

    List<Task> getAllEntitiesByClass(Class<?> aClass);
}
