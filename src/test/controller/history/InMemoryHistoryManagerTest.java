package controller.history;

import controller.managers.InMemoryTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import manager.Managers;

import java.util.ArrayList;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;


class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager;
    InMemoryTaskManager memoryTaskManagerTest = new InMemoryTaskManager(Managers.getDefaultHistory());

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void addSingleTaskTest() {
        Task task = new Task("Test Task", "Description", TaskStatus.NEW);
        historyManager.add(task);
        assertFalse(historyManager.getHistory().isEmpty(), "История просмотра не должна быть пустой после " +
                "добавления в нее просмотра");
        assertEquals(task, historyManager.getHistory().get(0), "Добавленная задача не равна " +
                "первой задаче в истории");
    }

    @Test
    void orderOfAddedTasksTest() {
        Task firstTask = new Task("First Task", "Description", TaskStatus.NEW);
        Task secondTask = new Task("Second Task", "Description", TaskStatus.NEW);
        historyManager.add(firstTask);
        historyManager.add(secondTask);
        assertEquals(2, historyManager.getHistory().size(), "История должна содержать две задачи");
        assertEquals(firstTask, historyManager.getHistory().get(0), "Первая задача не равна первой задачи в " +
                "истории просмотра");
        assertEquals(secondTask, historyManager.getHistory().get(1), "Вторая задача не равна второй задачи в " +
                "истории просмотра");
    }

    @Test
    void getHistoryUniqueTask() {
        ArrayList<Task> expectedList = new ArrayList<>();
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
        assertEquals(10, listOfReturnedHistory.size(),
                String.format("Было создано 10 уникальных просмотров, а вернулось %d", listOfReturnedHistory.size()));
        for (int i = 0; i < expectedList.size(); i++) {
            assertEquals(expectedList.get(i), listOfReturnedHistory.get(i), String.format("Объекты" +
                    " в истории не равны %s %s", expectedList.get(i), listOfReturnedHistory.get(i)));
        }
    }

    @Test
    void getHistoryRepeatedTask() {
        LinkedList<Task> expectedList = new LinkedList<>();
        ArrayList<Task> listOfReturnedHistory = historyManager.getHistory();

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
        historyManager.add(task1);

        listOfReturnedHistory = historyManager.getHistory();
        int expected1 = 1;
        assertEquals(expected1, listOfReturnedHistory.size(),
                String.format("Было создано %d уникальных просмотров, а вернулось %d", expected1, listOfReturnedHistory.size()));
        for (int i = 0; i < expectedList.size(); i++) {
            assertEquals(expectedList.get(i), listOfReturnedHistory.get(i), String.format("Объекты" +
                    " в истории не равны %s %s", expectedList.get(i), listOfReturnedHistory.get(i)));
        }

        historyManager.add(epic1);
        expectedList.add(epic1);
        expected1 = 2;
        listOfReturnedHistory = historyManager.getHistory();
        assertEquals(expected1, listOfReturnedHistory.size(),
                String.format("Было создано %d уникальных просмотров, а вернулось %d", expected1, listOfReturnedHistory.size()));
        for (int i = 0; i < expectedList.size(); i++) {
            assertEquals(expectedList.get(i), listOfReturnedHistory.get(i), String.format("Объекты" +
                    " в истории не равны %s %s", expectedList.get(i), listOfReturnedHistory.get(i)));
        }

        historyManager.add(subtask1);
        expectedList.add(subtask1);
        expected1 = 3;
        listOfReturnedHistory = historyManager.getHistory();
        assertEquals(expected1, listOfReturnedHistory.size(),
                String.format("Было создано %d уникальных просмотров, а вернулось %d", expected1, listOfReturnedHistory.size()));
        for (int i = 0; i < expectedList.size(); i++) {
            assertEquals(expectedList.get(i), listOfReturnedHistory.get(i), String.format("Объекты" +
                    " в истории не равны %s %s", expectedList.get(i), listOfReturnedHistory.get(i)));
        }
        // надо переместить в истории просмотра в конец задачу
        expectedList.remove(task1);
        expectedList.add(task1);
        historyManager.add(task1);

        listOfReturnedHistory = historyManager.getHistory();
        assertEquals(expected1, listOfReturnedHistory.size(),
                String.format("Было создано %d уникальных просмотров, а вернулось %d", expected1, listOfReturnedHistory.size()));
        for (int i = 0; i < expectedList.size(); i++) {
            assertEquals(expectedList.get(i), listOfReturnedHistory.get(i), String.format("Объекты" +
                    " в истории не равны %s %s", expectedList.get(i), listOfReturnedHistory.get(i)));
        }

    }


    @Test
    void add() {

        int sizeHistoryListBefore = historyManager.getRecentTasks().size();
        Task task1 = memoryTaskManagerTest.createNewTask("Переезд",
                "Новая квартира по адресу Москва ул. Дружбы", "DONE");
        Epic epic1 = memoryTaskManagerTest.createNewEpic("Переезд",
                "Новая квартира по адресу Москва ул. Дружбы");
        Subtask subtask1 = memoryTaskManagerTest.createNewSubtask("Title",
                "desc", "NEW", epic1.getId());

        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subtask1);
        int sizeHistoryListAfter = historyManager.getRecentTasks().size();
        int sizeHistoryList = sizeHistoryListAfter - sizeHistoryListBefore;
        assertEquals(3, sizeHistoryList,
                String.format("Было создано 3 просмотра - получено %d просмотров", sizeHistoryList));

    }

    @Test
    void removeTaskFromMiddleTest() {
        Task firstTask = new Task("First Task", "Description", TaskStatus.NEW);
        Task secondTask = new Task("Second Task", "Description", TaskStatus.NEW);
        Task thirdTask = new Task("Third Task", "Description", TaskStatus.NEW);

        historyManager.add(firstTask);
        historyManager.add(secondTask);
        historyManager.add(thirdTask);

        historyManager.remove(secondTask.getId());

        assertFalse(historyManager.getHistory().contains(secondTask), "Вторая задача должна быть удалена");
        assertTrue(historyManager.getHistory().contains(firstTask), "Первая задача должна была остаться");
        assertTrue(historyManager.getHistory().contains(thirdTask), "Третья задача должна была остаться");
    }


}