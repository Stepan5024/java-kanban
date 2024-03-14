package controller.managers;

import controller.history.InMemoryHistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import manager.Managers;

import java.util.HashMap;
import java.util.List;

import static model.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

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

    @Test
    void generateId() {
        long expectedId = InMemoryTaskManager.getId();
        expectedId += 1;
        memoryTaskManagerTest.generateId();
        long generedId = InMemoryTaskManager.getId();
        Assertions.assertEquals(expectedId, generedId, String.format("Expected id %d return id %d", expectedId, generedId));
    }


    @Test
    void checkIsEpic() {
        Epic epic1 = memoryTaskManagerTest.createNewEpic(firstEpicTitle,
                firstEpicDescription);
        boolean result = memoryTaskManagerTest.checkIsEpic(epic1.getId());
        Assertions.assertTrue(result);

    }

    @Test
    void checkIsNotEpic() {
        boolean result = memoryTaskManagerTest.checkIsEpic(1);
        Assertions.assertFalse(result);
    }

    @Test
    void addNewTask() {
        final Task savedTask = (memoryTaskManagerTest.createNewTask(firstTaskTitle, firstTaskDescription, "NEW"));
        Task task = new Task(secondTaskTitle, secondTaskDescription, NEW, savedTask.getId());
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Object> tasks = memoryTaskManagerTest.getAllEntitiesByClass(Task.class);

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void checkThatSubtaskDoNotAddedHowOwnEpic() {
        long idNewSubtask = InMemoryTaskManager.getId();
        Subtask subtask = memoryTaskManagerTest.createNewSubtask(
                thirdSubTaskTitle,
                thirdSubTaskDescription,
                "NEW", ++idNewSubtask);
        Assertions.assertNull(subtask, "Нельзя установить подзадачу как собственный Epic");
    }

    @Test
    void checkThatEpicDoNotAddedHowSubtask() {
        Epic epic = memoryTaskManagerTest.createNewEpic(firstEpicTitle,
                firstEpicDescription);

        Subtask subtask = memoryTaskManagerTest.createNewSubtask(
                secondSubTaskTitleForFirstEpic,
                secondSubTaskDescriptionForFirstEpic,
                "NEW", epic.getId());

        Object subtask2 = memoryTaskManagerTest.updateTask(subtask, epic.getId());

        Assertions.assertNull(subtask2, "Нельзя обновить эпик как подзадачу");
    }

    @Test
    void createNewTask() {
        Task task = memoryTaskManagerTest.createNewTask(firstTaskTitle,
                firstTaskDescription, "DONE");

        Assertions.assertEquals(Task.class, task.getClass(), "Класс создаваемых подзадач должен быть " +
                "Task");

        Task temp = memoryTaskManagerTest.getTaskById(task.getId());
        Assertions.assertNotNull(temp, String.format("Не была найдена созданная task по id %d", task.getId()));
    }

    @Test
    void createNewEpic() {
        Epic epic1 = memoryTaskManagerTest.createNewEpic(firstEpicTitle,
                firstEpicDescription);

        Assertions.assertEquals(Epic.class, epic1.getClass(), "Класс создаваемых подзадач должен быть " +
                "Epic");

        Epic temp = memoryTaskManagerTest.getEpicById(epic1.getId());
        Assertions.assertNotNull(temp, String.format("Не был найден созданный epic по id %d", epic1.getId()));
    }

    @Test
    void createNewSubtask() {
        Epic epic1 = memoryTaskManagerTest.createNewEpic(firstEpicTitle,
                firstEpicDescription);

        Subtask subtask = memoryTaskManagerTest.createNewSubtask(
                secondSubTaskTitleForFirstEpic,
                secondSubTaskDescriptionForFirstEpic,
                "NEW",
                epic1.getId());
        Assertions.assertEquals(Subtask.class, subtask.getClass(), "Класс создаваемых подзадач должен быть " +
                "subtask");

        Subtask temp = memoryTaskManagerTest.getSubtaskById(subtask.getId());
        Assertions.assertNotNull(temp, String.format("Не была найдена созданная subtask по id %d", subtask.getId()));
    }

    @Test
    void updateTask() {

        Task task1 = memoryTaskManagerTest.createNewTask(firstTaskTitle,
                firstTaskDescription, "DONE");
        Task task2 = (Task) memoryTaskManagerTest.updateTask(new Task(secondTaskTitle,
                secondTaskDescription, NEW, task1.getId()), task1.getId());

        Assertions.assertEquals(task1, task2, "task1 != task2 по id");

    }

    @Test
    void testThatAllEntityWithUniqueId() {
        Task task1 = new Task(firstTaskTitle, firstTaskDescription, NEW, 0);
        Task task2 = new Task(secondTaskTitle, secondTaskDescription, NEW, 0);
        Task task3 = memoryTaskManagerTest.createNewTask(thirdTaskTitle,
                thirdTaskDescription, "DONE");

        Epic epic0 = new Epic(firstEpicTitle, firstEpicDescription, NEW, 0);
        Epic epic1 = memoryTaskManagerTest.createNewEpic(secondEpicTitle,
                secondEpicDescription);
        Epic epic2 = memoryTaskManagerTest.createNewEpic(thirdEpicTitle,
                thirdEpicDescription);

        Epic epic3 = (Epic) memoryTaskManagerTest.updateTask(epic0, epic2.getId());
        Assertions.assertNull(epic3, "Переданный объект должен иметь тот же id что и назначаемый id");

        HashMap<Long, Integer> listOfId = new HashMap<>();

        List<Object> list = memoryTaskManagerTest.getListOfAllEntities();
        for (Object obj : list) {
            Task task = (Task) obj;
            listOfId.merge(task.getId(), 1, Integer::sum);
            Assertions.assertEquals(1, listOfId.get(task.getId()), String.format("На kanban доске " +
                    "обнаружены дубли id %d", task.getId()));
        }
    }

    @Test
    void removeSubTaskAndCheckThatThereDeletedFromEpicTest() {

        InMemoryTaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());

        Epic firstEpic = taskManager.createNewEpic(firstEpicTitle, firstEpicDescription);
        Subtask secondTask = taskManager.createNewSubtask(secondSubTaskTitleForFirstEpic, secondSubTaskDescriptionForFirstEpic,
                "NEW", firstEpic.getId());
        Subtask thirdTask = taskManager.createNewSubtask(thirdSubTaskTitle, thirdSubTaskDescription,
                "NEW", firstEpic.getId());

        taskManager.removeTaskById(secondTask.getId());

        assertFalse(taskManager.getListOfSubtaskByEpicId(firstEpic.getId()).contains(secondTask),
                "Подзадача была удалена и не должна больше принадлежать эпику");
        assertFalse(taskManager.getListOfAllEntities().contains(secondTask.getId()),
                "Задача удалена и не должна содержать свой Id в списке задач");

    }

    @Test
    void removeEpicWithSubtaskFromAndCheckHistoryTest() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
        InMemoryHistoryManager historyManager = (InMemoryHistoryManager) taskManager.getHistoryManager();

        Epic firstEpic = taskManager.createNewEpic(firstEpicTitle, firstEpicDescription);
        Subtask secondTask = taskManager.createNewSubtask(secondSubTaskTitleForFirstEpic, secondSubTaskTitleForFirstEpic,
                "NEW", firstEpic.getId());
        Subtask thirdTask = taskManager.createNewSubtask(thirdSubTaskTitle, thirdSubTaskDescription,
                "NEW", firstEpic.getId());

        Task fourthTask = taskManager.createNewTask(fourTaskTitle, fourTaskDescription, "NEW");

        taskManager.getEpicById(firstEpic.getId());
        taskManager.getSubtaskById(secondTask.getId());
        taskManager.getSubtaskById(thirdTask.getId());
        taskManager.getTaskById(fourthTask.getId());

        taskManager.removeTaskById(firstEpic.getId());

        assertFalse(historyManager.getHistory().contains(secondTask), "Вторая задача должна быть удалена");
        assertFalse(historyManager.getHistory().contains(firstEpic), "Первая задача не должна была остаться");
        assertFalse(historyManager.getHistory().contains(thirdTask), "Третья задача не должна была остаться");
        assertTrue(historyManager.getHistory().contains(fourthTask), "Четвертая задача должна была остаться");
    }
}