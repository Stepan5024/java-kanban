package controller.managers;

import controller.history.HistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskManager {
    long generateId();

    Task createNewTask(String title, String description, String status, LocalDateTime startTime, Duration duration);

    Epic createNewEpic(String title, String description);

    Subtask createNewSubtask(String title, String description, String status, long epicId,
                             LocalDateTime startTime, Duration duration);

    void actualizationEpicStatus(Subtask subtask);

    void updateEpicTimeAndDuration(long epicId);

    void addToTasksList(Object obj);

    <T> List<T> getListOfAllEntities();

    Object getEntityById(long id);

    Epic getEpicById(long id);

    boolean tasksOverlap(LocalDateTime start1, Duration duration1, LocalDateTime start2, Duration duration2);

    void changeEpicStatusAfterChangeSubtask(Object newTask);

    Subtask getSubtaskById(long id);

    Task getTaskById(long id);

    int removeEntityFromKanban(Class<?> aClass);

    List<Object> getAllEntitiesByClass(Class<?> aClass);

    int removeTaskById(long taskId);

    List<Subtask> getListOfSubtaskByEpicId(long epicId);

    Object updateTask(Object newTask, long taskId);

    HistoryManager getHistoryManager();
}
