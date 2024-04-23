package manager;


import storage.history.HistoryRepository;
import storage.history.InMemoryHistoryManager;
import storage.managers.TaskRepository;
import storage.managers.impl.FileBackedTaskManager;
import storage.managers.impl.InMemoryTaskManager;

import java.io.File;
import java.io.IOException;

public abstract class Managers {

    public static TaskRepository getDefault(HistoryRepository historyRepository) {
        try {
            new FileBackedTaskManager("123.txt", historyRepository);
            return FileBackedTaskManager.loadFromFile(new File("123.txt"));
        } catch (IOException e) {
            return new FileBackedTaskManager("123.txt", historyRepository);
        }

    }

    public static HistoryRepository getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
