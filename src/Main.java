import Controller.TaskManager;
import Model.Epic;
import Model.Subtask;
import Model.Task;
import Model.TaskStatus;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {


        TaskManager taskManager = new TaskManager();
        Task task2 = taskManager.createNewTask("Уборка", "В комнате и на столе", "NEW");

        Epic epic1 = taskManager.createNewEpic("Переезд",
                "Новая квартира по адресу Москва ул. Дружбы",
                "NEW");

        // метод возвращающий все задачи по эпику
        Subtask subtask1 = taskManager.createNewSubtask(
                "Собрать коробки",
                "Вещи + одежду",
                "NEW",
                epic1.getId());


        //System.out.println(task1);
        System.out.println(task2);

        System.out.println(subtask1);
        System.out.println(epic1);

        System.out.println("Весь список канбан доски");
        ArrayList<Object> allTasks = taskManager.getListOfAllTasks();
        for (Object obj : allTasks) {
            System.out.println(obj);
        }
        System.out.println("Список всех задач");
        ArrayList<Task> tasks = taskManager.getAllTasks();
        for (Task task : tasks) {
            System.out.println(task);
        }
        System.out.println("Список всех эпиков");
        ArrayList<Epic> epics = taskManager.getAllEpics();
        for (Epic epic : epics) {
            System.out.println(epic);
        }
        System.out.println("Список всех подзадач");
        ArrayList<Subtask> subtasks = taskManager.getAllSubtask();
        for (Subtask subtask : subtasks) {
            System.out.println(subtask);
        }
    }
}
