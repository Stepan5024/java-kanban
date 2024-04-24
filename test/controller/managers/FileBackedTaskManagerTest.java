package controller.managers;

import manager.Managers;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.impl.EpicService;
import service.impl.HistoryService;
import service.impl.SubtaskService;
import service.impl.TaskService;
import storage.history.HistoryRepository;
import storage.history.InMemoryHistoryManager;
import storage.managers.TaskRepository;
import storage.managers.impl.FileBackedTaskManager;
import storage.managers.impl.ManagerSaveException;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static java.io.File.createTempFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static storage.managers.impl.FileBackedTaskManager.historyFromString;

public class FileBackedTaskManagerTest
        extends TaskManagerTest<FileBackedTaskManager> {

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
        subtaskService = new SubtaskService(taskRepository, historyService, epicService);
        epicService.setSubtaskService(subtaskService);

    }

    @Test
    public void testSaveAndLoadEmptyFile() throws IOException {
        File tempFile = createTempFile("emptyTasks", ".csv");
        tempFile.deleteOnExit();


        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile.getAbsolutePath(), historyRepository);
        manager.save();

        TaskRepository loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Assertions.assertTrue(loadedManager.getListOfAllEntities().isEmpty(), "Список задач должен быть пуст после загрузки пустого менеджера");
    }

    @Test
    void saveShouldNotThrowExceptionWhenFilePathIsValid() throws IOException {
        String filePath = createTempFile("FileBackedTaskManager", ".csv").getPath();
        FileBackedTaskManager manager = new FileBackedTaskManager(filePath, historyRepository);

        // Добавление задачи для сохранения
        taskService.createTask(new Task(firstTaskTitle, firstTaskDescription, TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofHours(1)));

        // Проверка, что сохранение не вызывает исключения
        Assertions.assertDoesNotThrow(manager::save, "Метод save должен корректно сохранять данные в файл без выброса исключений.");
    }

    @Test
    void saveShouldThrowManagerSaveExceptionWhenFilePathIsInvalid() {
        // filePath указывает на несуществующую директорию
        String invalidFilePath = "/path/to/nonexistent/directory/file.csv";
        FileBackedTaskManager manager = new FileBackedTaskManager(invalidFilePath, historyRepository);

        // Проверка, что попытка сохранения выбросит ManagerSaveException из-за невозможности найти файл
        Assertions.assertThrows(ManagerSaveException.class, manager::save,
                "Метод save должен выбрасывать ManagerSaveException при попытке сохранения в несуществующий файл.");
    }


    @Test
    void shouldConvertCommaSeparatedStringToIntList() {
        String testValue = "1,2,3,4,5";
        List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> actual = historyFromString(testValue);
        assertEquals(expected, actual, "Список целых чисел не соответствует ожидаемому.");
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
        assertEquals(expected, actual, "Список из одного значения не соответствует ожидаемому.");
    }

    @Test
    void shouldIgnoreSpaces() {
        String testValue = "1, 2, 3 , 4 ,5";
        List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> actual = historyFromString(testValue);
        assertEquals(expected, actual, "Список целых чисел должен корректно обрабатывать пробелы.");
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
            return new FileBackedTaskManager(file.getAbsolutePath(), historyRepository);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
