package service.impl;

import model.Epic;
import model.Subtask;
import model.Task;

import model.TaskStatus;
import service.AbstractTaskService;
import service.IEpicStatusUpdater;
import service.IHistoryService;
import service.ISubtaskService;
import storage.managers.TaskRepository;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static storage.managers.TaskRepository.generateId;

public class SubtaskService extends AbstractTaskService implements ISubtaskService {

    private final IHistoryService historyService;
    private final IEpicStatusUpdater epicStatusUpdater;

    public SubtaskService(TaskRepository taskRepository, IHistoryService historyService, IEpicStatusUpdater epicStatusUpdater) {
        super(taskRepository);
        this.historyService = historyService;
        this.epicStatusUpdater = epicStatusUpdater;
    }

    @Override
    public List<Task> getSubtasks() {
        System.out.println("getSubtasks");
        return taskRepository.getPrioritizedTasks().stream()
                .filter(task -> task.getClass().equals(Subtask.class))
                .collect(Collectors.toList());
    }

    @Override
    public Subtask getSubtaskById(Long id) {
        System.out.println("getSubtaskById");
        Task task = taskRepository.getEntityById(id);

        if (task != null && task.getClass().equals(Subtask.class)) {
            historyService.addTask(task);
            taskRepository.getSubtaskById(id); // сохранение в памяти
        } else {
            String nameClass = task == null ? "null" : String.valueOf(task.getClass());
            System.out.printf("Запрашиваемый id = %d не принадлежит Subtask, а является %s\n", id, nameClass);
            return null;
        }

        return (Subtask) task;
    }

    public boolean checkIsEpic(long epicId) {
        Object currentEpic = taskRepository.getEntityById(epicId);
        return currentEpic != null && currentEpic.getClass().equals(Epic.class);
    }

    @Override
    public Subtask createSubtask(Subtask task) {
        // ТЗ пункт 2. D Создание Задачи
        System.out.println("createSubtask");
        task.setId(generateId());
        Long epicId = task.getEpicId();
        if (!checkIsEpic(epicId)) {
            return null;
        }
        if (task.getDuration() == null) {
            task.setDuration(Duration.ZERO);
        }

        boolean overlap = taskRepository.getPrioritizedTasks().stream().anyMatch(
                existingTask -> Task.tasksOverlap(task.getStartTime(), task.getDuration(),
                        existingTask.getStartTime(), existingTask.getDuration())
        );
        System.out.println("\nAll subTasks " + taskRepository.getAllEntitiesByClass(Subtask.class));

        if (!overlap) {
            taskRepository.addTask(task);
            epicStatusUpdater.actualizeEpicStatus(epicId);
            epicStatusUpdater.updateEpicTimeAndDuration(epicId);
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
    public boolean isAllSubtasksInRequiredStatus(Long epicId, TaskStatus status) {
        return getSubtasksByEpicId(epicId).stream()
                .allMatch(subtask -> subtask.getStatus().equals(status));
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(Long epicId) {
        // ТЗ пункт 3.А Получение списка всех подзадач определённого эпика.
        System.out.println("getSubtasksByEpicId for Epic ID: " + epicId);
        return taskRepository.getAllEntitiesByClass(Subtask.class).stream()
                .filter(subtask -> subtask instanceof Subtask)
                .map(subtask -> (Subtask) subtask)
                .filter(subtask -> subtask.getEpicId().equals(epicId))
                .collect(Collectors.toList());
    }

    @Override
    public Subtask updateSubtask(Subtask task) {
        System.out.println("updateSubtask");

        Subtask existingTask = taskRepository.getPrioritizedTasks().stream()
                .filter(t -> t.getId().equals(task.getId()) && t.getClass().equals(Subtask.class))
                .map(t -> (Subtask) t)
                .findFirst()
                .orElse(null);

        if (existingTask != null) {

            System.out.println("Existing subtask: " + existingTask);
            System.out.println("New subtask data: " + task);
            System.out.println("Current tasks in the repository: " + taskRepository.getPrioritizedTasks());

            boolean overlap = isOverlap(task);
            if (!overlap) {
                boolean removed = taskRepository.deleteTask(existingTask);
                if (removed) {

                    taskRepository.addTask(task);
                    System.out.println("Subtask updated successfully.");
                    if (task.getEpicId() != null) {
                        Long epicId = existingTask.getEpicId();
                        epicStatusUpdater.actualizeEpicStatus(epicId);
                        epicStatusUpdater.updateEpicTimeAndDuration(epicId);
                    }
                    return task;
                } else {
                    System.out.println("Failed to remove the existing task.");
                    return null;
                }
            }
            System.out.println("Operation aborted due to overlapping task.");

        }
        System.out.println("No subtask found with the provided ID.");
        return null;
    }

    @Override
    public boolean deleteSubtask(Long id) {
        // ТЗ пункт 2.F Удаление по идентификатору
        System.out.println("deleteSubtask");
        Subtask task = (Subtask) taskRepository.getEntityById(id);
        if (task != null && task.getClass().equals(Subtask.class)) {

            Long epicId = task.getEpicId();
            epicStatusUpdater.actualizeEpicStatus(epicId);
            epicStatusUpdater.updateEpicTimeAndDuration(epicId);

            return taskRepository.deleteTask(task);
        } else {
            return false;
        }

    }

}
