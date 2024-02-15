package util;

import controller.InMemoryTaskManager;
import controller.TaskManager;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

}
