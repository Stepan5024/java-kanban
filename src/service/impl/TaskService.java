package service.impl;


import model.Epic;
import model.Task;
import service.ITaskService;
import storage.history.HistoryRepository;
import storage.managers.TaskRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static storage.managers.TaskRepository.generateId;

public class TaskService implements ITaskService {

    private final TaskRepository taskManager;
    private final HistoryRepository historyManager;

    public TaskService(TaskRepository taskManager) {
        this.taskManager = taskManager;
        this.historyManager = taskManager.getHistoryManager();
    }

    @Override
    public List<Task> getTasks() {
        return taskManager.getPrioritizedTasks().stream().collect(Collectors.toList());
    }

    @Override
    public Task getTaskById(Long id) {
        Object task = taskManager.getEntityById(id);

        if (task != null && task.getClass().equals(Task.class)) {
            historyManager.addTask((Task) task);
        } else {
            String nameClass = task == null ? "null" : String.valueOf(task.getClass());
            System.out.printf("Запрашиваемый id = %d не принадлежит Task, а является %s\n", id, nameClass);
            return null;
        }

        return (Task) task;
    }

    @Override
    public Task createTask(Task task) {
        // ТЗ пункт 2. D Создание Задачи
        if (task.getDuration() == null) {
            task.setDuration(Duration.ZERO);
        }
        task.setId(generateId());

        System.out.println(task);
        boolean overlap = taskManager.getPrioritizedTasks().stream().anyMatch(
                existingTask -> tasksOverlap(task.getStartTime(), task.getDuration(),
                        existingTask.getStartTime(), existingTask.getDuration())
        );
        if (!overlap) {
            taskManager.addTask(task);
            return task;
        } else {
            Task newTask = taskManager.getPrioritizedTasks().stream()
                    .filter(existingTask -> tasksOverlap(
                            task.getStartTime(), task.getDuration(),
                            existingTask.getStartTime(), existingTask.getDuration()))
                    .findFirst()
                    .orElse(null);
            System.out.printf("Невозможно добавить задачу %s из-за пересечения времени выполнения" +
                    " с существующей задачей %s.\n", newTask, task);
            return null;
        }
    }

    @Override
    public boolean tasksOverlap(LocalDateTime start1, Duration duration1, LocalDateTime start2, Duration duration2) {
        if (start1 == null || start2 == null) {
            return false;
        }
        LocalDateTime end1 = start1.plus(duration1);
        LocalDateTime end2 = start2.plus(duration2);

        return start1.isBefore(end2) && start2.isBefore(end1);
    }


    @Override
    public Task updateTask(Task task) {
        // Retrieve the task from the set if it exists
        Task existingTask = taskManager.getPrioritizedTasks().stream()
                .filter(t -> t.getId().equals(task.getId()))
                .findFirst()
                .orElse(null);

        if (existingTask != null) {
            // Remove the old version of the task
            boolean removed = taskManager.getPrioritizedTasks().remove(existingTask);

            if (removed) {

                taskManager.addTask(task); // Add the updated task
                historyManager.addTask(task); // Record the task update in history
                // Return the updated task
                return task;
            } else {
                // Removal failed, handle accordingly, maybe throw an exception or return null
                return null;
            }
        } else {
            // Task not found, cannot update
            return null;
        }
    }

    @Override
    public boolean deleteTask(Long id) {
        // ТЗ пункт 2.F Удаление по идентификатору

        Task task = taskManager.getEntityById(id);
        System.out.println("task = " + task);
        System.out.println("class = " + task.getClass());
        if (task != null && task.getClass().equals(Task.class)) {
            return taskManager.deleteTask(task);
        } else {
            return false; // Task either not found or not of type Task
        }

    }
}
