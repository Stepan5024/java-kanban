import controller.history.HistoryManager;
import controller.history.InMemoryHistoryManager;
import controller.managers.FileBackedTaskManager;
import controller.managers.InMemoryTaskManager;
import controller.managers.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import manager.Managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            runUserScript3();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void runUserScript3() throws IOException {
        // Текстовые метки
        String taskLabel1 = "Задача 1";
        String taskDescription1 = "Описание задачи 1";
        String taskLabel2 = "Задача 2";
        String taskDescription2 = "Описание задачи 2";
        String epicLabel1 = "Эпик 1";
        String epicDescription1 = "Описание эпика 1";
        String subtaskLabel1 = "Подзадача 1 эпика 1";
        String subtaskDescription1 = "Описание подзадачи 1";
        String subtaskLabel2 = "Подзадача 2 эпика 1";
        String subtaskDescription2 = "Описание подзадачи 2";

        // Путь к файлу для сохранения и загрузки менеджера задач
        File file = File.createTempFile("FileBackedTaskManager", ".csv");

        // Создание и инициализация менеджера задач
        FileBackedTaskManager manager = new FileBackedTaskManager(new InMemoryHistoryManager(), file.getAbsolutePath());

        // Создание задач, эпиков и подзадач с использованием текстовых меток
        Task task1 = manager.createNewTask(taskLabel1, taskDescription1, TaskStatus.NEW.name());
        Task task2 = manager.createNewTask(taskLabel2, taskDescription2, TaskStatus.DONE.name());
        Epic epic1 = manager.createNewEpic(epicLabel1, epicDescription1);
        Subtask subtask1 = manager.createNewSubtask(subtaskLabel1, subtaskDescription1, TaskStatus.NEW.name(), epic1.getId());
        Subtask subtask2 = manager.createNewSubtask(subtaskLabel2, subtaskDescription2, TaskStatus.DONE.name(), epic1.getId());

        // Создание нового менеджера из этого же файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file, new InMemoryHistoryManager());

        // Проверка, что все задачи, эпики и подзадачи загрузились корректно
        System.out.println("Проверка загруженных данных:");
        printAllTasks(loadedManager);

        // Удаление временного файла
        file.deleteOnExit();
    }

    private static void runUserScript2() {

        String firstTaskTitle = "Переезд";
        String firstTaskDescription = "Новая квартира по адресу Москва ул. Дружбы";
        String secondTaskTitle = "Пример второй задачи";
        String secondTaskDescription = "Описание второй задачи";
        String firstEpicTitle = "Написание диплома";
        String firstEpicDescription = "Для выпуска из университета";
        String thirdSubTaskTitle = "Чтение литературы";
        String thirdSubTaskDescription = "Для выпуска из университета";
        String secondEpicTitle = "Сдача ITELS";
        String secondEpicDescription = "Для магистратуры";
        String secondSubTaskTitleForFirstEpic = "Подзадача в рамках эпика";
        String secondSubTaskDescriptionForFirstEpic = "Пум-Пум";

        InMemoryTaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
        InMemoryHistoryManager historyManager = (InMemoryHistoryManager) taskManager.getHistoryManager();

        System.out.println("Создаем две задачи ...");

        Task task1 = taskManager.createNewTask(firstTaskTitle, firstTaskDescription, "NEW");
        Task task2 = taskManager.createNewTask(secondTaskTitle, secondTaskDescription, "NEW");

        System.out.println("Создаем эпик с тремя подзадачами ...");

        Epic epic1 = taskManager.createNewEpic(firstEpicTitle, firstEpicDescription);

        Subtask subtask1 = taskManager.createNewSubtask(
                secondSubTaskTitleForFirstEpic,
                secondSubTaskDescriptionForFirstEpic,
                "NEW",
                epic1.getId());
        Subtask subtask2 = taskManager.createNewSubtask(
                thirdSubTaskTitle,
                thirdSubTaskDescription,
                "NEW",
                epic1.getId());
        Subtask subtask3 = taskManager.createNewSubtask(
                thirdSubTaskTitle,
                thirdSubTaskDescription,
                "DONE",
                epic1.getId());
        System.out.println("Создаем один эпик без подзадач ...");

        Epic epic2 = taskManager.createNewEpic(secondEpicTitle,
                secondEpicDescription);

        System.out.println("Запрос созданных задач несколько раз в разном порядке ...");


        // Запрос созданных задач в разном порядке
        System.out.printf("Запрос задачи c id = %d\n\n", task1.getId());
        taskManager.getTaskById(task1.getId());
        printHistory(historyManager);

        System.out.printf("Запрос эпика с id = %d\n\n", epic1.getId());
        taskManager.getEpicById(epic1.getId());
        printHistory(historyManager);

        System.out.printf("Запрос подзадачи с id = %d\n\n", subtask3.getId());
        taskManager.getSubtaskById(subtask3.getId());
        printHistory(historyManager);

        System.out.printf("Запрос задачи c id = %d\n\n", task1.getId());
        taskManager.getTaskById(task1.getId());
        printHistory(historyManager);

        System.out.printf("Запрос подзадачи с id = %d\n\n", subtask1.getId());
        taskManager.getSubtaskById(subtask1.getId());
        printHistory(historyManager);

        System.out.printf("Запрос задачи с id = %d\n\n", task2.getId());
        taskManager.getTaskById(task2.getId());
        printHistory(historyManager);

        System.out.printf("Запрос задачи c id = %d\n\n", task1.getId());
        taskManager.getTaskById(task1.getId());
        printHistory(historyManager);

        System.out.printf("Удаляю задачу с Id = %d, которая есть в истории ...\n\n", task1.getId());
        taskManager.removeTaskById(task1.getId());
        printHistory(historyManager);

        System.out.printf("\nУдаляю эпик с Id = %d, который есть в истории и имеет три подзадачи...\n",
                epic1.getId());
        taskManager.removeTaskById(epic1.getId());
        printHistory(historyManager);

    }

    private static void runUserScript1() {
        String firstTaskTitle = "Переезд";
        String firstTaskDescription = "Новая квартира по адресу Москва ул. Дружбы";
        String secondTaskTitle = "Тест-лаба";
        String secondTaskDescription = "Описание тест-лабы";
        String firstEpicTitle = "Написание диплома";
        String firstEpicDescription = "Для выпуска из университета";
        String thirdSubTaskTitle = "Чтение литературы";
        String thirdSubTaskDescription = "Для выпуска из университета";
        String secondEpicTitle = "Сдача IELTS";
        String secondEpicDescription = "Для магистратуры";
        String secondSubTaskTitleForFirstEpic = "Подзадача в рамках эпика";
        String secondSubTaskDescriptionForFirstEpic = "Пум-Пум";

        InMemoryTaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
        Task task1 = taskManager.createNewTask(firstTaskTitle, firstTaskDescription, "NEW");
        Task task2 = taskManager.createNewTask(secondTaskTitle, secondTaskDescription, "NEW");
        Epic epic1 = taskManager.createNewEpic(firstEpicTitle,
                firstEpicDescription);
        Epic epic2 = taskManager.createNewEpic(secondEpicTitle,
                secondEpicDescription);
        Epic epic3 = taskManager.createNewEpic(firstEpicTitle,
                firstEpicDescription);
        Subtask subtask1 = taskManager.createNewSubtask(
                secondSubTaskTitleForFirstEpic,
                thirdSubTaskDescription,
                "NEW",
                epic1.getId());
        Subtask subtask2 = taskManager.createNewSubtask(
                thirdSubTaskTitle,
                thirdSubTaskDescription,
                "NEW",
                epic1.getId());
        Subtask subtask3 = taskManager.createNewSubtask(
                secondSubTaskTitleForFirstEpic,
                secondSubTaskDescriptionForFirstEpic,
                "DONE",
                epic1.getId());
        Subtask subtask4 = taskManager.createNewSubtask(
                thirdSubTaskTitle,
                thirdSubTaskDescription,
                "IN_PROGRESS",
                epic2.getId());
        Task task3 = taskManager.createNewTask(firstTaskTitle,
                firstTaskDescription, "NEW");

        printAllList(taskManager);
        System.out.printf("Подзадача до обновления %s\n", subtask4);

        subtask4 = (Subtask) taskManager.updateTask(new Subtask("newSubtask",
                "newDescription",
                TaskStatus.valueOf("DONE"),
                subtask4.getEpicId(),
                subtask4.getId()), subtask4.getId());

        System.out.printf("Подзадача после обновления %s\n", subtask4);

        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getSubtaskById(subtask4.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getEpicById(epic1.getId());

        InMemoryHistoryManager historyManager = (InMemoryHistoryManager) taskManager.getHistoryManager();
        printHistory(historyManager);

        printAllTasks(taskManager);

        System.out.println("Удаляю все подзадачи");
        taskManager.removeEntityFromKanban(Subtask.class);
        printAllList(taskManager);


        List<Object> epics = taskManager.getAllEntitiesByClass(Epic.class);
        System.out.println("Всего эпиков " + epics.size());
        System.out.println("Список всех эпиков:");
        for (Object epic : epics) {
            System.out.println(epic);
        }
        printAllList(taskManager);

        System.out.printf("Удаляю эпик id = %d\n", epic1.getId());
        int result = taskManager.removeTaskById(epic1.getId());
        if (result > 0) {
            System.out.printf("Эпик id = %d удален с канбан доски\n", epic1.getId());
        }
        printAllList(taskManager);

        System.out.printf("Задача до обновления %s\n", task2);
        task2 = (Task) taskManager.updateTask(new Task("newTitle after updating",
                "newDescription",
                TaskStatus.valueOf("DONE"),
                task2.getId()), task2.getId());
        System.out.printf("Задача после обновления %s\n", task2);
        printAllList(taskManager);

        List<Object> tasks = taskManager.getAllEntitiesByClass(Task.class);
        System.out.println("Всего задач " + tasks.size());
        System.out.println("Список всех задач:");
        for (Object task : tasks) {
            System.out.println(task);
        }
        System.out.println();

        System.out.println("Удаляю все задачи");
        System.out.println("Удалено " + taskManager.removeEntityFromKanban(Task.class));
        printAllList(taskManager);
    }

    private static void printHistory(InMemoryHistoryManager historyManager) {
        ArrayList<Task> listHistory = historyManager.getHistory();
        System.out.printf("Размер истории просмотра количества задач равен %d\n", listHistory.size());
        for (Task task : listHistory) {
            System.out.println(task);
        }
        System.out.println();
    }

    public static void printAllList(TaskManager taskManager) {
        System.out.println("\nВесь список канбан доски");
        List<Object> allTasks = taskManager.getListOfAllEntities();

        for (Object obj : allTasks) {
            System.out.println(obj);
        }
        System.out.println();
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println();
        System.out.println("Задачи:");
        for (Object task : manager.getAllEntitiesByClass(Task.class)) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Object epic : manager.getAllEntitiesByClass(Epic.class)) {
            System.out.println(epic);

            for (Task task : manager.getListOfSubtaskByEpicId(((Epic) epic).getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Object subtask : manager.getAllEntitiesByClass(Subtask.class)) {
            System.out.println(subtask);
        }
        // Используйте существующий объект InMemoryHistoryManager через TaskManager
        HistoryManager historyManager = manager.getHistoryManager();
        System.out.println("История:");
        for (Task task : historyManager.getHistory()) {
            System.out.println(task);
        }
        System.out.println();
    }
}
