package controller.managers;

import controller.history.HistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.impl.InvalidEntityTypeException;
import service.impl.LongGenerateIdServiceImpl;
import service.impl.TaskOverlapException;
import service.impl.TaskServiceImpl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {


    TaskServiceImpl taskServiceImpl;
    private final List<Object> listOfAllTasks = new ArrayList<>();

    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
        taskServiceImpl = new TaskServiceImpl(new LongGenerateIdServiceImpl(), historyManager);
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    private final Set<Task> prioritizedTasks = new TreeSet<>(new TaskStartTimeComparator());
    
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    @Override
    public Task createNewTask(String title, String description, String status,
                              LocalDateTime startTime, Duration duration) {
        // ТЗ пункт 2. D Создание Задачи
        if (duration == null) {
            duration = Duration.ZERO;
        }
        Task newTask = null;
        try {
            newTask = taskServiceImpl.create(new Task(title, description,
                    TaskStatus.valueOf(status), startTime, duration));
        } catch (TaskOverlapException e) {
            System.out.printf(e.toString());
        }

        return newTask;
    }

    @Override
    public Task checkForOverlap(Task newTask) {
        // проверяет есть ли пересечение в задачах, если есть то возвращает задачу где найдено пересечение
        // Если пересечений нет, то возвращает null
        return prioritizedTasks.stream().filter(existingTask -> tasksOverlap(
                        newTask.getStartTime(), newTask.getDuration(),
                        existingTask.getStartTime(), existingTask.getDuration()))
                .findFirst()
                .orElse(null);
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
    public Epic createNewEpic(String title, String description) {
        // ТЗ пункт 2. D Создание Эпика
        Epic epic = new Epic(title, description, TaskStatus.NEW);
        addToTasksList(epic);

        return epic;
    }

    @Override
    public boolean checkIsEpic(long epicId) {
        Object currentEpic = getEntityById(epicId);
        return currentEpic != null && currentEpic.getClass().equals(Epic.class);
    }

    @Override
    public void actualizationEpicStatus(Subtask subtask) {
        Epic currentEpic = (Epic) getEntityById(subtask.getEpicId());

        if (isAllSubtasksInRequiredStatus(currentEpic.getId(), TaskStatus.DONE)) {
            // эпик получает статус DONE
            System.out.printf("Обновляем эпик с id = %d на статус DONE\n", currentEpic.getId());
            updateTask(new Epic(currentEpic.getTitle(),
                    currentEpic.getDescription(),
                    TaskStatus.DONE, currentEpic.getId()), currentEpic.getId());

        } else if (isAllSubtasksInRequiredStatus(currentEpic.getId(), TaskStatus.NEW)) {
            // эпик получает статус NEW
            System.out.printf("Обновляем эпик с id = %d на статус NEW\n", currentEpic.getId());
            updateTask(new Epic(currentEpic.getTitle(),
                    currentEpic.getDescription(),
                    TaskStatus.NEW, currentEpic.getId()), currentEpic.getId());
        } else {
            // эпик получает статус IN_PROGRESS
            System.out.printf("Обновляем эпик c id = %d на статус IN_PROGRESS\n", currentEpic.getId());
            updateTask(new Epic(currentEpic.getTitle(),
                    currentEpic.getDescription(),
                    TaskStatus.IN_PROGRESS, currentEpic.getId()), currentEpic.getId());
        }
    }

    @Override
    public void addToTasksList(Object obj) {
        if (obj == null) return;
        Task task = (Task) obj;
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }

        listOfAllTasks.add(obj);
        if (obj instanceof Subtask) {
            updateEpicTimeAndDuration(((Subtask) obj).getEpicId());
        }

    }

    @Override
    public Subtask createNewSubtask(String title, String description, String status, long epicId,
                                    LocalDateTime startTime, Duration duration) {
        // ТЗ пункт 2. D Создание Подзадачи
        if (duration == null) {
            duration = Duration.ZERO;
        }

        if (checkIsEpic(epicId)) {
            Subtask subtask = new Subtask(title, description, TaskStatus.valueOf(status), epicId, startTime, duration);
            boolean overlap = prioritizedTasks.stream().anyMatch(
                    existingTask -> tasksOverlap(subtask.getStartTime(), subtask.getDuration(),
                            existingTask.getStartTime(), existingTask.getDuration())
            );
            if (!overlap) {
                addToTasksList(subtask);
                actualizationEpicStatus(subtask);
                return subtask;
            } else {
                System.out.println("Невозможно добавить задачу из-за пересечения времени " +
                        "выполнения с существующей задачей.");
                return null;
            }

        } else {
            System.out.printf("Нельзя создать подзадачу с несуществующим Id = %d эпика. " +
                    "Проверьте Id %d переданного epic\n", epicId, epicId);
        }
        return null;
    }

    private boolean isAllSubtasksInRequiredStatus(long epicId, TaskStatus status) {
        return getListOfSubtaskByEpicId(epicId).stream()
                .allMatch(subtask -> subtask.getStatus().equals(status));
    }

    @Override
    public void updateEpicTimeAndDuration(long epicId) {
        List<Subtask> subtasks = getListOfSubtaskByEpicId(epicId);
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

        Epic epic = getEpicById(epicId);
        if (epic != null) {
            epic.setStartTime(startTime);
            epic.setEndTime(endTime);
            epic.setDuration(duration);
        }
    }

    @Override
    public List<Object> getListOfAllEntities() {
        return listOfAllTasks;
    }

    @Override
    public Object getEntityById(long id) {
        // ТЗ 2.C Получение по идентификатору задачи, эписка, подзадачи
        return getListOfAllEntities().stream()
                .filter(obj -> obj instanceof Task && ((Task) obj).getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Task getTaskById(long id) {
        try {
            return taskServiceImpl.getTaskById(id);
        } catch (InvalidEntityTypeException e) {
            System.out.printf(e.toString());
            return null;
        }
    }

    @Override
    public void addInHistoryManager(Task task) {
        historyManager.add(task);
    }


    @Override
    public Epic getEpicById(long id) {
        Object task = getEntityById(id);

        if (task != null && task.getClass().equals(Epic.class)) {
            addInHistoryManager((Task) task);
        } else {
            String nameClass = task == null ? "null" : String.valueOf(task.getClass());
            System.out.printf("Запрашиваемый id = %d не принадлежит Epic, а является %s", id, nameClass);
            return null;

        }

        return (Epic) task;
    }

    @Override
    public Subtask getSubtaskById(long id) {
        Object task = getEntityById(id);

        if (task.getClass().equals(Subtask.class)) {
            addInHistoryManager((Task) task);
        } else {
            System.out.printf("Запрашиваемый id = %d не принадлежит Subtask, а является %s", id, task.getClass());
            return null;
        }
        return (Subtask) task;
    }

    @Override
    public int removeEntityFromKanban(Class<?> aClass) {
        // ТЗ пункт 2.B Удаление всех эпиков, подзадач, тасков
        List<Long> idsToRemove = getAllEntitiesByClass(aClass).stream()
                .filter(task -> task.getClass().equals(aClass))
                .map(aClass::cast)
                .map(obj -> ((Task) obj).getId())
                .collect(Collectors.toList()); // Собираем идентификаторы для удаления

        idsToRemove.forEach(this::removeTaskById);
        return idsToRemove.size();
    }

    @Override
    public List<Object> getAllEntitiesByClass(Class<?> aClass) {
        // ТЗ 2.A Получение списка всех задач, подзадач, эпиков
        return getListOfAllEntities().stream()
                .filter(aClass::isInstance)
                .collect(Collectors.toList());
    }


    @Override
    public int removeTaskById(long taskId) {
        // ТЗ пункт 2.F Удаление по идентификатору

        List<Object> tasksToRemove = taskServiceImpl.removeTaskById(taskId);
        int countDeletedItems = tasksToRemove.size();

        tasksToRemove
                .forEach(task -> {
                    historyManager.remove(((Task) task).getId());
                    listOfAllTasks.remove(task);
                    changeEpicStatusAfterChangeSubtask(task);
                });

        return countDeletedItems;
    }


    @Override
    public List<Subtask> getListOfSubtaskByEpicId(long epicId) {
        // ТЗ пункт 3.А Получение списка всех подзадач определённого эпика.

        return getAllEntitiesByClass(Subtask.class).stream()
                .filter(subtask -> subtask instanceof Subtask && ((Subtask) subtask).getEpicId() == epicId)
                .map(subtask -> (Subtask) subtask)
                .collect(Collectors.toList());
    }

    @Override
    public Object updateTask(Object newTask, long taskId) {
        Integer index = findTaskIndex(taskId, newTask.getClass());
        if (index != null) {
            listOfAllTasks.set(index, newTask);
            return listOfAllTasks.get(index);
        }
        return null;
    }

    private Integer findTaskIndex(long taskId, Class<?> taskClass) {
        for (int i = 0; i < listOfAllTasks.size(); i++) {
            Object task = listOfAllTasks.get(i);
            if (((Task) task).getId() == taskId && task.getClass().equals(taskClass)) {
                return i;
            }
        }
        return null;
    }

    @Override
    public void changeEpicStatusAfterChangeSubtask(Object newTask) {
        if (newTask.getClass().equals(Subtask.class) && checkIsEpic(((Subtask) newTask).getEpicId())) {
            actualizationEpicStatus((Subtask) newTask);
            updateEpicTimeAndDuration(((Subtask) newTask).getEpicId());
        }
    }
}
