package model;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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