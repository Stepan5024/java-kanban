package storage.managers;


import model.Task;
import service.impl.LongGenerateIdServiceImpl;
import storage.history.HistoryRepository;

import java.util.List;
import java.util.Set;

public interface TaskRepository {

    LongGenerateIdServiceImpl generateId = new LongGenerateIdServiceImpl();

    HistoryRepository getHistoryManager();

    /**
     * Generates and returns a unique identifier for a new task.
     *
     * @return a unique task ID
     */
    static Long generateId() {
        return generateId.generateId();
    }

    ;


    static Long getId() {
        return generateId.getId();
    }

    /**
     * Retrieves all tasks in a prioritized order.
     *
     * @return a sorted set of prioritized tasks
     */
    Set<Task> getPrioritizedTasks();

    /**
     * Retrieves a task by its ID.
     *
     * @param id the ID of the task
     * @return the task with the given ID or null if not found
     */
    Task getTaskById(long id);

    List<Task> getListOfAllEntities();

    Task getEntityById(long id);

    /**
     * Adds a new task to the repository.
     *
     * @param task the task to add
     */
    void addTask(Task task);

    /**
     * Updates an existing task in the repository.
     *
     * @param task the task to update
     * @return true if the task was updated, false if not found
     */
    boolean updateTask(Task task);

    /**
     * Deletes a task from the repository by its ID.
     *
     * @param task the ID of the task to delete
     * @return true if the task was deleted, false if not found
     */
    boolean deleteTask(Task task);

    /**
     * Retrieves the entire history of tasks accessed.
     *
     * @return a list representing the history of accessed tasks
     */
    List<Task> getHistory();

    /**
     * Clears all tasks from the repository.
     */
    void clear();
}
