package service;

import model.Subtask;
import model.Task;

import java.util.List;

public interface ISubtaskService {

    List<Task> getSubtasks();

    Subtask getSubtaskById(Long id);

    Task createSubtask(Subtask task);

    Subtask updateSubtask(Subtask task);

    boolean deleteSubtask(Long id);
}
