package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface IEpicService {
    void setSubtaskService(ISubtaskService subtaskService);

    List<Task> getEpics();

    Epic getEpicById(Long id);

    Epic createEpic(Epic task);

    Epic updateEpic(Epic task);

    boolean deleteEpic(Long id);

}
