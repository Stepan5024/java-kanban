import controller.managers.InMemoryTaskManager;
import model.Epic;
import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    static Epic epic;
    long expectedIdEpic;
    static Subtask subtask;

    @BeforeEach
    void init() {
        epic = new Epic("Epic Title", "Epic description", TaskStatus.NEW);
        expectedIdEpic = InMemoryTaskManager.getTaskId() - 1;
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