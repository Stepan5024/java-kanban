package controller.managers;

import controller.history.HistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {

    private static long taskId;
    private final ArrayList<Object> listOfAllTasks = new ArrayList<>();
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public long generateId() {
        return taskId++;
    }

    public static long getId() {
        return taskId;
    }

    @Override
    public Task createNewTask(String title, String description, String status) {
        // ТЗ пункт 2. D Создание Задачи
        Task task = new Task(title,
                description,
                TaskStatus.valueOf(status));
        addToTasksList(task);
        return task;
    }

    @Override
    public Epic createNewEpic(String title, String description) {
        // ТЗ пункт 2. D Создание Эпика
        Epic epic = new Epic(title, description, TaskStatus.NEW);
        addToTasksList(epic);

        return epic;
    }

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
        listOfAllTasks.add(obj);
    }

    @Override
    public Subtask createNewSubtask(String title, String description, String status, long epicId) {
        // ТЗ пункт 2. D Создание Подзадачи
        if (checkIsEpic(epicId)) {
            Subtask subtask = new Subtask(title, description, TaskStatus.valueOf(status), epicId);
            addToTasksList(subtask);
            actualizationEpicStatus(subtask);
            return subtask;
        } else {
            System.out.printf("Нельзя создать подзадачу с несуществующим Id = %d эпика. " +
                    "Проверьте Id %d переданного epic\n", epicId, epicId);
        }
        return null;
    }

    private boolean isAllSubtasksInRequiredStatus(long epicId, TaskStatus status) {
        for (Subtask subtask : getListOfSubtaskByEpicId(epicId)) {
            if (!subtask.getStatus().equals(status)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ArrayList<Object> getListOfAllEntities() {
        return listOfAllTasks;
    }

    @Override
    public Object getEntityById(long id) {
        // ТЗ 2.C Получение по идентификатору задачи, эписка, подзадачи
        Object target = null;
        for (Object obj : getListOfAllEntities()) {
            if (obj.getClass().equals(Subtask.class) && ((Subtask) obj).getId() == id) {
                target = obj;
            } else if (obj.getClass().equals(Task.class) && ((Task) obj).getId() == id) {
                target = obj;
            } else if (obj.getClass().equals(Epic.class) && ((Epic) obj).getId() == id) {
                target = obj;
            }
        }
        return target;
    }

    @Override
    public Task getTaskById(long id) {
        Object task = getEntityById(id);

        if (task.getClass().equals(Task.class)) {
            historyManager.add((Task) task);
        } else {
            System.out.printf("Запрашиваемый id = %d не принадлежит Task, а является %s", id, task.getClass());
            return null;
        }

        return (Task) task;
    }

    @Override
    public Epic getEpicById(long id) {
        Object task = getEntityById(id);

        if (task.getClass().equals(Epic.class)) {
            historyManager.add((Task) task);
        } else {
            System.out.printf("Запрашиваемый id = %d не принадлежит Epic, а является %s", id, task.getClass());
            return null;
        }

        return (Epic) task;
    }

    @Override
    public Subtask getSubtaskById(long id) {
        Object task = getEntityById(id);

        if (task.getClass().equals(Subtask.class)) {
            historyManager.add((Task) task);
        } else {
            System.out.printf("Запрашиваемый id = %d не принадлежит Subtask, а является %s", id, task.getClass());
            return null;
        }
        return (Subtask) task;
    }

    @Override
    public int removeEntityFromKanban(Class<?> aClass) {
        // ТЗ пункт 2.B Удаление всех эпиков, подзадач, тасков
        int countDeletedItems = 0;

        for (Object task : getAllEntitiesByClass(aClass)) {
            if (aClass.isInstance(task)) {
                countDeletedItems += removeTaskById(((Task) task).getId());
            }
        }

        return countDeletedItems;
    }

    @Override
    public ArrayList<Object> getAllEntitiesByClass(Class<?> aClass) {
        // ТЗ 2.A Получение списка всех задач, подзадач, эпиков
        ArrayList<Object> entities = new ArrayList<>();
        for (Object obj : getListOfAllEntities()) {
            if (obj.getClass().equals(aClass)) {
                entities.add(obj);
            }
        }
        return entities;
    }


    @Override
    public int removeTaskById(long taskId) {
        // ТЗ пункт 2.F Удаление по идентификатору
        List<Object> tasksToRemove = new ArrayList<>();
        Object obj = getEntityById(taskId);

        if (obj != null) {
            tasksToRemove.add(obj);
            if (obj.getClass().equals(Epic.class)) {
                tasksToRemove.addAll(getListOfSubtaskByEpicId(taskId));
            }
            changeEpicStatusAfterChangeSubtask(obj);
        }

        int countDeletedItems = tasksToRemove.size();

        for (Object task : tasksToRemove) {
            historyManager.remove(((Task) task).getId());
            listOfAllTasks.remove(task);
        }

        return countDeletedItems;
    }


    @Override
    public ArrayList<Subtask> getListOfSubtaskByEpicId(long epicId) {
        // ТЗ пункт 3.А Получение списка всех подзадач определённого эпика.

        ArrayList<Subtask> listOfSubtasks = new ArrayList<>();

        for (Object subtask : getAllEntitiesByClass(Subtask.class)) {

            if (((Subtask) subtask).getEpicId() == epicId) {
                listOfSubtasks.add((Subtask) subtask);
            }
        }
        return listOfSubtasks;
    }

    @Override
    public Object updateTask(Object newTask, long taskId) {
        // taskId - id задачи которую хотим заменить
        // ТЗ 2. E Обновление объекта новой версией.
        // логика работы
        // 1. Находим индекс таски в списке
        // 2. Удаляем элемент по индексу
        // 3. Вставляем новый объект по индексу
        // 4. Возвращаем вставленную задачу

        int index = -1;

        for (int i = 0; i < getListOfAllEntities().size(); i++) {
            Object obj = getListOfAllEntities().get(i);
            if (obj.getClass().equals(Subtask.class)
                    && ((Subtask) obj).getId() == taskId
                    && (newTask.getClass().equals(Subtask.class))
                    && ((Subtask) newTask).getId() == taskId) {
                index = i;
            } else if (obj.getClass().equals(Task.class)
                    && ((Task) obj).getId() == taskId
                    && (newTask.getClass().equals(Task.class))
                    && ((Task) newTask).getId() == taskId) {
                index = i;
            } else if (obj.getClass().equals(Epic.class)
                    && ((Epic) obj).getId() == taskId
                    && (newTask.getClass().equals(Epic.class))
                    && ((Epic) newTask).getId() == taskId) {
                index = i;
            }
        }
        if (index != -1) {
            if (checkIsEpic(taskId)) {
                // пытаемся обновить по id эпика не объект эпика
                if (!newTask.getClass().equals(Epic.class)) {
                    System.out.printf("Нельзя обновить переданный id %d эпика объектом отличного от Epic класса (%s)\n",
                            taskId, newTask.getClass());
                    return null;
                }
            } else if (newTask.getClass().equals(Subtask.class) && !checkIsEpic(((Subtask) newTask).getEpicId())) {
                // поптка обновить подзадачу с id epic который не принадлежит epic
                System.out.printf("Нельзя обновить переданный id %d подзадачи, потому что по атрибуту EpicId" +
                                " не нашлось объекта класса Epic\n",
                        taskId);
                return null;
            }
            listOfAllTasks.remove(index);
            listOfAllTasks.add(index, newTask);

            changeEpicStatusAfterChangeSubtask(newTask);
            return listOfAllTasks.get(index);
        }
        System.out.printf("Переданный объект %s должен иметь тот же id %d что и назначаемый id %d\n",
                newTask.toString(), ((Task) newTask).getId(), taskId);
        return null;
    }

    private void changeEpicStatusAfterChangeSubtask(Object newTask) {
        if (newTask.getClass().equals(Subtask.class) && checkIsEpic(((Subtask) newTask).getEpicId())) {
            actualizationEpicStatus((Subtask) newTask);
        }
    }
}
