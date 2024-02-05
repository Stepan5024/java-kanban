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
    public static long getId() {
        return taskId;
    }


    public Task createNewTask(String title, String description, String status) {

        Task task = new Task(title,
                description,
                TaskStatus.valueOf(status));
        addToTasksList(task);
        return task;
    }

    public Object getClass(Object obj) {
        if (obj.getClass().equals(Task.class)) return Task.class;
        else if (obj.getClass().equals(Epic.class)) return Epic.class;
        else if (obj.getClass().equals(Subtask.class)) return Subtask.class;
        return obj;
    }


    public Epic createNewEpic(String title, String description, String status) {
        Epic epic = new Epic(new Task(title, description, TaskStatus.valueOf(status)));
        addToTasksList(epic);
        return epic;
    }

    public Subtask createNewSubtask(String title, String description, String status, long epicId) {
        Subtask subtask = new Subtask(new Task(title, description, TaskStatus.valueOf(status)), epicId);
        addToTasksList(subtask);
        return subtask;
    }

    private void addToTasksList(Object obj) {
        listOfAllTasks.add(obj);
    }

    public ArrayList<Object> getListOfAllTasks() {
        return listOfAllTasks;
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Object obj : getListOfAllTasks()) {
            if (obj.getClass().equals(Task.class)) {
                tasks.add((Task) obj);
            }
        }
        return tasks;
    }

    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> epics = new ArrayList<>();
        for (Object obj : getListOfAllTasks()) {
            if (obj.getClass().equals(Epic.class)) {
                epics.add((Epic) obj);
            }
        }
        return epics;
    }

    public ArrayList<Subtask> getAllSubtask() {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (Object obj : getListOfAllTasks()) {
            if (obj.getClass().equals(Subtask.class)) {
                subtasks.add((Subtask) obj);
            }
        }
        return subtasks;
    }

    public int removeAllTasks() {
        int countDeletedItems = 0;

        for (Task task : getAllTasks()) {
            countDeletedItems += removeTaskById(task.getId());
        }
        return countDeletedItems;
    }

    public int removeAllSubtasks() {
        // ТЗ пункт 2.B Удаление всех эпиков
        int countDeletedItems = 0;
        for (Subtask task : getAllSubtask()) {
            countDeletedItems += removeTaskById(task.getId());
        }
        return countDeletedItems;
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

    private ArrayList<Object> getAllEntitiesByClass(Class<?> aClass) {
        ArrayList<Object> entities = new ArrayList<>();
        for(Object obj : getListOfAllTasks()) {
            if(obj.getClass().equals(aClass)) {
                entities.add(obj);
            }
        }
        return entities;
    }

    public int removeAllEpics() {
        // ТЗ пункт 2.B Удаление всех эпиков
        int countDeletedItems = 0;
        for (Epic task : getAllEpics()) {
            countDeletedItems += removeTaskById(task.getId());
        }
        return countDeletedItems;
    }


    public int removeTaskById(long taskId) {
    // ТЗ пункт 2.F Удаление по идентификатору
        List<Object> tasksToRemove = new ArrayList<>();
        for (Object obj : getListOfAllTasks()) {
            if (getClass(obj).equals(Subtask.class) && ((Subtask) obj).getId() == taskId) {
                tasksToRemove.add(obj);
            } else if (getClass(obj).equals(Task.class) && ((Task) obj).getId() == taskId) {
                tasksToRemove.add(obj);
            } else if (getClass(obj).equals(Epic.class) && ((Epic) obj).getId() == taskId) {
                tasksToRemove.addAll(getListOfSubtaskByEpicId(taskId));
                tasksToRemove.add(obj);
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

        for (Subtask subtask : getAllSubtask()) {
            if (subtask.getEpicId() == epicId) {
                listOfSubtasks.add(subtask);
            }
        }
        return listOfSubtasks;
    }

    public Object updateTask(Object newTask, long taskId) {
        // логика работы
        // 1. Находим индекс таски в списке
        // 2. Удаляем элемент по индексу
        // 3. Вставляем новый объект по индексу

        int index = -1;
        for (int i = 0; i < getListOfAllTasks().size(); i++) {
            Object obj = getListOfAllTasks().get(i);
            if (getClass(obj).equals(Subtask.class) && ((Subtask) obj).getId() == taskId) {
                index = i;
            } else if (getClass(obj).equals(Task.class) && ((Task) obj).getId() == taskId) {
                index = i;
            } else if (getClass(obj).equals(Epic.class) && ((Epic) obj).getId() == taskId) {
                index = i;
            }
        }
        if(index != -1) {
            listOfAllTasks.remove(index);
            listOfAllTasks.add(index, newTask);
            ((Task) newTask).setId(taskId);
            return newTask;
        }
        System.out.println("Переданного Id нету в списке задач");
        return null;
    }
}
