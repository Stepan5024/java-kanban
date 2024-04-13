package service.impl;

import controller.history.HistoryManager;
import controller.managers.InMemoryTaskManager;
import controller.managers.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import service.GenerateIdService;
import service.TaskService;

import java.util.ArrayList;
import java.util.List;

public class TaskServiceImpl implements TaskService {

    private final GenerateIdService<Long> generateIdService;
    TaskManager taskManager;

    public TaskServiceImpl(GenerateIdService<Long> generateIdService, HistoryManager historyManager) {
        this.generateIdService = generateIdService;
        this.taskManager = new InMemoryTaskManager(historyManager);
    }

    @Override
    public Task create(Task task) throws TaskOverlapException {
        task.setId(generateIdService.generateId());

        Task overlappingTask = taskManager.checkForOverlap(task);

        if (overlappingTask == null) {
            // пересекающаяся по времени задача отсутсвует
            taskManager.addToTasksList(task);
            return task;
        } else {
            throw new TaskOverlapException(String.format("Невозможно добавить задачу %s из-за пересечения " +
                            "времени выполнения с существующей задачей %s.\\n",
                    task, overlappingTask));
        }

    }


    @Override
    public Task getTaskById(long id) throws InvalidEntityTypeException {
        Object task = taskManager.getEntityById(id);

        if (task != null && task.getClass().equals(Task.class)) {
            taskManager.addInHistoryManager((Task) task);
        } else {
            String nameClass = task == null ? "null" : String.valueOf(task.getClass());
            throw new InvalidEntityTypeException(String.format("Запрашиваемый id = %d не принадлежит Task, а является %s\n", id, nameClass));
        }

        return (Task) task;
    }


    @Override
    public List<Object> removeTaskById(long taskId) {
        List<Object> tasksToRemove = new ArrayList<>();
        Object obj = taskManager.getEntityById(taskId);

        if (obj != null) {
            tasksToRemove.add(obj);
            if (obj.getClass().equals(Epic.class)) {
                tasksToRemove.addAll(taskManager.getListOfSubtaskByEpicId(taskId));
            }

        }
        return tasksToRemove;

    }

    @Override
    public Object updateTask(Task newTask, long taskId) {
        if (!isValidUpdate(newTask, taskId)) {
            return null;
        }
        return taskManager.updateTask(newTask, taskId);
    }

    private boolean isValidUpdate(Task newTask, long taskId) {
        if (newTask instanceof Epic && !taskManager.checkIsEpic(taskId)) {
            System.out.printf("Нельзя обновить переданный id %d эпика объектом отличного от Epic класса (%s)\n", taskId);
            return false;
        } else if (newTask instanceof Subtask) {
            long epicId = ((Subtask) newTask).getEpicId();
            if (!taskManager.checkIsEpic(epicId)) {
                System.out.printf("Нельзя обновить переданный id %d подзадачи, потому что по атрибуту EpicId %d" +
                        "не нашлось объекта класса Epic\n", taskId, epicId);
                return false;
            }
        }
        return true;
    }

}
