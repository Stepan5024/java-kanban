package service.impl;

import model.Task;
import service.IHistoryService;
import storage.history.HistoryRepository;
import storage.managers.TaskRepository;

import java.util.List;

public class HistoryService implements IHistoryService {

    private final HistoryRepository historyRepository;

    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }


    @Override
    public void addTask(Task task) {

        historyRepository.addTask(task);
        System.out.println("addTask to history in service " + historyRepository.getHistory());
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = historyRepository.getHistory();
        System.out.println("Retrieving task history. Total entries: " + history.size());
        return history;
    }

    @Override
    public boolean removeTask(Long id) {
        historyRepository.removeTask(id);
        return true;
    }
}
