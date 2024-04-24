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
import service.impl.EpicService;
import service.impl.HistoryService;
import service.impl.SubtaskService;
import service.impl.TaskService;
import storage.history.HistoryRepository;
import storage.history.InMemoryHistoryManager;
import storage.managers.TaskRepository;
import storage.managers.impl.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static model.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskRepository> {


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

    HistoryRepository historyRepository = Managers.getDefaultHistory();
    TaskRepository taskRepository = Managers.getDefault(historyRepository);
    HistoryService historyService = new HistoryService(historyRepository);
    TaskService taskService = new TaskService(taskRepository, historyService);
    EpicService epicService = new EpicService(taskRepository, historyService);
    SubtaskService subtaskService = new SubtaskService(taskRepository, historyService, epicService);


    @BeforeEach
    void setUp() {
        historyRepository = Managers.getDefaultHistory();
        taskRepository = Managers.getDefault(historyRepository);

        historyService = new HistoryService(historyRepository);
        taskService = new TaskService(taskRepository, historyService);
        epicService = new EpicService(taskRepository, historyService);
        epicService.setSubtaskService(subtaskService);
        subtaskService = new SubtaskService(taskRepository, historyService, epicService);

    }


    @Test
    void getPrioritizedTasksShouldReturnTasksInOrderOfStartTime() {

        taskService.createTask(new Task(firstTaskTitle, firstTaskDescription,
                NEW, LocalDateTime.now(), Duration.ofHours(1)));
        taskService.createTask(new Task(secondTaskTitle, secondTaskDescription,
                NEW, LocalDateTime.now().plusHours(2), Duration.ofHours(1)));
        taskService.createTask(new Task(thirdTaskTitle, thirdTaskDescription,
                NEW, LocalDateTime.now().minusHours(2), Duration.ofHours(1)));


        Set<Task> prioritizedTasks = taskRepository.getPrioritizedTasks();

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
    void checkIsEpic() {
        Epic epic1 = epicService.createEpic(new Epic(firstEpicTitle,
                firstEpicDescription, null));
        boolean result = subtaskService.checkIsEpic(epic1.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void checkIsNotEpic() {
        boolean result = subtaskService.checkIsEpic(1);
        assertFalse(result);
    }


    @Test
    void createNewSubtaskWithTimeOverlapShouldReturnNull() {
        Epic epic1 = epicService.createEpic(new Epic(firstEpicTitle,
                firstEpicDescription, null));
        long epicId = epic1.getId();
        // Добавление задачи, которая пересекается по времени
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(1);
        taskService.createTask(new Task(firstTaskTitle, firstTaskDescription, NEW, startTime, duration));

        Subtask subtask = subtaskService.createSubtask(new Subtask(secondTaskTitle, secondTaskDescription,
                NEW, epicId, startTime, duration));

        assertNull(subtask, "Subtask should not be added if there is a time overlap");
    }


    @Test
    void addNewTask() {
        final Task savedTask = (taskService.createTask(new Task(firstTaskTitle, firstTaskDescription, NEW,
                null, Duration.ZERO)));
        Task task = new Task(firstTaskTitle, firstTaskDescription, NEW, savedTask.getId());
        Assertions.assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskRepository.getAllEntitiesByClass(Task.class);

        Assertions.assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void shouldNotAddTaskWithOverlap() {

        LocalDateTime start = LocalDateTime.now();
        Duration duration = Duration.ofHours(2);

        // Создание и добавление задачи
        Task existingTask = taskService.createTask(new Task(firstTaskTitle, firstTaskDescription, NEW, start, duration));
        Assertions.assertNotNull(existingTask, "Существующая задача должна быть успешно добавлена.");

        // Попытка добавить новую задачу, пересекающуюся по времени с уже существующей
        Task newTask = taskService.createTask(new Task(secondTaskTitle, secondTaskDescription, NEW, start.plusHours(1), duration));
        assertNull(newTask, "Новая задача не должна быть добавлена из-за пересечения времени выполнения.");
    }


    @Test
    void shouldAddTaskWithoutOverlap() {

        LocalDateTime start = LocalDateTime.now();
        Duration duration = Duration.ofHours(2);

        // Создание и добавление задачи, которая не должна пересекаться по времени с другими
        Task firstTask = taskService.createTask(new Task(firstTaskTitle, firstTaskDescription, NEW,
                start.minusHours(4), duration));
        Assertions.assertNotNull(firstTask, "Задача должна быть успешно добавлена.");

        // Попытка добавить еще одну задачу, которая также не пересекается по времени
        Task secondTask = taskService.createTask(new Task(secondTaskTitle, secondTaskDescription, NEW,
                start.plusHours(4), duration));
        Assertions.assertNotNull(secondTask, "Вторая задача должна быть успешно добавлена.");
    }

    @Test
    void checkThatSubtaskDoNotAddedHowOwnEpic() {
        long idNewSubtask = TaskRepository.getId();
        Subtask subtask = subtaskService.createSubtask(new Subtask(
                thirdSubTaskTitle,
                thirdSubTaskDescription,
                NEW, ++idNewSubtask, null, Duration.ZERO));
        assertNull(subtask, "Нельзя установить подзадачу как собственный Epic");
    }


    @Test
    void createTask() {
        Task task = taskService.createTask(new Task(firstTaskTitle,
                firstTaskDescription, TaskStatus.DONE, null, Duration.ZERO));

        assertEquals(Task.class, task.getClass(), "Класс создаваемых подзадач должен быть " +
                "Task");

        Task temp = taskRepository.getTaskById(task.getId());
        Assertions.assertNotNull(temp, String.format("Не была найдена созданная task по id %d", task.getId()));
    }

    @Test
    void createEpic() {
        Epic epic1 = epicService.createEpic(new Epic(firstEpicTitle,
                firstEpicDescription, null));

        assertEquals(Epic.class, epic1.getClass(), "Класс создаваемых подзадач должен быть " +
                "Epic");

        Epic temp = taskRepository.getEpicById(epic1.getId());
        Assertions.assertNotNull(temp, String.format("Не был найден созданный epic по id %d", epic1.getId()));
    }


    @Test
    void updateTask() {

        Task task1 = taskService.createTask(new Task(firstTaskTitle,
                firstTaskDescription, TaskStatus.DONE, null, Duration.ZERO));
        Task task2 = (Task) taskService.updateTask(new Task(firstTaskTitle,
                firstTaskDescription, TaskStatus.DONE, task1.getId()));

        assertEquals(task1, task2, "task1 != task2 по id");

    }

    @Test
    void testThatAllEntityWithUniqueId() {
        Task task1 = taskService.createTask(new Task(firstTaskTitle, firstTaskDescription, NEW, 0));
        Task task2 = taskService.createTask(new Task(secondTaskTitle, secondTaskDescription, NEW, 0));
        Task task3 = taskService.createTask(new Task(thirdTaskTitle,
                thirdTaskDescription, TaskStatus.DONE, null, Duration.ZERO));

        Epic epic0 = new Epic(firstEpicTitle, firstEpicDescription, NEW, 0);
        Epic epic1 = epicService.createEpic(new Epic(secondEpicTitle,
                secondEpicDescription, null));
        Epic epic2 = epicService.createEpic(new Epic(thirdEpicTitle,
                thirdEpicDescription, null));

        Epic epic3 = (Epic) taskService.updateTask(epic0);
        assertNull(epic3, "Переданный объект должен иметь тот же id что и назначаемый id");

        HashMap<Long, Integer> listOfId = new HashMap<>();

        List<Task> list = taskRepository.getListOfAllEntities();
        for (Task task : list) {

            listOfId.merge(task.getId(), 1, Integer::sum);
            assertEquals(1, listOfId.get(task.getId()), String.format("На kanban доске " +
                    "обнаружены дубли id %d", task.getId()));
        }
    }

}
