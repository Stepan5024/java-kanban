package controller.managers;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import manager.Managers;
import java.util.HashMap;
import java.util.List;

import static model.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    InMemoryTaskManager memoryTaskManagerTest = new InMemoryTaskManager(Managers.getDefaultHistory());

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
        Epic epic1 = memoryTaskManagerTest.createNewEpic("Переезд",
                "Новая квартира по адресу Москва ул. Дружбы");
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

        final Task savedTask = (memoryTaskManagerTest.createNewTask("Test addNewTask", "Test addNewTask description", "NEW"));
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW, savedTask.getId());
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
                "Собрать коробки",
                "Вещи + одежду",
                "NEW", ++idNewSubtask);
        Assertions.assertNull(subtask, "Нельзя установить подзадачу как собственный Epic");
    }

    @Test
    void checkThatEpicDoNotAddedHowSubtask() {
        Epic epic = memoryTaskManagerTest.createNewEpic("Переезд",
                "Новая квартира по адресу Москва ул. Дружбы");

        Subtask subtask = memoryTaskManagerTest.createNewSubtask(
                "Собрать коробки",
                "Вещи + одежду",
                "NEW", epic.getId());

        Object subtask2 = memoryTaskManagerTest.updateTask(subtask, epic.getId());

        Assertions.assertNull(subtask2, "Нельзя обновить эпик как подзадачу");
    }

    @Test
    void createNewTask() {
        Task task = memoryTaskManagerTest.createNewTask("Переезд",
                "Новая квартира по адресу Москва ул. Дружбы", "DONE");

        Assertions.assertEquals(Task.class, task.getClass(), "Класс создаваемых подзадач должен быть " +
                "Task");

        Task temp = memoryTaskManagerTest.getTaskById(task.getId());
        Assertions.assertNotNull(temp, String.format("Не была найдена созданная task по id %d", task.getId()));
    }

    @Test
    void createNewEpic() {
        Epic epic1 = memoryTaskManagerTest.createNewEpic("Переезд",
                "Новая квартира по адресу Москва ул. Дружбы");

        Assertions.assertEquals(Epic.class, epic1.getClass(), "Класс создаваемых подзадач должен быть " +
                "Epic");

        Epic temp = memoryTaskManagerTest.getEpicById(epic1.getId());
        Assertions.assertNotNull(temp, String.format("Не был найден созданный epic по id %d", epic1.getId()));
    }

    @Test
    void createNewSubtask() {
        Epic epic1 = memoryTaskManagerTest.createNewEpic("Переезд",
                "Новая квартира по адресу Москва ул. Дружбы");

        Subtask subtask = memoryTaskManagerTest.createNewSubtask(
                "Упаковать кошку",
                "Кошка белая",
                "NEW",
                epic1.getId());
        Assertions.assertEquals(Subtask.class, subtask.getClass(), "Класс создаваемых подзадач должен быть " +
                "subtask");

        Subtask temp = memoryTaskManagerTest.getSubtaskById(subtask.getId());
        Assertions.assertNotNull(temp, String.format("Не была найдена созданная subtask по id %d", subtask.getId()));
    }

    @Test
    void updateTask() {

        Task task1 = memoryTaskManagerTest.createNewTask("Переезд",
                "Новая квартира по адресу Москва ул. Дружбы", "DONE");
        Task task2 = (Task) memoryTaskManagerTest.updateTask(new Task("new title",
                "new desc", NEW, task1.getId()), task1.getId());

        Assertions.assertEquals(task1, task2, "task1 != task2 по id");

    }

    @Test
    void testThatAllEntityWithUniqueId() {
        Task task1 = new Task("Покупка", "продуктов", NEW, 0);
        Task task2 = new Task("Уборка", "В комнате и на столе", NEW, 0);
        Task task3 = memoryTaskManagerTest.createNewTask("Переезд",
                "Новая квартира по адресу Москва ул. Дружбы", "DONE");

        Epic epic0 = new Epic("Уборка", "В комнате и на столе", NEW, 0);
        Epic epic1 = memoryTaskManagerTest.createNewEpic("Переезд",
                "Новая квартира по адресу Москва ул. Дружбы");
        Epic epic2 = memoryTaskManagerTest.createNewEpic("Переезд",
                "Новая квартира по адресу Москва ул. Дружбы");

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
}