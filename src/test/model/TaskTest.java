package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    Task task;

    @BeforeEach
    void setUp() {
        task = new Task("Task Title", "Task description", TaskStatus.NEW);
    }

    @Test
    void testToString() {
        String printedEpic = task.toString();
        String expected = "Task{" +
                "id=" + task.getId() +
                ", title=" + task.getTitle() +
                ", description='" + task.getDescription() + '\'' +
                ", status=" + task.getStatus() +
                '}';
        Assertions.assertEquals(expected, printedEpic, String.format("Expected %s output %s", expected, printedEpic));

    }

    @Test
    void checkTwoDuplicatesTaskById() {
        // 1. проверьте, что экземпляры класса Task равны друг другу, если равен их id;
        Task task2 = new Task("Task Title2", "Task description2", TaskStatus.NEW, task.getId());
        assertEquals(task, task2, String.format("task1 != task2 by id. " +
                "Expected that their id should be %d", task.getId()));
    }

    @Test
    void checkTwoDuplicatesInheritorTaskById() {
        // 2. проверьте, что наследники класса Task равны друг другу, если равен их id;
        Subtask subtask1 = new Subtask("Subtask Title", "Subtask description",
                TaskStatus.NEW, 1, 1);
        Subtask subtask2 = new Subtask("Subtask Title", "Subtask description",
                TaskStatus.NEW, 1, 1);
        assertEquals(subtask1, subtask2, String.format("subtask1 != subtask2 by id. " +
                "Expected that their id should be %d", task.getId()));
    }
}