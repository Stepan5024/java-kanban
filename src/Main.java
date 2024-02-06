import controller.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        // пример чтобы задача стала эпиком

        TaskManager taskManager = new TaskManager();
        Task task1 = taskManager.createNewTask("Покупка", "продуктов", "NEW");
        Task task2 = taskManager.createNewTask("Уборка", "В комнате и на столе", "NEW");

        Epic epic1 = taskManager.createNewEpic("Переезд",
                "Новая квартира по адресу Москва ул. Дружбы"
        );
        Epic epic2 = taskManager.createNewEpic("Важный эпик 2",
                "Описание эпика 2"
        );

        Epic epic3 = taskManager.createNewEpic("Важный эпик 3",
                "Описание эпика 3"
        );

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
                "DONE",
                epic1.getId());

        Subtask subtask4 = taskManager.createNewSubtask(
                "mysubtask4",
                "subtask4!",
                "IN_PROGRESS",
                epic2.getId());

        printAllList(taskManager);
        System.out.printf("Подзадача до обновления %s\n", subtask4);
        subtask4 = (Subtask) taskManager.updateTask(new Subtask("newSubtask",
                "newDescription",
                TaskStatus.valueOf("DONE"), epic2.getId()), subtask4.getId());
        System.out.printf("Подзадача после обновления %s\n", subtask4);

        printAllList(taskManager);

        System.out.println("Удаляю все подзадачи");
        taskManager.removeEntityFromKanban(Subtask.class);
        printAllList(taskManager);


        ArrayList<Object> epics = taskManager.getAllEntitiesByClass(Epic.class);
        System.out.println("Всего эпиков " + epics.size());
        System.out.println("Список всех эпиков:");
        for (Object epic : epics) {
            System.out.println(epic);
        }
        printAllList(taskManager);


        System.out.printf("Удаляю эпик id = %d\n", epic1.getId());
        int result = taskManager.removeTaskById(epic1.getId());
        if (result > 0) {
            System.out.printf("Эпик id = %d удален с канбан доски\n", epic1.getId());
        }
        printAllList(taskManager);


        System.out.printf("Задача %s до обновления\n", task2);
        task2 = (Task) taskManager.updateTask(new Task("newTitle after updating",
                "newDescription",
                TaskStatus.valueOf("DONE")), task2.getId());
        System.out.printf("Задача %s после обновления\n", task2);
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
        System.out.println("\nВесь список канбан доски");
        ArrayList<Object> allTasks = taskManager.getListOfAllEntities();

        for (Object obj : allTasks) {
            System.out.println(obj);
        }
        System.out.println();
    }
}
