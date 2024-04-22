package model;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import storage.managers.TaskRepository;

class EpicTest {

    static String firstEpicTitle;
    static String firstEpicDescription;

    static Epic epic;

    @BeforeAll
    static void initTextLabels() {
        firstEpicTitle = "Написание диплома";
        firstEpicDescription = "Для выпуска из университета";
        epic = new Epic(firstEpicTitle, firstEpicDescription, TaskStatus.NEW);
    }

    /*
    @Test
    void testGetId() {

        long expected = TaskRepository.getId() - 1;
        long idEpic = epic.getId();

        Assertions.assertEquals(expected, idEpic, String.format("Expected %d output %d", expected, idEpic));
    }


     */
    @Test
    void testToString() {
        String printedEpic = epic.toString();
        String expected = "Epic{" +
                "id=" + epic.getId() +
                ", title='" + epic.getTitle() + '\'' +
                ", description='" + epic.getDescription() + '\'' +
                ", status=" + epic.getStatus() +
                ", endTime=" + epic.getEndTime() +
                '}';
        Assertions.assertEquals(expected, printedEpic, String.format("Expected %s output %s", expected, printedEpic));
    }
}