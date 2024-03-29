package model;

import controller.managers.InMemoryTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubtaskTest {
    static String firstEpicTitle;
    static String firstEpicDescription;
    static Epic epic;
    long expectedIdEpic;
    static Subtask subtask;

    @BeforeAll
    static void initAllEntity() {
        firstEpicTitle = "Написание диплома";
        firstEpicDescription = "Для выпуска из университета";
    }

    @BeforeEach
    void init() {
        epic = new Epic(firstEpicTitle, firstEpicDescription, TaskStatus.NEW);
        expectedIdEpic = InMemoryTaskManager.getId() - 1;
        subtask = new Subtask("Subtask Title", "Subtask description", TaskStatus.NEW, epic.getId());
    }

    @Test
    void getEpicId() {
        long idEpic = epic.getId();
        Assertions.assertEquals(expectedIdEpic, idEpic, String.format("Expected %d output %d", expectedIdEpic, idEpic));
    }

    @Test
    void testToString() {
        String printedSubtask = subtask.toString();
        String expected = "Subtask{" +
                "id=" + subtask.getId() +
                ", epicId=" + subtask.getEpicId() +
                ", title='" + subtask.getTitle() + '\'' +
                ", description='" + subtask.getDescription() + '\'' +
                ", status=" + subtask.getStatus() +
                '}';
        Assertions.assertEquals(expected, printedSubtask, String.format("Expected %s output %s", expected, printedSubtask));

    }
}