package controller.managers;

import controller.history.HistoryManager;
import controller.history.InMemoryHistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileBackedTaskManagerTest {

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

        manager.createNewTask("Task 1", "Description 1", "NEW");
        Epic epic = manager.createNewEpic("Epic 1", "Description Epic");
        manager.createNewSubtask("Subtask 1", "Description Subtask 1", "NEW", epic.getId());


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
        Task task1 = manager.createNewTask("Task 1", "Description 1", "NEW");
        Epic epic1 = manager.createNewEpic("Epic 1", "Epic Description 1");
        Subtask subtask1 = manager.createNewSubtask("Subtask 1", "Subtask Description 1", "NEW", epic1.getId());

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile, historyManager);

        // Проверки
        assertEquals(3, loadedManager.getListOfAllEntities().size(), "Количество задач после загрузки должно соответствовать количеству сохраненных задач");
        assertNotNull(loadedManager.getTaskById(task1.getId()), "Задача должна быть загружена");
        assertNotNull(loadedManager.getEpicById(epic1.getId()), "Эпик должен быть загружен");
        assertNotNull(loadedManager.getSubtaskById(subtask1.getId()), "Подзадача должна быть загружена");
    }
}
