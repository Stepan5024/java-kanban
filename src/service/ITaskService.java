package service;

import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public interface ITaskService {
    /**
     * Retrieves all tasks.
     *
     * @return a list of tasks
     */
    List<Task> getTasks();

    /**
     * Retrieves a task by its ID.
     *
     * @param id the ID of the task
     * @return the task or null if not found
     */
    Task getTaskById(Long id);

    /**
     * Creates a new task.
     *
     * @param task the task to create
     * @return the created task with its new ID
     */
    Task createTask(Task task);



    boolean tasksOverlap(LocalDateTime start1, Duration duration1, LocalDateTime start2, Duration duration2);

    /**
     * Updates an existing task.
     *
     * @param task the task with updated information
     * @return the updated task or null if the task does not exist
     */
    Task updateTask(Task task);

    /**
     * Deletes a task by its ID.
     *
     * @param id the ID of the task to delete
     */

    boolean deleteTask(Long id);
}

