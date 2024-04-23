package service.impl;

import model.Epic;
import model.Subtask;
import model.Task;

import service.AbstractTaskService;
import service.IHistoryService;
import service.ISubtaskService;
import storage.managers.TaskRepository;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static storage.managers.TaskRepository.generateId;

public class SubtaskService extends AbstractTaskService implements ISubtaskService {

    private final IHistoryService historyService;

    public SubtaskService(TaskRepository taskRepository, IHistoryService historyService) {
        super(taskRepository);
        this.historyService = historyService;
    }

    @Override
    public List<Task> getSubtasks() {
        return taskRepository.getPrioritizedTasks().stream()
                .filter(task -> task.getClass().equals(Subtask.class))
                .collect(Collectors.toList());
    }

    @Override
    public Subtask getSubtaskById(Long id) {
        Subtask task = (Subtask) taskRepository.getEntityById(id);

        if (task != null && task.getClass().equals(Subtask.class)) {
            historyService.addTask(task);
        } else {
            String nameClass = task == null ? "null" : String.valueOf(task.getClass());
            System.out.printf("Запрашиваемый id = %d не принадлежит Subtask, а является %s\n", id, nameClass);
            return null;
        }

        return task;
    }

    public boolean checkIsEpic(long epicId) {
        Object currentEpic = taskRepository.getEntityById(epicId);
        return currentEpic != null && currentEpic.getClass().equals(Epic.class);
    }

    @Override
    public Subtask createSubtask(Subtask task) {
        // ТЗ пункт 2. D Создание Задачи
        if (!checkIsEpic(task.getEpicId())) {
            return null;
        }
        if (task.getDuration() == null) {
            task.setDuration(Duration.ZERO);
        }
        task.setId(generateId());

        System.out.println(task);
        boolean overlap = taskRepository.getPrioritizedTasks().stream().anyMatch(
                existingTask -> Task.tasksOverlap(task.getStartTime(), task.getDuration(),
                        existingTask.getStartTime(), existingTask.getDuration())
        );
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
    public Subtask updateSubtask(Subtask task) {
        // Retrieve the task from the set if it exists
        Subtask existingTask = (Subtask) taskRepository.getPrioritizedTasks().stream()
                .filter(t -> t.getId().equals(task.getId()) && t.getClass().equals(Subtask.class))
                .findFirst()
                .orElse(null);

        if (existingTask != null) {
            // Remove the old version of the task
            boolean removed = taskRepository.getPrioritizedTasks().remove(existingTask);
            boolean overlap = isOverlap(task);
            if (!overlap) {
                if (removed) {

                    taskRepository.addTask(task); // Add the updated task
                    historyService.addTask(task); // Record the task update in history
                    // Return the updated task
                    return task;
                } else {
                    // Removal failed, handle accordingly, maybe throw an exception or return null
                    return null;
                }
            }
            System.out.println("overlap in update Subtask = " + overlap);
        }
        return null;
    }

    @Override
    public boolean deleteSubtask(Long id) {
        // ТЗ пункт 2.F Удаление по идентификатору

        Subtask task = (Subtask) taskRepository.getEntityById(id);
        System.out.println("task = " + task);
        System.out.println("class = " + task.getClass());
        if (task != null && task.getClass().equals(Subtask.class)) {
            return taskRepository.deleteTask(task);
        } else {
            return false; // Task either not found or not of type Task
        }

    }

}
