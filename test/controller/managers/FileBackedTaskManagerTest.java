package controller.managers;

import controller.history.HistoryManager;
import controller.history.InMemoryHistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    static String firstTaskTitle;
    static String firstTaskDescription;
    static String secondTaskTitle;
    static String secondTaskDescription;
    static String firstEpicTitle;
    static String firstEpicDescription;
    static String thirdSubTaskTitle;
    static String thirdSubTaskDescription;
    static String secondSubTaskTitleForFirstEpic;
    static String secondSubTaskDescriptionForFirstEpic;

    @BeforeAll
    static void initTextLabels() {
        firstTaskTitle = "Переезд";
        firstTaskDescription = "Новая квартира по адресу Москва ул. Дружбы";
        secondTaskTitle = "Пример второй задачи";
        secondTaskDescription = "Описание второй задачи";
        firstEpicTitle = "Написание диплома";
        firstEpicDescription = "Для выпуска из университета";
        secondSubTaskTitleForFirstEpic = "Заголовок 2 подзадачи";
        secondSubTaskDescriptionForFirstEpic = "Описание 2 подзадачи";
        thirdSubTaskTitle = "Чтение литературы";
        thirdSubTaskDescription = "Из рекомендованной руководителем";
    }

    @Test
    public void testSaveAndLoadEmptyFile() throws IOException {
        File tempFile = File.createTempFile("emptyTasks", ".csv");
        tempFile.deleteOnExit();

        HistoryManager historyManager = new InMemoryHistoryManager();
        FileBackedTaskManager manager = new FileBackedTaskManager(historyManager, tempFile.getAbsolutePath());
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile, historyManager);
        Assertions.assertTrue(loadedManager.getListOfAllEntities().isEmpty(), "Список задач должен быть пуст после загрузки пустого менеджера");
    }

    @Test
    public void testSaveAndLoadMultipleTasks() throws IOException {
        File tempFile = File.createTempFile("multipleTasks", ".csv");
        tempFile.deleteOnExit();

        HistoryManager historyManager = new InMemoryHistoryManager();
        FileBackedTaskManager manager = new FileBackedTaskManager(historyManager, tempFile.getAbsolutePath());

        manager.createNewTask(firstTaskTitle, firstTaskDescription, "NEW");
        Epic epic = manager.createNewEpic(firstEpicTitle, firstEpicDescription);
        manager.createNewSubtask(secondSubTaskTitleForFirstEpic, secondSubTaskDescriptionForFirstEpic, "NEW", epic.getId());


        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile, historyManager);
        assertEquals(3, loadedManager.getListOfAllEntities().size());
    }

    @Test
    void testSaveAndLoadManagerWithTasks() throws IOException {
        File tempFile = File.createTempFile("managerWithTasks", ".csv");
        tempFile.deleteOnExit();
        HistoryManager historyManager = new InMemoryHistoryManager();
        FileBackedTaskManager manager = new FileBackedTaskManager(historyManager, tempFile.getAbsolutePath());

        // Добавляем задачи
        Task task1 = manager.createNewTask(firstTaskTitle, firstTaskDescription, "NEW");
        Epic epic1 = manager.createNewEpic(firstEpicTitle, firstEpicDescription);
        Subtask subtask1 = manager.createNewSubtask(thirdSubTaskTitle, thirdSubTaskDescription, "NEW", epic1.getId());

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile, historyManager);

        // Проверки
        assertEquals(3, loadedManager.getListOfAllEntities().size(), "Количество задач после загрузки должно соответствовать количеству сохраненных задач");
        assertNotNull(loadedManager.getTaskById(task1.getId()), "Задача должна быть загружена");
        assertNotNull(loadedManager.getEpicById(epic1.getId()), "Эпик должен быть загружен");
        assertNotNull(loadedManager.getSubtaskById(subtask1.getId()), "Подзадача должна быть загружена");
    }


    @Test
    void removeSubTaskAndCheckThatThereDeletedFromEpicTest() throws IOException {
        // Создаем временный файл для теста
        File tempFile = File.createTempFile("testRemoveSubtask", ".csv");
        tempFile.deleteOnExit();

        // Инициализируем FileBackedTaskManager с временным файлом
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        FileBackedTaskManager manager = new FileBackedTaskManager(historyManager, tempFile.getAbsolutePath());

        // Создаем эпик и подзадачи
        Epic firstEpic = manager.createNewEpic(firstEpicTitle, firstEpicDescription);
        Subtask secondTask = manager.createNewSubtask(thirdSubTaskTitle, thirdSubTaskDescription, TaskStatus.NEW.name(), firstEpic.getId());
        Subtask thirdTask = manager.createNewSubtask(secondSubTaskTitleForFirstEpic, secondSubTaskDescriptionForFirstEpic, TaskStatus.NEW.name(), firstEpic.getId());

        // Удаляем одну подзадачу
        manager.removeTaskById(secondTask.getId());

        // Загружаем менеджер задач из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile, historyManager);

        // Проверяем, что удаленная подзадача отсутствует в списке подзадач эпика
        assertFalse(loadedManager.getListOfSubtaskByEpicId(firstEpic.getId()).contains(secondTask), "Удаленная подзадача не должна присутствовать в списке подзадач эпика.");

        // Проверяем, что удаленная подзадача отсутствует в общем списке задач
        assertFalse(loadedManager.getListOfAllEntities().contains(secondTask), "Удаленная подзадача не должна присутствовать в общем списке задач.");

        // Дополнительная проверка на наличие неудаленной подзадачи
        assertTrue(loadedManager.getListOfSubtaskByEpicId(firstEpic.getId()).contains(thirdTask), "Неудаленная подзадача должна присутствовать в списке подзадач эпика.");
    }

    @Test
    void removeEpicWithSubtaskFromAndCheckHistoryTest() throws IOException {
        // Создаем временный файл для теста
        File tempFile = File.createTempFile("testRemoveEpicWithSubtasks", ".csv");
        tempFile.deleteOnExit();

        // Инициализируем FileBackedTaskManager с временным файлом
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        FileBackedTaskManager manager = new FileBackedTaskManager(historyManager, tempFile.getAbsolutePath());

        // Создаем эпик, подзадачи и отдельную задачу
        Epic firstEpic = manager.createNewEpic(firstEpicTitle, firstEpicDescription);
        Subtask secondTask = manager.createNewSubtask(secondTaskTitle, secondTaskDescription, TaskStatus.NEW.name(), firstEpic.getId());
        Subtask thirdTask = manager.createNewSubtask(thirdSubTaskTitle, thirdSubTaskDescription, TaskStatus.NEW.name(), firstEpic.getId());
        Task fourthTask = manager.createNewTask(firstTaskTitle, firstTaskDescription, TaskStatus.NEW.name());

        // Добавляем задачи в историю просмотров
        manager.getEpicById(firstEpic.getId());
        manager.getSubtaskById(secondTask.getId());
        manager.getSubtaskById(thirdTask.getId());
        manager.getTaskById(fourthTask.getId());

        // Удаляем эпик вместе с подзадачами
        manager.removeTaskById(firstEpic.getId());

        // Загружаем менеджер задач из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile, historyManager);

        assertFalse(loadedManager.getHistoryManager().getHistory().contains(secondTask), "Вторая задача должна быть удалена из истории");
        assertFalse(loadedManager.getHistoryManager().getHistory().contains(firstEpic), "Эпик должен быть удален из истории");
        assertFalse(loadedManager.getHistoryManager().getHistory().contains(thirdTask), "Третья задача должна быть удалена из истории");
        assertTrue(loadedManager.getHistoryManager().getHistory().contains(fourthTask), "Четвертая задача должна остаться в истории");
    }
}
