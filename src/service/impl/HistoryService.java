package service.impl;

import model.Task;
import service.IHistoryService;
import storage.history.HistoryRepository;
import storage.managers.TaskRepository;

import java.util.List;

public class HistoryService  implements IHistoryService {

    private final HistoryRepository historyRepository;

    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    // Method to record a task access in the history
    @Override
    public void addTask(Task task) {

        historyRepository.addTask(task);
        System.out.println("addTask to history in service " + historyRepository.getHistory());
        //System.out.println("Recorded task access: " + task);
    }

    // Method to retrieve the history of accessed tasks
    @Override
    public List<Task> getHistory() {
        System.out.println("history repo " + historyRepository.getHistory());
        List<Task> history = historyRepository.getHistory();
        //System.out.println("Retrieving task history. Total entries: " + history.size());
        return history;
    }

    // Method to clear the task history
    @Override
    public boolean removeTask(Long id) {
        historyRepository.removeTask(id);
        //System.out.printf("History id %d cleared.\n", id);
        return true;
    }
}
