package manager;

import controller.history.InMemoryHistoryManager;
import controller.managers.InMemoryTaskManager;
import controller.managers.TaskManager;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
