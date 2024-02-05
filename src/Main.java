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

        Subtask subtask4 = taskManager.createNewSubtask(
                "mysubtask4",
                "subtask4!",
                "NEW",
                epic2.getId());

        printAllList(taskManager);
        System.out.println("subtask4 до обновления " + subtask4 + " по ИД " + subtask4.getId());
        subtask4 = (Subtask) taskManager.updateTask(new Subtask("newSubtask",
                "newDescription",
                TaskStatus.valueOf("NEW"), epic2.getId()), subtask4.getId());
        System.out.println("subtask4 после обновления " + subtask4);

        printAllList(taskManager);



        ArrayList<Object> epics = taskManager.getAllEntitiesByClass(Epic.class);
        System.out.println("Всего эпиков " + epics.size());
        System.out.println("Список всех эпиков:");
        for (Object epic : epics) {
            System.out.println(epic);
        }
        printAllList(taskManager);


        System.out.println("Удаляю эпик" + epic1.getId());
        taskManager.removeTaskById(epic1.getId());
        printAllList(taskManager);
        System.out.println();

        System.out.println("task2 до обновления " + task2 + " по ИД " + task2.getId());
        task2 = (Task) taskManager.updateTask(new Task("newTitle",
                "newDescription",
                TaskStatus.valueOf("NEW")), task2.getId());
        System.out.println("task2 после обновления " + task2);
        printAllList(taskManager);

        ArrayList<Object> tasks = taskManager.getAllEntitiesByClass(Task.class);
        System.out.println("Всего задач " + tasks.size());
        System.out.println("Список всех задач:");
        for (Object task : tasks) {
            System.out.println(task);
        }
        System.out.println();

        System.out.println("Удаляю все задачи");
        System.out.println("Удалено " + taskManager.removeEntityFromKanban(Task.class));
        printAllList(taskManager);

    }

    public static void printAllList(TaskManager taskManager) {
        System.out.println();
        System.out.println("Весь список канбан доски");
        ArrayList<Object> allTasks = taskManager.getListOfAllEntities();

        for (Object obj : allTasks) {
            System.out.println(obj);
        }
        System.out.println();
    }
}
