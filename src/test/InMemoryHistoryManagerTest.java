import controller.history.InMemoryHistoryManager;
import controller.managers.InMemoryTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.Managers;

import java.util.ArrayList;

import static controller.history.InMemoryHistoryManager.COUNT_OF_RECENT_TASK;
import static model.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
    InMemoryTaskManager memoryTaskManagerTest = new InMemoryTaskManager(Managers.getDefaultHistory());

    @Test
    void getHistory() {
        ArrayList<Task> expectedList = new ArrayList<>(COUNT_OF_RECENT_TASK);
        Task task1 = memoryTaskManagerTest.createNewTask("Переезд",
                "Новая квартира по адресу Москва ул. Дружбы", "DONE");
        Epic epic1 = memoryTaskManagerTest.createNewEpic("Переезд",
                "Новая квартира по адресу Москва ул. Дружбы");
        Subtask subtask1 = memoryTaskManagerTest.createNewSubtask("Title",
                "desc", "NEW", epic1.getId());
        Task task2 = memoryTaskManagerTest.createNewTask("Переезд",
                "Новая квартира по адресу Москва ул. Дружбы", "DONE");
        Epic epic2 = memoryTaskManagerTest.createNewEpic("Переезд",
                "Новая квартира по адресу Москва ул. Дружбы");
        Subtask subtask2 = memoryTaskManagerTest.createNewSubtask("Title",
                "desc", "NEW", epic1.getId());
        Task task3 = memoryTaskManagerTest.createNewTask("Переезд",
                "Новая квартира по адресу Москва ул. Дружбы", "DONE");
        Epic epic3 = memoryTaskManagerTest.createNewEpic("Переезд",
                "Новая квартира по адресу Москва ул. Дружбы");
        Subtask subtask3 = memoryTaskManagerTest.createNewSubtask("Title",
                "desc", "NEW", epic1.getId());
        Subtask subtask4 = memoryTaskManagerTest.createNewSubtask("Title",
                "desc", "NEW", epic1.getId());
        expectedList.add(task1);
        expectedList.add(epic1);
        expectedList.add(subtask1);
        expectedList.add(task2);
        expectedList.add(epic2);
        expectedList.add(subtask2);
        expectedList.add(task3);
        expectedList.add(epic3);
        expectedList.add(subtask3);
        expectedList.add(subtask4);

        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subtask1);
        historyManager.add(task2);
        historyManager.add(epic2);
        historyManager.add(subtask2);
        historyManager.add(task3);
        historyManager.add(epic3);
        historyManager.add(subtask3);
        historyManager.add(subtask4);

        ArrayList<Task> listOfReturnedHistory = historyManager.getHistory();
        Assertions.assertTrue(listOfReturnedHistory.size() <= COUNT_OF_RECENT_TASK,
                String.format("В возвращенном списке количества просмотренных задач отличное " +
                        "значение от COUNT_OF_RECENT_TASK = %d", COUNT_OF_RECENT_TASK));
        for (int i = 0; i < expectedList.size(); i++) {
            Assertions.assertEquals(expectedList.get(i), listOfReturnedHistory.get(i), String.format("Объекты" +
                    " в истории не равны %s %s", expectedList.get(i), listOfReturnedHistory.get(i)));
        }
    }

    @Test
    void add() {
        Task task1 = memoryTaskManagerTest.createNewTask("Переезд",
                "Новая квартира по адресу Москва ул. Дружбы", "DONE");
        Epic epic1 = memoryTaskManagerTest.createNewEpic("Переезд",
                "Новая квартира по адресу Москва ул. Дружбы");
        Subtask subtask1 = memoryTaskManagerTest.createNewSubtask("Title",
                "desc", "NEW", epic1.getId());

        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subtask1);
        int sizeHistoryList = historyManager.getRecentTasks().size();
        Assertions.assertEquals(3, sizeHistoryList, String.format("Было создано 3 " +
                "просмотра - получено %d просмотров", sizeHistoryList));

    }
}