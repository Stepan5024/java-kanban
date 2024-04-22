package manager;


import storage.history.InMemoryHistoryManager;
import storage.managers.TaskRepository;
import storage.managers.impl.InMemoryTaskManager;

public abstract class Managers {

    public static TaskRepository getDefault() {
        return new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
