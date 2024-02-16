import controller.managers.InMemoryTaskManager;
import model.Epic;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EpicTest {

    static Epic epic = new Epic("Epic Title", "Epic description", TaskStatus.NEW);

    @Test
    void testGetId() {

        long expected = InMemoryTaskManager.getId() - 1;
        long idEpic = epic.getId();

        Assertions.assertEquals(expected, idEpic, String.format("Expected %d output %d", expected, idEpic));
    }

    @Test
    void testToString() {
        String printedEpic = epic.toString();
        String expected = "Epic{" +
                "id=" + epic.getId() +
                ", title='" + epic.getTitle() + '\'' +
                ", description='" + epic.getDescription() + '\'' +
                ", status=" + epic.getStatus() +
                '}';
        Assertions.assertEquals(expected, printedEpic, String.format("Expected %s output %s", expected, printedEpic));
    }
}