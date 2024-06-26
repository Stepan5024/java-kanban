package model;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.impl.LongGenerateIdServiceImpl;

import java.time.Duration;

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
        LongGenerateIdServiceImpl longGenerateIdService = new LongGenerateIdServiceImpl();
        epic = new Epic(firstEpicTitle, firstEpicDescription, TaskStatus.NEW);
        epic.setId(longGenerateIdService.generateId());
        expectedIdEpic = longGenerateIdService.getId();

        subtask = new Subtask("Subtask Title", "Subtask description", TaskStatus.NEW, epic.getId(),
                null, Duration.ZERO);
        subtask.setId(longGenerateIdService.generateId());
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
                ", startTime=" + subtask.getStartTime() +
                ", duration=" + subtask.getDuration() +
                '}';
        Assertions.assertEquals(expected, printedSubtask, String.format("Expected %s output %s",
                expected, printedSubtask));

    }
}