package service;

public interface IEpicStatusUpdater {
    void actualizeEpicStatus(Long epicId);

    void updateEpicTimeAndDuration(Long epicId);
}
