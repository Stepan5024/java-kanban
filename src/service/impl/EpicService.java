package service.impl;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.*;
import storage.managers.TaskRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static storage.managers.TaskRepository.generateId;

public class EpicService extends AbstractTaskService implements IEpicService, IEpicStatusUpdater {
    private final IHistoryService historyService;
    private ISubtaskService subtaskService;

    public EpicService(TaskRepository taskRepository, IHistoryService historyService) {
        super(taskRepository);
        this.historyService = historyService;
    }

    @Override
    public void setSubtaskService(ISubtaskService subtaskService) {
        this.subtaskService = subtaskService;
    }

    @Override
    public ISubtaskService getSubtaskService() {
        return subtaskService;
    }

    @Override
    public List<Task> getEpics() {
        System.out.println("getEpics");
        return taskRepository.getPrioritizedTasks().stream()
                .filter(task -> task.getClass().equals(Epic.class))
                .collect(Collectors.toList());
    }

    @Override
    public Epic getEpicById(Long id) {
        Epic epic = (Epic) taskRepository.getEntityById(id);

        if (epic != null && epic.getClass().equals(Epic.class)) {
            historyService.addTask(epic);
            taskRepository.getEpicById(id); // сохранение просмотра
        } else {
            String nameClass = epic == null ? "null" : String.valueOf(epic.getClass());
            System.out.printf("Запрашиваемый id = %d не принадлежит Task, а является %s\n", id, nameClass);
            return null;
        }

        return epic;
    }

    @Override
    public Epic createEpic(Epic task) {
        // ТЗ пункт 2. D Создание Задачи
        System.out.println("createEpic");
        task.setId(generateId());

        //System.out.println(task);
        taskRepository.addTask(task);
        return task;
    }

    @Override
    public Epic updateEpic(Epic task) {
        System.out.println("updateEpic");
        // Retrieve the task from the set if it exists
        Task existingTask = taskRepository.getPrioritizedTasks().stream()
                .filter(t -> t.getId().equals(task.getId()) && t.getClass().equals(Epic.class))
                .findFirst()
                .orElse(null);
        System.out.println("updateEpic existingTask = " + existingTask);
        System.out.println("updateEpic newTask = " + task);
        assert existingTask != null;
        if (existingTask.equals(task)) return task;

        if (existingTask != null) {


            // Remove the old version of the task
            boolean removed = taskRepository.deleteTask(existingTask);
            System.out.println("existingTask " + existingTask);
            System.out.println("task = " + task);
            if (removed) {
                System.out.println("Задача удалена");
                if (taskRepository.addTask(task)) {
                    System.out.println("Задача добавлена после удаления");
                    return task;
                } else {
                    System.out.println("Задача НЕ добавлена после удаления");
                    return null;
                }

            }

        }
        // Task not found, cannot update
        return null;

    }

    @Override
    public boolean deleteEpic(Long id) {
        // ТЗ пункт 2.F Удаление по идентификатору
        System.out.println("deleteEpic");
        Task task = taskRepository.getEntityById(id);


        if (task != null && task.getClass().equals(Epic.class)) {
            taskRepository.deleteListOfTask(new ArrayList<>(subtaskService.getSubtasksByEpicId(id)));
            return taskRepository.deleteTask(task);
        } else {
            return false; // Task either not found or not of type Task
        }

    }


    @Override
    public void actualizeEpicStatus(Long epicId) {
        System.out.println("actualizeEpicStatus");
        Epic currentEpic = (Epic) taskRepository.getEntityById(epicId);
        if (currentEpic == null) return;

        System.out.println("Current Epic Status: " + currentEpic.getStatus());
        System.out.println(subtaskService.getSubtasksByEpicId(epicId));


        List<Subtask> subtasks = subtaskService.getSubtasksByEpicId(epicId);
        if (!subtasks.isEmpty()) {
            boolean allDone = subtaskService.isAllSubtasksInRequiredStatus(currentEpic.getId(), TaskStatus.DONE);
            boolean allNew = subtaskService.isAllSubtasksInRequiredStatus(currentEpic.getId(), TaskStatus.NEW);

            System.out.println("All Subtasks DONE: " + allDone);
            System.out.println("All Subtasks NEW: " + allNew);

            if (allDone) {
                currentEpic.setStatus(TaskStatus.DONE);
            } else if (allNew) {
                currentEpic.setStatus(TaskStatus.NEW);
            } else {
                currentEpic.setStatus(TaskStatus.IN_PROGRESS);
            }
        } else {
            System.out.println("No subtasks found for Epic ID: " + epicId);
            currentEpic.setStatus(TaskStatus.NEW);
        }

        updateEpic(currentEpic);
    }


    @Override
    public void updateEpicTimeAndDuration(Long epicId) {
        System.out.println("updateEpicTimeAndDuration");
        List<Subtask> subtasks = subtaskService.getSubtasksByEpicId(epicId);
        if (subtasks.isEmpty()) {
            return;
        }

        LocalDateTime startTime = subtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime endTime = subtasks.stream()
                .map(Task::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        Duration duration = subtasks.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration::plus)
                .orElse(Duration.ZERO);

        Epic epic = (Epic) taskRepository.getEntityById(epicId);
        if (epic != null) {
            System.out.printf("startTime %s endTime %s duration %s\n", startTime, endTime, duration);
            epic.setStartTime(startTime);
            epic.setEndTime(endTime);
            epic.setDuration(duration);
        }
    }

}
