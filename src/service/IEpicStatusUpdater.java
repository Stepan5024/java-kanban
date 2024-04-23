package service;

import model.Subtask;

public interface IEpicStatusUpdater {
    void actualizeEpicStatus(Long epicId);
    void updateEpicTimeAndDuration(Long epicId);
}
