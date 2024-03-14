package controller.managers;

import controller.history.HistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {
    long generateId();

    Task createNewTask(String title, String description, String status);

    Epic createNewEpic(String title, String description);

    Subtask createNewSubtask(String title, String description, String status, long epicId);

    void actualizationEpicStatus(Subtask subtask);

    void addToTasksList(Object obj);

    <T> List<T> getListOfAllEntities();

    Object getEntityById(long id);

    Epic getEpicById(long id);

    Subtask getSubtaskById(long id);

    Task getTaskById(long id);

    int removeEntityFromKanban(Class<?> aClass);

    List<Object> getAllEntitiesByClass(Class<?> aClass);

    int removeTaskById(long taskId);

    List<Subtask> getListOfSubtaskByEpicId(long epicId);

    Object updateTask(Object newTask, long taskId);

    HistoryManager getHistoryManager();
}
