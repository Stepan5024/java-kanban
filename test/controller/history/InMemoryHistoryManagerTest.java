package controller.history;


import manager.Managers;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.impl.EpicService;
import service.impl.HistoryService;
import service.impl.SubtaskService;
import service.impl.TaskService;
import storage.history.HistoryRepository;
import storage.history.InMemoryHistoryManager;
import storage.managers.impl.InMemoryTaskManager;

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

    HistoryRepository historyRepository;
    InMemoryTaskManager memoryTaskManagerTest = new InMemoryTaskManager(Managers.getDefaultHistory());
    HistoryService historyService = new HistoryService(historyRepository);
    TaskService taskService = new TaskService(memoryTaskManagerTest, historyService);
    EpicService epicService = new EpicService(memoryTaskManagerTest, historyService);
    SubtaskService subtaskService = new SubtaskService(memoryTaskManagerTest, historyService, epicService);

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
        historyRepository = new InMemoryHistoryManager();
        historyService = new HistoryService(historyRepository);

        memoryTaskManagerTest = new InMemoryTaskManager(Managers.getDefaultHistory());

        taskService = new TaskService(memoryTaskManagerTest, historyService);
        epicService = new EpicService(memoryTaskManagerTest, historyService);
        this.epicService.setSubtaskService(subtaskService);
        subtaskService = new SubtaskService(memoryTaskManagerTest, historyService, epicService);

    }

    @Test
    void addSingleTaskTest() {
        Task task = new Task(firstTaskTitle, firstTaskDescription, TaskStatus.NEW);
        historyService.addTask(task);
        assertFalse(historyRepository.getHistory().isEmpty(), "История просмотра не должна быть пустой после " +
                "добавления в нее просмотра");
        assertEquals(task, historyRepository.getHistory().get(0), "Добавленная задача не равна " +
                "первой задаче в истории");
    }

    @Test
    public void shouldReturnEmptyHistoryWhenNoTasks() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        assertTrue(historyManager.getHistory().isEmpty(), "История задач должна быть пустой.");
    }

    @Test
    public void shouldNotAllowDuplicatesAndMoveToTheEndIfRepeated() {

        Task task1 = taskService.createTask(new Task(firstTaskTitle, firstTaskDescription, TaskStatus.NEW));

        Task task2 = taskService.createTask(new Task(secondTaskTitle, secondTaskDescription, TaskStatus.NEW));

        historyService.addTask(task1);
        historyService.addTask(task2);
        historyService.addTask(task1); // Добавляем task1 еще раз

        List<Task> history = historyRepository.getHistory();
        assertEquals(2, history.size(), "История должна содержать 2 уникальных задачи.");
        assertEquals(task2, history.get(0), "Task2 должен быть первым в истории.");
        assertEquals(task1, history.get(1), "Task1 должен быть перемещен в конец истории.");
    }

    @Test
    public void shouldRemoveTaskFromStartOfHistory() {

        Task task1 = taskService.createTask(new Task(firstTaskTitle, firstTaskDescription, TaskStatus.NEW));
        Task task2 = taskService.createTask(new Task(secondTaskTitle, secondTaskDescription, TaskStatus.NEW));

        historyService.addTask(task1);
        historyService.addTask(task2);

        historyService.removeTask(task1.getId());

        assertFalse(historyService.getHistory().contains(task1), "Task1 должен быть удален из истории.");
    }

    @Test
    public void shouldRemoveTaskFromMiddleOfHistory() {

        Task task1 = taskService.createTask(new Task(firstTaskTitle, firstTaskDescription, TaskStatus.NEW));
        Task task2 = taskService.createTask(new Task(secondTaskTitle, secondTaskDescription, TaskStatus.NEW));
        Task task3 = taskService.createTask(new Task(thirdTaskTitle, thirdTaskDescription, TaskStatus.NEW));

        historyService.addTask(task1);
        historyService.addTask(task2);
        historyService.addTask(task3);

        historyService.removeTask(task2.getId());

        assertFalse(historyService.getHistory().contains(task2), "Task2 должен быть удален из истории.");
    }

    @Test
    public void shouldRemoveTaskFromEndOfHistory() {

        Task task1 = taskService.createTask(new Task(firstTaskTitle, firstTaskDescription, TaskStatus.NEW));
        Task task2 = taskService.createTask(new Task(secondTaskTitle, secondTaskDescription, TaskStatus.NEW));

        historyService.addTask(task1);
        historyService.addTask(task2);

        historyService.removeTask(task2.getId());

        assertFalse(historyService.getHistory().contains(task2), "Task2 должен быть удален из истории.");
    }


    @Test
    void orderOfAddedTasksTest() {
        Task firstTask = taskService.createTask(new Task(firstTaskTitle, firstTaskDescription, TaskStatus.NEW));
        Task secondTask = taskService.createTask(new Task(secondTaskTitle, secondTaskDescription, TaskStatus.NEW));
        historyRepository.addTask(firstTask);
        historyRepository.addTask(secondTask);
        assertEquals(2, historyRepository.getHistory().size(), "История должна содержать две задачи");
        assertEquals(firstTask, historyRepository.getHistory().get(0), "Первая задача не равна первой задачи в " +
                "истории просмотра");
        assertEquals(secondTask, historyRepository.getHistory().get(1), "Вторая задача не равна второй задачи в " +
                "истории просмотра");
    }

    @Test
    void getHistoryUniqueTask() {
        ArrayList<Task> expectedList = new ArrayList<>();

        Task task1 = taskService.createTask(new Task(firstTaskTitle,
                firstTaskDescription, TaskStatus.DONE, null, Duration.ZERO));
        Epic epic1 = epicService.createEpic(new Epic(firstEpicTitle,
                firstEpicDescription, null));
        System.out.println(epic1);
        assertNotNull(epic1, "Epic creation failed, epic1 is null.");
        assertNotNull(epic1.getId(), "Epic ID is null after creation.");

        Subtask subtask1 = subtaskService.createSubtask(new Subtask(thirdSubTaskTitle,
                thirdSubTaskDescription, TaskStatus.NEW, epic1.getId(), null, Duration.ZERO));
        Subtask subtask2 = subtaskService.createSubtask(new Subtask(secondSubTaskTitleForFirstEpic,
                secondSubTaskDescriptionForFirstEpic, TaskStatus.NEW, epic1.getId(), null, Duration.ZERO));
        Task task2 = taskService.createTask(new Task(fourTaskTitle,
                fourTaskDescription, TaskStatus.DONE, null, Duration.ZERO));
        Epic epic2 = epicService.createEpic(new Epic(secondEpicTitle,
                secondEpicDescription, null));
        Task task3 = taskService.createTask(new Task(thirdTaskTitle,
                thirdTaskDescription, TaskStatus.DONE, null, Duration.ZERO));
        Epic epic3 = epicService.createEpic(new Epic(thirdEpicTitle,
                thirdEpicDescription, null));
        Subtask subtask3 = subtaskService.createSubtask(new Subtask(firstTaskTitle,
                firstTaskDescription, TaskStatus.NEW, epic1.getId(), null, Duration.ZERO));
        Subtask subtask4 = subtaskService.createSubtask(new Subtask(firstTaskTitle,
                firstTaskDescription, TaskStatus.NEW, epic1.getId(), null, Duration.ZERO));
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

        historyService.addTask(task1);
        historyService.addTask(epic1);
        historyService.addTask(subtask1);
        historyService.addTask(task2);
        historyService.addTask(epic2);
        historyService.addTask(subtask2);
        historyService.addTask(task3);
        historyService.addTask(epic3);
        historyService.addTask(subtask3);
        historyService.addTask(subtask4);

        List<Task> listOfReturnedHistory = historyService.getHistory();
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

        Task task1 = taskService.createTask(new Task(firstTaskTitle,
                firstTaskDescription, TaskStatus.DONE, null, Duration.ZERO));
        Epic epic1 = epicService.createEpic(new Epic(firstEpicTitle,
                firstEpicDescription, null));
        Subtask subtask1 = subtaskService.createSubtask(new Subtask(thirdSubTaskTitle,
                thirdSubTaskDescription, TaskStatus.NEW, epic1.getId(), null, Duration.ZERO));

        expectedList.add(task1);
        historyRepository.addTask(task1);

        listOfReturnedHistory = historyRepository.getHistory();
        int expected1 = 1;
        assertEquals(expected1, listOfReturnedHistory.size(),
                String.format("Было создано %d уникальных просмотров, а вернулось %d",
                        expected1, listOfReturnedHistory.size()));
        for (int i = 0; i < expectedList.size(); i++) {
            assertEquals(expectedList.get(i), listOfReturnedHistory.get(i), String.format("Объекты" +
                    " в истории не равны %s %s", expectedList.get(i), listOfReturnedHistory.get(i)));
        }

        historyRepository.addTask(epic1);
        expectedList.add(epic1);
        expected1 = 2;
        listOfReturnedHistory = historyRepository.getHistory();
        assertEquals(expected1, listOfReturnedHistory.size(),
                String.format("Было создано %d уникальных просмотров, а вернулось %d",
                        expected1, listOfReturnedHistory.size()));
        for (int i = 0; i < expectedList.size(); i++) {
            assertEquals(expectedList.get(i), listOfReturnedHistory.get(i), String.format("Объекты" +
                    " в истории не равны %s %s", expectedList.get(i), listOfReturnedHistory.get(i)));
        }

        historyRepository.addTask(subtask1);
        expectedList.add(subtask1);
        expected1 = 3;
        listOfReturnedHistory = historyRepository.getHistory();
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
        historyRepository.addTask(task1);

        listOfReturnedHistory = historyRepository.getHistory();
        assertEquals(expected1, listOfReturnedHistory.size(),
                String.format("Было создано %d уникальных просмотров, а вернулось %d", expected1, listOfReturnedHistory.size()));
        for (int i = 0; i < expectedList.size(); i++) {
            assertEquals(expectedList.get(i), listOfReturnedHistory.get(i), String.format("Объекты" +
                    " в истории не равны %s %s", expectedList.get(i), listOfReturnedHistory.get(i)));
        }
    }

    @Test
    void add() {
        int sizeHistoryListBefore = historyRepository.getHistory().size();

        Task task1 = taskService.createTask(new Task(firstTaskTitle,
                firstTaskDescription, TaskStatus.DONE, null, Duration.ZERO));
        Epic epic1 = epicService.createEpic(new Epic(firstEpicTitle,
                firstEpicDescription, null));
        Subtask subtask1 = subtaskService.createSubtask(new Subtask(thirdSubTaskTitle,
                thirdSubTaskDescription, TaskStatus.NEW, epic1.getId(), null, Duration.ZERO));

        historyRepository.addTask(task1);
        historyRepository.addTask(epic1);
        historyRepository.addTask(subtask1);
        int sizeHistoryListAfter = historyRepository.getHistory().size();
        int sizeHistoryList = sizeHistoryListAfter - sizeHistoryListBefore;
        assertEquals(3, sizeHistoryList,
                String.format("Было создано 3 просмотра - получено %d просмотров", sizeHistoryList));
    }

    @Test
    void removeTaskFromMiddleTest() {
        Task firstTask = taskService.createTask(new Task(firstTaskTitle, firstTaskDescription, TaskStatus.NEW));
        Task secondTask = taskService.createTask(new Task(secondTaskTitle, secondTaskDescription, TaskStatus.NEW));
        Task thirdTask = taskService.createTask(new Task(thirdTaskTitle, thirdTaskDescription, TaskStatus.NEW));

        historyService.addTask(firstTask);
        historyService.addTask(secondTask);
        historyService.addTask(thirdTask);

        historyService.removeTask(secondTask.getId());

        assertFalse(historyService.getHistory().contains(secondTask), "Вторая задача должна быть удалена");
        assertTrue(historyService.getHistory().contains(firstTask), "Первая задача должна была остаться");
        assertTrue(historyService.getHistory().contains(thirdTask), "Третья задача должна была остаться");
    }


}