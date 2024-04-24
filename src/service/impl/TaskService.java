package service.impl;


import model.Task;
import service.AbstractTaskService;
import service.IHistoryService;
import service.ITaskService;
import storage.managers.TaskRepository;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static storage.managers.TaskRepository.generateId;

public class TaskService extends AbstractTaskService implements ITaskService {

    private final IHistoryService historyService;

    public TaskService(TaskRepository taskRepository, IHistoryService historyService) {
        super(taskRepository);
        this.historyService = historyService;
    }

    @Override
    public List<Task> getTasks() {
        return taskRepository.getPrioritizedTasks().stream()
                .filter(task -> task.getClass().equals(Task.class))
                .collect(Collectors.toList());
    }

    @Override
    public Task getTaskById(Long id) {
        Task task = taskRepository.getTaskById(id);

        if (task != null && task.getClass().equals(Task.class)) {
            historyService.addTask(task);
            taskRepository.getTaskById(id); // сохранение истории
        } else {
            String nameClass = task == null ? "null" : String.valueOf(task.getClass());
            System.out.printf("Запрашиваемый id = %d не принадлежит Task, а является %s\n", id, nameClass);
            return null;
        }

        return task;
    }

    @Override
    public Task createTask(Task task) {
        // ТЗ пункт 2. D Создание Задачи
        if (task.getDuration() == null) {
            task.setDuration(Duration.ZERO);
        }
        task.setId(generateId());

        //System.out.println(task);
        boolean overlap = isOverlap(task);
        if (!overlap) {
            taskRepository.addTask(task);
            return task;
        } else {
            Task newTask = taskRepository.getPrioritizedTasks().stream()
                    .filter(existingTask -> Task.tasksOverlap(
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
    public Task updateTask(Task task) {
        // Retrieve the task from the set if it exists
        Task existingTask = taskRepository.getPrioritizedTasks().stream()
                .filter(t -> t.getId().equals(task.getId()) && t.getClass().equals(Task.class))
                .findFirst()
                .orElse(null);

        if (existingTask != null) {

            // check do not overlap
            boolean overlap = isOverlap(task);
            if (!overlap) {
                // Remove the old version of the task

                boolean removed = taskRepository.deleteTask(existingTask);

                if (removed) {

                    taskRepository.addTask(task); // Add the updated task
                    // Return the updated task
                    return task;
                }
            }

        }
        // Task not found, cannot update
        return null;

    }

    @Override
    public boolean deleteTask(Long id) {
        // ТЗ пункт 2.F Удаление по идентификатору
        Task task = taskRepository.getEntityById(id);

        if (task != null && task.getClass().equals(Task.class)) {
            return taskRepository.deleteTask(task);
        } else {
            return false;
        }

    }

    @Override
    public boolean deleteAllTasks() {

        List<Task> tasks = taskRepository.getAllEntitiesByClass(Task.class);
        tasks.forEach(task -> taskRepository.deleteTask(task));
        return taskRepository.getAllEntitiesByClass(Task.class).isEmpty();
    }
}
