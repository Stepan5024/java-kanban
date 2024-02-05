import Controller.TaskManager;
import Model.Epic;
import Model.Subtask;
import Model.Task;
import Model.TaskStatus;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {


        TaskManager taskManager = new TaskManager();
        Task task1 = taskManager.createNewTask("Покупка", "продуктов", "NEW");
        Task task2 = taskManager.createNewTask("Уборка", "В комнате и на столе", "NEW");

        Epic epic1 = taskManager.createNewEpic("Переезд",
                "Новая квартира по адресу Москва ул. Дружбы",
                "NEW");
        Epic epic2 = taskManager.createNewEpic("Важный эпик 2",
                "Описание эпика 2",
                "IN_PROGRESS");

        Epic epic3 = taskManager.createNewEpic("Важный эпик 3",
                "Описание эпика 3",
                "NEW");

        // метод возвращающий все задачи по эпику
        Subtask subtask1 = taskManager.createNewSubtask(
                "Собрать коробки",
                "Вещи + одежду",
                "NEW",
                epic1.getId());

        Subtask subtask2 = taskManager.createNewSubtask(
                "Упаковать кошку",
                "Кошка белая",
                "NEW",
                epic1.getId());

        Subtask subtask3 = taskManager.createNewSubtask(
                "Сказать слова прощания",
                "Поехали!",
                "NEW",
                epic1.getId());


       //printAllList(taskManager);
      /*  System.out.println("Список всех задач");
        ArrayList<Task> tasks = taskManager.getAllTasks();
        for (Task task : tasks) {
            System.out.println(task);
        }
        System.out.println("Список всех эпиков");
        ArrayList<Epic> epics = taskManager.getAllEpics();
        for (Epic epic : epics) {
            System.out.println(epic);
        }*/
       printAllList(taskManager);

        System.out.println("подзадачи по эпику 1 = " + taskManager.getListOfSubtaskByEpicId(epic1.getId()));
        System.out.println("Удаляю эпик");
        System.out.println("epicId main = " + epic1.getId());
        taskManager.removeTaskById(epic1.getId());

        printAllList(taskManager);
        System.out.println("Удаляю все задачи");
        taskManager.removeTasks();

        printAllList(taskManager);

    }

    public static void printAllList(TaskManager taskManager){
        System.out.println("Весь список канбан доски");
        ArrayList<Object> allTasks = taskManager.getListOfAllTasks();

        for (Object obj : allTasks) {
            System.out.println(obj);
        }
    }
}
