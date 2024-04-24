package controller.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import manager.Managers;
import model.Epic;
import model.Subtask;
import model.TaskStatus;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class SubtaskServiceTest {


    private TaskRepository taskRepository;

    private SubtaskService subtaskService;
    HistoryRepository historyRepository;
    HistoryService historyService = new HistoryService(historyRepository);
    TaskService taskService = new TaskService(taskRepository, historyService);
    EpicService epicService = new EpicService(taskRepository, historyService);

    @BeforeEach
    void setUp() {
        historyRepository = Managers.getDefaultHistory();
        taskRepository = Managers.getDefault(historyRepository);

        historyService = new HistoryService(historyRepository);
        taskService = new TaskService(taskRepository, historyService);
        epicService = new EpicService(taskRepository, historyService);
        subtaskService = new SubtaskService(taskRepository, historyService, epicService);
        this.epicService.setSubtaskService(subtaskService);

    }

    @Test
    void getSubtasksByEpicId_ShouldReturnSubtasksForEpic() {

        Epic epic = epicService.createEpic(new Epic("sd", "re", null));
        Long epicId = epic.getId();
        Subtask sub1 = subtaskService.createSubtask(new Subtask("Subtask 1", "Description", TaskStatus.NEW, epicId, null, Duration.ZERO));
        Subtask sub2 = subtaskService.createSubtask(new Subtask("Subtask 2", "Description", TaskStatus.NEW, epicId, null, Duration.ZERO));

        List<Subtask> subtasks = subtaskService.getSubtasksByEpicId(epicId);

        assertEquals(2, subtasks.size(), "Should return exactly two subtasks for the epic");

    }

    @Test
    void getSubtasksByEpicId_ShouldReturnEmptyListIfNoSubtasks() {
        Long epicId = 2L;

        List<Subtask> subtasks = subtaskService.getSubtasksByEpicId(epicId);

        assertEquals(0, subtasks.size(), "Should return an empty list when there are no subtasks for the epic");
    }

    @Test
    void getSubtasksByEpicId_ShouldHandleNonExistentEpicId() {
        Long nonExistentEpicId = 99L;
        Subtask sub1 = subtaskService.createSubtask(new Subtask("Subtask 1", "Description", TaskStatus.NEW, 1L, null, Duration.ZERO));

        List<Subtask> subtasks = subtaskService.getSubtasksByEpicId(nonExistentEpicId);

        assertEquals(0, subtasks.size(), "Should return an empty list when the epic ID does not exist");
    }
}
