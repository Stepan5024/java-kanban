package Controller;

import Model.Epic;
import Model.Subtask;
import Model.Task;
import Model.TaskStatus;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private static long taskId;
    private final ArrayList<Object> listOfAllTasks = new ArrayList<>();

    public static long generateId() {
        return taskId++;
    }


    public Task createNewTask(String title, String description, String status) {
        // ТЗ пункт 2. D Создание Задачи
        Task task = new Task(title,
                description,
                TaskStatus.valueOf(status));
        addToTasksList(task);
        return task;
    }

    public Epic createNewEpic(String title, String description, String status) {
        // ТЗ пункт 2. D Создание Эпика
        Epic epic = new Epic(new Task(title, description, TaskStatus.valueOf(status)));
        addToTasksList(epic);
        return epic;
    }

    public Subtask createNewSubtask(String title, String description, String status, long epicId) {
        // ТЗ пункт 2. D Создание Подзадачи
        Subtask subtask = new Subtask(new Task(title, description, TaskStatus.valueOf(status)), epicId);
        addToTasksList(subtask);
        return subtask;
    }

    private void addToTasksList(Object obj) {
        listOfAllTasks.add(obj);
    }

    public ArrayList<Object> getListOfAllEntities() {
        return listOfAllTasks;
    }

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


    public int removeTaskById(long taskId) {
        // ТЗ пункт 2.F Удаление по идентификатору
        List<Object> tasksToRemove = new ArrayList<>();
        Object obj = getEntityById(taskId);

        if(obj != null) {
            tasksToRemove.add(obj);
            if(obj.getClass().equals(Epic.class)) {
                tasksToRemove.addAll(getListOfSubtaskByEpicId(taskId));
            }
        }

        int countDeletedItems = tasksToRemove.size();

        for (Object task : tasksToRemove) {
            listOfAllTasks.remove(task);
        }
        return countDeletedItems;
    }


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

    public Object updateTask(Object newTask, long taskId) {
        // ТЗ 2. E Обновление объекта новой версией.
        // логика работы
        // 1. Находим индекс таски в списке
        // 2. Удаляем элемент по индексу
        // 3. Вставляем новый объект по индексу

        int index = -1;

        for (int i = 0; i < getListOfAllEntities().size(); i++) {
            Object obj = getListOfAllEntities().get(i);
            if (obj.getClass().equals(Subtask.class) && ((Subtask) obj).getId() == taskId) {
                index = i;
            } else if (obj.getClass().equals(Task.class) && ((Task) obj).getId() == taskId) {
                index = i;
            } else if (obj.getClass().equals(Epic.class) && ((Epic) obj).getId() == taskId) {
                index = i;
            }
        }
        if (index != -1) {
            listOfAllTasks.remove(index);
            listOfAllTasks.add(index, newTask);
            ((Task) newTask).setId(taskId);
            return newTask;
        }
        System.out.println("Переданного Id нету в списке задач");
        return null;
    }
}
