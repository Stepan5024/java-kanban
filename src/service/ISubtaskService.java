package service;

import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.List;

public interface ISubtaskService {

    List<Task> getSubtasks();

    Subtask getSubtaskById(Long id);

    Task createSubtask(Subtask task);
    List<Subtask> getSubtasksByEpicId(Long epicId);

    boolean isAllSubtasksInRequiredStatus(Long epicId, TaskStatus status);


    Subtask updateSubtask(Subtask task);

    boolean deleteSubtask(Long id);
}
