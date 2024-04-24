package service;

import model.Epic;
import model.Task;

import java.util.List;

public interface IEpicService {
    void setSubtaskService(ISubtaskService subtaskService);

    ISubtaskService getSubtaskService();

    List<Task> getEpics();

    Epic getEpicById(Long id);

    Epic createEpic(Epic task);

    Epic updateEpic(Epic task);

    boolean deleteEpic(Long id);

}
