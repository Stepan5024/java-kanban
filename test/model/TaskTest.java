package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    static String firstSubTaskTitleForFirstEpic;
    static String firstSubTaskDescriptionForFirstEpic;
    static String secondSubTaskTitle;
    static String secondSubTaskDescription;
    Task task;
    static String firstTaskTitle;
    static String firstTaskDescription;
    static String secondTaskTitle;
    static String secondTaskDescription;


    @BeforeAll
    static void initTextLabels() {
        firstTaskTitle = "Переезд";
        firstTaskDescription = "Новая квартира по адресу Москва ул. Дружбы";
        secondTaskTitle = "Пример второй задачи";
        secondTaskDescription = "Описание второй задачи";
        firstSubTaskTitleForFirstEpic = "Подзадача в рамках эпика";
        firstSubTaskDescriptionForFirstEpic = "Пум-Пум";
        secondSubTaskTitle = "Чтение литературы";
        secondSubTaskDescription = "Для выпуска из университета";
    }

    @BeforeEach
    void setUp() {
        task = new Task(firstTaskTitle, firstTaskDescription, TaskStatus.NEW);
        task.setId(1);
    }

    @Test
    void testToString() {
        String printedEpic = task.toString();
        String expected = "Task{" +
                "id=" + task.getId() +
                ", title=" + task.getTitle() +
                ", description='" + task.getDescription() + '\'' +
                ", status=" + task.getStatus() +
                ", startTime=" + task.getStartTime() +
                ", duration=" + task.getDuration() +
                '}';
        Assertions.assertEquals(expected, printedEpic, String.format("Expected %s output %s", expected, printedEpic));

    }

    @Test
    void checkTwoDuplicatesTaskById() {
        // 1. проверьте, что экземпляры класса Task равны друг другу, если равен их id;
        Task task2 = new Task(firstTaskTitle, firstTaskDescription, TaskStatus.NEW, task.getId());
        task2.setId(task.getId());
        assertEquals(task, task2, String.format("task1 != task2 by id. " +
                "Expected that their id should be %d", task.getId()));
    }

    @Test
    void checkTwoDuplicatesInheritorTaskById() {
        // 2. проверьте, что наследники класса Task равны друг другу, если равен их id;
        Subtask subtask1 = new Subtask(firstSubTaskTitleForFirstEpic, firstSubTaskDescriptionForFirstEpic,
                TaskStatus.NEW, 1, 1, null, Duration.ZERO);
        subtask1.setId(task.getId());
        Subtask subtask2 = new Subtask(firstSubTaskTitleForFirstEpic, firstSubTaskDescriptionForFirstEpic,
                TaskStatus.NEW, 1, 1, null, Duration.ZERO);
        subtask2.setId(task.getId());
        assertEquals(subtask1, subtask2, String.format("subtask1 != subtask2 by id. " +
                "Expected that their id should be %d", task.getId()));
    }
}