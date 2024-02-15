package controller.history;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    static int COUNT_OF_RECENT_TASK = 10;
    private static final List<Task> recentTasks = new ArrayList<>(COUNT_OF_RECENT_TASK);


    @Override
    public ArrayList<Task> getHistory() {


        ArrayList<Task> historyList = new ArrayList<>(COUNT_OF_RECENT_TASK);
        int size = recentTasks.size();
        int startIndex = Math.max(0, size - COUNT_OF_RECENT_TASK);

        for (int i = startIndex; i < size; i++) {
            historyList.add((Task) recentTasks.get(i));
        }
        return historyList;
    }

    @Override
    public void add(Task task) {

        if (recentTasks.size() >= COUNT_OF_RECENT_TASK) {
            recentTasks.remove(0);
            recentTasks.add(recentTasks.size(), (Task) task);
        } else {
            recentTasks.add((Task) task);
        }
    }
}
