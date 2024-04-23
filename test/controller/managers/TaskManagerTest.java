package controller.managers;

import manager.Managers;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import storage.history.HistoryRepository;
import storage.managers.TaskRepository;
import storage.managers.impl.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static model.TaskStatus.NEW;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public abstract class TaskManagerTest<T extends TaskRepository> {
    protected TaskRepository taskManager;
    protected HistoryRepository historyManager;

    // Абстрактный метод для создания конкретного экземпляра TaskManager
    abstract TaskRepository createTaskManager();

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


    //InMemoryTaskManager memoryTaskManagerTest = new InMemoryTaskManager(Managers.getDefaultHistory());

    /*
    @Test
    void getPrioritizedTasksShouldReturnTasksInOrderOfStartTime() {
        memoryTaskManagerTest.createNewTask(firstTaskTitle, firstTaskDescription,
                "NEW", LocalDateTime.now(), Duration.ofHours(1));
        memoryTaskManagerTest.createNewTask(secondTaskTitle, secondTaskDescription,
                "NEW", LocalDateTime.now().plusHours(2), Duration.ofHours(1));
        memoryTaskManagerTest.createNewTask(thirdTaskTitle, thirdTaskDescription,
                "NEW", LocalDateTime.now().minusHours(2), Duration.ofHours(1));


        Set<Task> prioritizedTasks = memoryTaskManagerTest.getPrioritizedTasks();

        // Проверяем, что задачи упорядочены по времени начала
        Iterator<Task> iterator = prioritizedTasks.iterator();
        Task previousTask = iterator.next();
        while (iterator.hasNext()) {
            Task currentTask = iterator.next();
            Assertions.assertTrue(previousTask.getStartTime().isBefore(currentTask.getStartTime())
                    || previousTask.getStartTime().isEqual(currentTask.getStartTime()));
            previousTask = currentTask;
        }
    }

    @Test
    void getEpicByIdShouldReturnNullIfIdDoesNotBelongToEpic() {
        // Предполагаем, что id обычной задачи равен 1
        long taskId = 1;
        memoryTaskManagerTest.createNewTask("Test Task", "This is a test task", "NEW",
                null, Duration.ZERO);

        // Пытаемся получить Epic по id обычной задачи
        Epic result = memoryTaskManagerTest.getEpicById(taskId);

        // Проверяем, что результат равен null
        assertNull(result);
    }

    @Test
    void getTaskById_ShouldReturnNullIfIdDoesNotBelongToTask() {
        // Предполагаем, что id подзадачи равен 1, а эпика - 2
        long subtaskId = 1;
        long nonExistentTaskId = 99; // ID, который не был вообще использован
        long epicId = memoryTaskManagerTest.createNewEpic(secondTaskTitle, secondTaskDescription).getId();
        memoryTaskManagerTest.createNewSubtask(firstTaskTitle, firstTaskDescription, "NEW",
                epicId, null, Duration.ZERO);

        // Пытаемся получить Task по id подзадачи
        Task subtaskResult = memoryTaskManagerTest.getTaskById(subtaskId);
        // Пытаемся получить Task по несуществующему id
        Task nonExistentTaskResult = memoryTaskManagerTest.getTaskById(nonExistentTaskId);

        // Проверяем, что результаты равны null
        assertNull(subtaskResult, "Должен вернуть null, так как id принадлежит подзадаче, а не обычной задаче");
        assertNull(nonExistentTaskResult, "Должен вернуть null, так как задача с таким id не существует");


    }

    @Test
    void actualizationEpicStatusAllSubtasksNew_ShouldSetEpicToNew() {
        Epic epic = memoryTaskManagerTest.createNewEpic(firstEpicTitle, firstEpicDescription);
        memoryTaskManagerTest.createNewSubtask(secondSubTaskTitleForFirstEpic, secondSubTaskDescriptionForFirstEpic,
                TaskStatus.NEW.name(),
                epic.getId(), null, Duration.ZERO);
        memoryTaskManagerTest.createNewSubtask(thirdSubTaskTitle, thirdSubTaskDescription, TaskStatus.NEW.name(),
                epic.getId(), null, Duration.ZERO);

        //memoryTaskManagerTest.actualizationEpicStatus(memoryTaskManagerTest.getListOfSubtaskByEpicId(epic.getId()).get(0));

        assertEquals(TaskStatus.NEW, epic.getStatus(), "Epic status should be NEW when all subtasks are NEW");
    }

    @Test
    void actualizationEpicStatusAllSubtasksDone_ShouldSetEpicToDone() {
        Epic epic = memoryTaskManagerTest.createNewEpic(firstEpicTitle, firstEpicDescription);
        memoryTaskManagerTest.createNewSubtask(secondSubTaskTitleForFirstEpic, secondSubTaskDescriptionForFirstEpic,
                TaskStatus.DONE.name(),
                epic.getId(), null, Duration.ZERO);
        memoryTaskManagerTest.createNewSubtask(thirdSubTaskTitle, thirdSubTaskDescription, TaskStatus.DONE.name(),
                epic.getId(), null, Duration.ZERO);
        epic = memoryTaskManagerTest.getEpicById(epic.getId());

        assertEquals(TaskStatus.DONE, epic.getStatus(), "Epic status should be DONE when all subtasks are DONE");
    }

    @Test
    void actualizationEpicStatusSubtasksNewAndDone_ShouldSetEpicToInProgress() {
        Epic epic = memoryTaskManagerTest.createNewEpic(firstEpicTitle, firstEpicDescription);
        memoryTaskManagerTest.createNewSubtask(secondTaskTitle, secondTaskDescription, TaskStatus.NEW.name(),
                epic.getId(), null, Duration.ZERO);
        memoryTaskManagerTest.createNewSubtask(thirdSubTaskTitle, thirdSubTaskDescription, TaskStatus.DONE.name(),
                epic.getId(), null, Duration.ZERO);

        memoryTaskManagerTest.actualizationEpicStatus(memoryTaskManagerTest.getListOfSubtaskByEpicId(epic.getId()).get(0));
        epic = memoryTaskManagerTest.getEpicById(epic.getId());

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Epic status should be IN_PROGRESS " +
                "when subtasks are in mixed states");
    }

    @Test
    void actualizationEpicStatusSubtasksInProgress_ShouldSetEpicToInProgress() {
        Epic epic = memoryTaskManagerTest.createNewEpic(firstEpicTitle, firstEpicDescription);
        memoryTaskManagerTest.createNewSubtask(thirdSubTaskTitle, thirdSubTaskDescription,
                TaskStatus.IN_PROGRESS.name(), epic.getId(), null, Duration.ZERO);

        memoryTaskManagerTest.actualizationEpicStatus(memoryTaskManagerTest.getListOfSubtaskByEpicId(epic.getId()).get(0));
        epic = memoryTaskManagerTest.getEpicById(epic.getId());
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Epic status should be IN_PROGRESS when any subtask is IN_PROGRESS");
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
    void createNewSubtaskWithNullDurationShouldSetDurationToZero() {

        Epic epic1 = memoryTaskManagerTest.createNewEpic(firstEpicTitle,
                firstEpicDescription);
        long epicId = epic1.getId();

        Subtask subtask = memoryTaskManagerTest.createNewSubtask(firstTaskTitle, firstTaskDescription, "NEW",
                epicId, LocalDateTime.now(), null);

        assertEquals(Duration.ZERO, subtask.getDuration(), "Duration should be set to ZERO when null is provided");
    }

    @Test
    void createNewSubtaskWithoutTimeOverlapShouldAddSubtask() {

        Epic epic1 = memoryTaskManagerTest.createNewEpic(firstEpicTitle,
                firstEpicDescription);
        long epicId = epic1.getId();
        // Добавление задачи, которая не пересекается по времени
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(1);
        memoryTaskManagerTest.createNewTask(secondTaskTitle, secondTaskDescription, "NEW",
                startTime.minusHours(2), duration);

        Subtask subtask = memoryTaskManagerTest.createNewSubtask(thirdTaskTitle, thirdTaskDescription,
                "NEW", epicId, startTime, duration);

        Assertions.assertNotNull(subtask, "Subtask should be added if there is no time overlap");
    }

    @Test
    void createNewSubtaskWithTimeOverlapShouldReturnNull() {
        Epic epic1 = memoryTaskManagerTest.createNewEpic(firstEpicTitle,
                firstEpicDescription);
        long epicId = epic1.getId();
        // Добавление задачи, которая пересекается по времени
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(1);
        memoryTaskManagerTest.createNewTask(firstTaskTitle, firstTaskDescription, "NEW", startTime, duration);

        Subtask subtask = memoryTaskManagerTest.createNewSubtask(secondTaskTitle, secondTaskDescription,
                "NEW", epicId, startTime, duration);

        assertNull(subtask, "Subtask should not be added if there is a time overlap");
    }


    @Test
    void addNewTask() {
        final Task savedTask = (memoryTaskManagerTest.createNewTask(firstTaskTitle, firstTaskDescription, "NEW",
                null, Duration.ZERO));
        Task task = new Task(secondTaskTitle, secondTaskDescription, NEW, savedTask.getId());
        Assertions.assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Object> tasks = memoryTaskManagerTest.getAllEntitiesByClass(Task.class);

        Assertions.assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }
    @Test
    void shouldNotAddTaskWithOverlap() {

        LocalDateTime start = LocalDateTime.now();
        Duration duration = Duration.ofHours(2);

        // Создание и добавление задачи
        Task existingTask = taskManager.createNewTask(firstTaskTitle, firstTaskDescription, "NEW", start, duration);
        Assertions.assertNotNull(existingTask, "Существующая задача должна быть успешно добавлена.");

        // Попытка добавить новую задачу, пересекающуюся по времени с уже существующей
        Task newTask = taskManager.createNewTask(secondTaskTitle, secondTaskDescription, "NEW", start.plusHours(1), duration);
        assertNull(newTask, "Новая задача не должна быть добавлена из-за пересечения времени выполнения.");
    }


    @Test
    void shouldAddTaskWithoutOverlap() {

        LocalDateTime start = LocalDateTime.now();
        Duration duration = Duration.ofHours(2);

        // Создание и добавление задачи, которая не должна пересекаться по времени с другими
        Task firstTask = taskManager.createNewTask(firstTaskTitle, firstTaskDescription, "NEW",
                start.minusHours(4), duration);
        Assertions.assertNotNull(firstTask, "Задача должна быть успешно добавлена.");

        // Попытка добавить еще одну задачу, которая также не пересекается по времени
        Task secondTask = taskManager.createNewTask(secondTaskTitle, secondTaskDescription, "NEW",
                start.plusHours(4), duration);
        Assertions.assertNotNull(secondTask, "Вторая задача должна быть успешно добавлена.");
    }

    @Test
    void checkThatSubtaskDoNotAddedHowOwnEpic() {
        long idNewSubtask = InMemoryTaskManager.getId();
        Subtask subtask = memoryTaskManagerTest.createNewSubtask(
                thirdSubTaskTitle,
                thirdSubTaskDescription,
                "NEW", ++idNewSubtask, null, Duration.ZERO);
        Assertions.assertNull(subtask, "Нельзя установить подзадачу как собственный Epic");
    }

    @Test
    void checkThatEpicDoNotAddedHowSubtask() {
        Epic epic = memoryTaskManagerTest.createNewEpic(firstEpicTitle,
                firstEpicDescription);

        Subtask subtask = memoryTaskManagerTest.createNewSubtask(
                secondSubTaskTitleForFirstEpic,
                secondSubTaskDescriptionForFirstEpic,
                "NEW", epic.getId(), null, Duration.ZERO);

        Object subtask2 = memoryTaskManagerTest.updateTask(subtask, epic.getId());

        Assertions.assertNull(subtask2, "Нельзя обновить эпик как подзадачу");
    }

    @Test
    void createNewTask() {
        Task task = memoryTaskManagerTest.createNewTask(firstTaskTitle,
                firstTaskDescription, "DONE", null, Duration.ZERO);

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
                epic1.getId(), null, Duration.ZERO);
        Assertions.assertEquals(Subtask.class, subtask.getClass(), "Класс создаваемых подзадач должен быть " +
                "subtask");

        Subtask temp = memoryTaskManagerTest.getSubtaskById(subtask.getId());
        Assertions.assertNotNull(temp, String.format("Не была найдена созданная subtask по id %d", subtask.getId()));
    }

    @Test
    void updateTask() {

        Task task1 = memoryTaskManagerTest.createNewTask(firstTaskTitle,
                firstTaskDescription, "DONE", null, Duration.ZERO);
        Task task2 = (Task) memoryTaskManagerTest.updateTask(new Task(secondTaskTitle,
                secondTaskDescription, NEW, task1.getId()), task1.getId());

        Assertions.assertEquals(task1, task2, "task1 != task2 по id");

    }

    @Test
    void testThatAllEntityWithUniqueId() {
        Task task1 = new Task(firstTaskTitle, firstTaskDescription, NEW, 0);
        Task task2 = new Task(secondTaskTitle, secondTaskDescription, NEW, 0);
        Task task3 = memoryTaskManagerTest.createNewTask(thirdTaskTitle,
                thirdTaskDescription, "DONE", null, Duration.ZERO);

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

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
        historyManager = taskManager.getHistoryManager();
    }

    @Test
    void removeSubTaskAndCheckThatThereDeletedFromEpicTest() {

        Epic firstEpic = taskManager.createNewEpic(firstEpicTitle, firstEpicDescription);
        Subtask secondTask = taskManager.createNewSubtask(secondSubTaskTitleForFirstEpic, secondSubTaskDescriptionForFirstEpic,
                "NEW", firstEpic.getId(), null, Duration.ZERO);
        Subtask thirdTask = taskManager.createNewSubtask(thirdSubTaskTitle, thirdSubTaskDescription,
                "NEW", firstEpic.getId(), null, Duration.ZERO);

        taskManager.removeTaskById(secondTask.getId());

        assertFalse(taskManager.getListOfSubtaskByEpicId(firstEpic.getId()).contains(secondTask),
                "Подзадача была удалена и не должна больше принадлежать эпику");
        assertFalse(taskManager.getListOfAllEntities().contains(secondTask.getId()),
                "Задача удалена и не должна содержать свой Id в списке задач");
        Assertions.assertTrue(taskManager.getListOfSubtaskByEpicId(firstEpic.getId()).contains(thirdTask),
                "Неудаленная подзадача должна присутствовать в списке подзадач эпика.");

    }

    @Test
    void shouldRemoveAllTasksAndSaveChanges() {
        // Инициализация FileBackedTaskManager с временным файлом

        // Добавление нескольких задач разных типов
        taskManager.createNewTask(firstEpicTitle, firstEpicDescription, TaskStatus.NEW.name(),
                LocalDateTime.now(), Duration.ofHours(1));
        Epic epic = taskManager.createNewEpic(firstEpicTitle, firstEpicDescription);
        taskManager.createNewSubtask(secondTaskTitle, secondTaskDescription,
                TaskStatus.NEW.name(), epic.getId(), LocalDateTime.now().plusHours(2), Duration.ofHours(1));

        // Удаление всех задач
        int deletedCount = taskManager.removeEntityFromKanban(Task.class);
        Assertions.assertEquals(1, deletedCount, "Должна быть удалена одна задача.");

        Assertions.assertTrue(taskManager.getListOfAllEntities().stream()
                        .noneMatch(task -> task.getClass().equals(Task.class)),
                "В списке не должно остаться задач после удаления.");
    }


    @Test
    void removeEpicWithSubtaskFromAndCheckHistoryTest() {

        Epic firstEpic = taskManager.createNewEpic(firstEpicTitle, firstEpicDescription);
        Subtask secondTask = taskManager.createNewSubtask(secondSubTaskTitleForFirstEpic,
                secondSubTaskTitleForFirstEpic, "NEW", firstEpic.getId(), null, Duration.ZERO);
        Subtask thirdTask = taskManager.createNewSubtask(thirdSubTaskTitle, thirdSubTaskDescription,
                "NEW", firstEpic.getId(), null, Duration.ZERO);

        Task fourthTask = taskManager.createNewTask(fourTaskTitle, fourTaskDescription, "NEW",
                null, Duration.ZERO);
        // Добавляем задачи в историю просмотров
        taskManager.getEpicById(firstEpic.getId());
        taskManager.getSubtaskById(secondTask.getId());
        taskManager.getSubtaskById(thirdTask.getId());
        taskManager.getTaskById(fourthTask.getId());
        // Удаляем эпик вместе с подзадачами
        taskManager.removeTaskById(firstEpic.getId());

        assertFalse(historyManager.getHistory().contains(secondTask), "Вторая задача должна быть удалена");
        assertFalse(historyManager.getHistory().contains(firstEpic), "Первая задача не должна была остаться");
        assertFalse(historyManager.getHistory().contains(thirdTask), "Третья задача не должна была остаться");
        Assertions.assertTrue(historyManager.getHistory().contains(fourthTask), "Четвертая задача должна была остаться");
    }


     */
}
