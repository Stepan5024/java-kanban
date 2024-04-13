package service;

import model.Task;
import service.impl.InvalidEntityTypeException;
import service.impl.TaskOverlapException;

import java.util.List;

public interface TaskService {

    Task create(Task task) throws TaskOverlapException;

    Task getTaskById(long id) throws InvalidEntityTypeException;

    List<Object> removeTaskById(long taskId);

    Object updateTask(Task newTask, long taskId);

}
