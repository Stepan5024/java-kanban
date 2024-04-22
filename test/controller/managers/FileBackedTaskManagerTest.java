package controller.managers;

import manager.Managers;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import storage.managers.impl.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


import static java.io.File.createTempFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileBackedTaskManagerTest {
    /* extends TaskManagerTest<FileBackedTaskManager> {


    @Test
    public void testSaveAndLoadEmptyFile() throws IOException {
        File tempFile = createTempFile("emptyTasks", ".csv");
        tempFile.deleteOnExit();

        HistoryManager historyManager = new InMemoryHistoryManager();
        FileBackedTaskManager manager = new FileBackedTaskManager(historyManager, tempFile.getAbsolutePath());
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile, historyManager);
        Assertions.assertTrue(loadedManager.getListOfAllEntities().isEmpty(), "Список задач должен быть пуст после загрузки пустого менеджера");
    }

    @Test
    public void testSaveAndLoadMultipleTasks() throws IOException {
        File tempFile = createTempFile("multipleTasks", ".csv");
        tempFile.deleteOnExit();

        HistoryManager historyManager = new InMemoryHistoryManager();
        FileBackedTaskManager manager = new FileBackedTaskManager(historyManager, tempFile.getAbsolutePath());

        manager.createNewTask(firstTaskTitle, firstTaskDescription, "NEW", null, Duration.ZERO);
        Epic epic = manager.createNewEpic(firstEpicTitle, firstEpicDescription);
        manager.createNewSubtask(secondSubTaskTitleForFirstEpic, secondSubTaskDescriptionForFirstEpic,
                "NEW", epic.getId(), null, Duration.ZERO);


        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile, historyManager);
        assertEquals(3, loadedManager.getListOfAllEntities().size());
    }

    @Test
    void testSaveAndLoadManagerWithTasks() throws IOException {
        File tempFile = createTempFile("managerWithTasks", ".csv");
        tempFile.deleteOnExit();
        HistoryManager historyManager = new InMemoryHistoryManager();
        FileBackedTaskManager manager = new FileBackedTaskManager(historyManager, tempFile.getAbsolutePath());

        // Добавляем задачи
        Task task1 = manager.createNewTask(firstTaskTitle, firstTaskDescription, "NEW", null, Duration.ZERO);
        Epic epic1 = manager.createNewEpic(firstEpicTitle, firstEpicDescription);
        Subtask subtask1 = manager.createNewSubtask(thirdSubTaskTitle, thirdSubTaskDescription,
                "NEW", epic1.getId(), null, Duration.ZERO);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile, historyManager);

        // Проверки
        assertEquals(3, loadedManager.getListOfAllEntities().size(), "Количество задач после загрузки должно соответствовать количеству сохраненных задач");
        assertNotNull(loadedManager.getTaskById(task1.getId()), "Задача должна быть загружена");
        assertNotNull(loadedManager.getEpicById(epic1.getId()), "Эпик должен быть загружен");
        assertNotNull(loadedManager.getSubtaskById(subtask1.getId()), "Подзадача должна быть загружена");
    }

    @Test
    void saveShouldNotThrowExceptionWhenFilePathIsValid() throws IOException {
        String filePath = createTempFile("FileBackedTaskManager", ".csv").getPath();
        FileBackedTaskManager manager = new FileBackedTaskManager(new InMemoryHistoryManager(), filePath);

        // Добавление задачи для сохранения
        manager.createNewTask(firstTaskTitle, firstTaskDescription, TaskStatus.NEW.name(),
                LocalDateTime.now(), Duration.ofHours(1));

        // Проверка, что сохранение не вызывает исключения
        Assertions.assertDoesNotThrow(manager::save, "Метод save должен корректно сохранять данные в файл без выброса исключений.");
    }

    @Test
    void saveShouldThrowManagerSaveExceptionWhenFilePathIsInvalid() {
        // filePath указывает на несуществующую директорию
        String invalidFilePath = "/path/to/nonexistent/directory/file.csv";
        FileBackedTaskManager manager = new FileBackedTaskManager(new InMemoryHistoryManager(), invalidFilePath);

        // Проверка, что попытка сохранения выбросит ManagerSaveException из-за невозможности найти файл
        Assertions.assertThrows(ManagerSaveException.class, manager::save,
                "Метод save должен выбрасывать ManagerSaveException при попытке сохранения в несуществующий файл.");
    }

    @Test
    void createNewTaskShouldThrowExceptionWhenSaveFails() {
        // filePath указывает на несуществующую директорию, вызывающую ошибку при сохранении
        String invalidFilePath = "/path/to/nonexistent/directory/file.csv";
        FileBackedTaskManager manager = new FileBackedTaskManager(new InMemoryHistoryManager(), invalidFilePath);

        // Ожидаем, что будет выброшено исключение ManagerSaveException при попытке создания новой задачи,
        // так как это приведет к вызову метода save(), неспособного выполнить сохранение.
        Assertions.assertThrows(ManagerSaveException.class, () -> {
            manager.createNewTask(firstTaskTitle, firstTaskDescription, TaskStatus.NEW.name(), LocalDateTime.now(), Duration.ofHours(1));
        }, "Метод createNewTask должен выбрасывать ManagerSaveException при ошибке сохранения задачи.");
    }

    @Test
    void shouldConvertCommaSeparatedStringToIntList() {
        String testValue = "1,2,3,4,5";
        List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> actual = historyFromString(testValue);
        Assertions.assertEquals(expected, actual, "Список целых чисел не соответствует ожидаемому.");
    }

    @Test
    void shouldHandleEmptyString() {
        String testValue = "";
        List<Integer> expected = List.of();
        List<Integer> actual = historyFromString(testValue);
        Assertions.assertTrue(actual.isEmpty(), "Пустая строка должна приводить к пустому списку.");
    }

    @Test
    void shouldHandleSingleValue() {
        String testValue = "42";
        List<Integer> expected = List.of(42);
        List<Integer> actual = historyFromString(testValue);
        Assertions.assertEquals(expected, actual, "Список из одного значения не соответствует ожидаемому.");
    }

    @Test
    void shouldIgnoreSpaces() {
        String testValue = "1, 2, 3 , 4 ,5";
        List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> actual = historyFromString(testValue);
        Assertions.assertEquals(expected, actual, "Список целых чисел должен корректно обрабатывать пробелы.");
    }

    @Test
    void shouldThrowNumberFormatExceptionForInvalidValues() {
        String testValue = "1,2,abc,4,5";
        Assertions.assertThrows(NumberFormatException.class, () -> historyFromString(testValue),
                "Строка с некорректными значениями должна приводить к NumberFormatException.");
    }
    @Override
    FileBackedTaskManager createTaskManager() {
        try {
            File file = createTempFile("FileBackedTaskManager", ".csv");
            return new FileBackedTaskManager(Managers.getDefaultHistory(), file.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

     */
}
