package controller.history;

import controller.managers.InMemoryTaskManager;
import manager.Managers;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class InMemoryHistoryManagerTest {

    static String firstTaskTitle;
    static String firstTaskDescription;
    static String secondTaskTitle;
    static String secondTaskDescription;
    static String firstEpicTitle;
    static String firstEpicDescription;
    static String thirdSubTaskTitle;
    static String thirdSubTaskDescription;
    static String fourTaskTitle;
    static String fourTaskDescription;
    static String secondEpicTitle;
    static String secondEpicDescription;
    static String secondSubTaskTitleForFirstEpic;
    static String secondSubTaskDescriptionForFirstEpic;
    static String thirdTaskTitle;
    static String thirdTaskDescription;
    static String thirdEpicTitle;
    static String thirdEpicDescription;

    InMemoryHistoryManager historyManager;
    InMemoryTaskManager memoryTaskManagerTest = new InMemoryTaskManager(Managers.getDefaultHistory());

    @BeforeAll
    static void initTextLabels() {
        firstTaskTitle = "Переезд";
        firstTaskDescription = "Новая квартира по адресу Москва ул. Дружбы";
        secondTaskTitle = "Пример второй задачи";
        secondTaskDescription = "Описание второй задачи";
        firstEpicTitle = "Написание диплома";
        firstEpicDescription = "Для выпуска из университета";
        thirdSubTaskTitle = "Чтение литературы";
        thirdSubTaskDescription = "Для выпуска из университета";
        fourTaskTitle = "Прогулка и фитнес ";
        fourTaskDescription = "в парке";
        secondEpicTitle = "Сдача ITELS";
        secondEpicDescription = "Для магистратуры";
        secondSubTaskTitleForFirstEpic = "Подзадача в рамках эпика";
        secondSubTaskDescriptionForFirstEpic = "Пум-Пум";
        thirdTaskTitle = "Купить корм собаке";
        thirdTaskDescription = "5ка";
        thirdEpicTitle = "Эпик три";
        thirdEpicDescription = "Все будет задрайвись!";

    }

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void addSingleTaskTest() {
        Task task = new Task(firstTaskTitle, firstTaskDescription, TaskStatus.NEW);
        historyManager.add(task);
        assertFalse(historyManager.getHistory().isEmpty(), "История просмотра не должна быть пустой после " +
                "добавления в нее просмотра");
        assertEquals(task, historyManager.getHistory().get(0), "Добавленная задача не равна " +
                "первой задаче в истории");
    }

    @Test
    void orderOfAddedTasksTest() {
        Task firstTask = new Task(firstTaskTitle, firstTaskDescription, TaskStatus.NEW);
        Task secondTask = new Task(secondTaskTitle, secondTaskDescription, TaskStatus.NEW);
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
        Task task1 = memoryTaskManagerTest.createNewTask(firstTaskTitle,
                firstTaskDescription, "DONE", null, Duration.ZERO);
        Epic epic1 = memoryTaskManagerTest.createNewEpic(firstEpicTitle,
                firstEpicDescription);
        Subtask subtask1 = memoryTaskManagerTest.createNewSubtask(thirdSubTaskTitle,
                thirdSubTaskDescription, "NEW", epic1.getId(), null, Duration.ZERO);
        Subtask subtask2 = memoryTaskManagerTest.createNewSubtask(secondSubTaskTitleForFirstEpic,
                secondSubTaskDescriptionForFirstEpic, "NEW", epic1.getId(), null, Duration.ZERO);
        Task task2 = memoryTaskManagerTest.createNewTask(fourTaskTitle,
                fourTaskDescription, "DONE", null, Duration.ZERO);
        Epic epic2 = memoryTaskManagerTest.createNewEpic(secondEpicTitle,
                secondEpicDescription);
        Task task3 = memoryTaskManagerTest.createNewTask(thirdTaskTitle,
                thirdTaskDescription, "DONE", null, Duration.ZERO);
        Epic epic3 = memoryTaskManagerTest.createNewEpic(thirdEpicTitle,
                thirdEpicDescription);
        Subtask subtask3 = memoryTaskManagerTest.createNewSubtask(firstTaskTitle,
                firstTaskDescription, "NEW", epic1.getId(), null, Duration.ZERO);
        Subtask subtask4 = memoryTaskManagerTest.createNewSubtask(firstTaskTitle,
                firstTaskDescription, "NEW", epic1.getId(), null, Duration.ZERO);
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
        List<Task> listOfReturnedHistory;

        Task task1 = memoryTaskManagerTest.createNewTask(firstTaskTitle,
                firstTaskDescription, "DONE", null, Duration.ZERO);
        Epic epic1 = memoryTaskManagerTest.createNewEpic(firstEpicTitle,
                firstEpicDescription);
        Subtask subtask1 = memoryTaskManagerTest.createNewSubtask(thirdSubTaskTitle,
                thirdSubTaskDescription, "NEW", epic1.getId(), null, Duration.ZERO);

        expectedList.add(task1);
        historyManager.add(task1);

        listOfReturnedHistory = historyManager.getHistory();
        int expected1 = 1;
        assertEquals(expected1, listOfReturnedHistory.size(),
                String.format("Было создано %d уникальных просмотров, а вернулось %d",
                        expected1, listOfReturnedHistory.size()));
        for (int i = 0; i < expectedList.size(); i++) {
            assertEquals(expectedList.get(i), listOfReturnedHistory.get(i), String.format("Объекты" +
                    " в истории не равны %s %s", expectedList.get(i), listOfReturnedHistory.get(i)));
        }

        historyManager.add(epic1);
        expectedList.add(epic1);
        expected1 = 2;
        listOfReturnedHistory = historyManager.getHistory();
        assertEquals(expected1, listOfReturnedHistory.size(),
                String.format("Было создано %d уникальных просмотров, а вернулось %d",
                        expected1, listOfReturnedHistory.size()));
        for (int i = 0; i < expectedList.size(); i++) {
            assertEquals(expectedList.get(i), listOfReturnedHistory.get(i), String.format("Объекты" +
                    " в истории не равны %s %s", expectedList.get(i), listOfReturnedHistory.get(i)));
        }

        historyManager.add(subtask1);
        expectedList.add(subtask1);
        expected1 = 3;
        listOfReturnedHistory = historyManager.getHistory();
        assertEquals(expected1, listOfReturnedHistory.size(),
                String.format("Было создано %d уникальных просмотров, а вернулось %d",
                        expected1, listOfReturnedHistory.size()));
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

        Task task1 = memoryTaskManagerTest.createNewTask(firstTaskTitle,
                firstTaskDescription, "DONE", null, Duration.ZERO);
        Epic epic1 = memoryTaskManagerTest.createNewEpic(firstEpicTitle,
                firstEpicDescription);
        Subtask subtask1 = memoryTaskManagerTest.createNewSubtask(thirdSubTaskTitle,
                thirdSubTaskDescription, "NEW", epic1.getId(), null, Duration.ZERO);

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
        Task firstTask = new Task(firstTaskTitle, firstTaskDescription, TaskStatus.NEW);
        Task secondTask = new Task(secondTaskTitle, secondTaskDescription, TaskStatus.NEW);
        Task thirdTask = new Task(thirdTaskTitle, thirdTaskDescription, TaskStatus.NEW);

        historyManager.add(firstTask);
        historyManager.add(secondTask);
        historyManager.add(thirdTask);

        historyManager.remove(secondTask.getId());

        assertFalse(historyManager.getHistory().contains(secondTask), "Вторая задача должна быть удалена");
        assertTrue(historyManager.getHistory().contains(firstTask), "Первая задача должна была остаться");
        assertTrue(historyManager.getHistory().contains(thirdTask), "Третья задача должна была остаться");
    }
}