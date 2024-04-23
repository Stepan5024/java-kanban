
import com.sun.net.httpserver.HttpServer;
import handler.*;
import manager.Managers;
import service.*;
import service.impl.*;
import storage.history.HistoryRepository;
import storage.managers.TaskRepository;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private HttpServer server;
    private final ITaskService taskService;
    private final ISubtaskService subtaskService;
    private final EpicService epicService;
    private final IHistoryService historyService;
    private final IPrioritizedService prioritizedService;

    public HttpTaskServer(TaskRepository taskRepository, HistoryRepository historyRepository) {
        this.historyService = new HistoryService(historyRepository);
        this.taskService = new TaskService(taskRepository, historyService); // Instantiate TaskService here
        this.epicService = new EpicService(taskRepository, historyService);
        this.subtaskService = new SubtaskService(taskRepository, historyService, epicService);
        this.epicService.setSubtaskService(subtaskService);
        this.prioritizedService = new PrioritizedService(taskRepository, historyService);

    }

    public static void main(String[] args) {
        HistoryRepository historyRepository = Managers.getDefaultHistory();
        TaskRepository taskRepository = Managers.getDefault(historyRepository);
        HttpTaskServer taskServer = new HttpTaskServer(taskRepository, historyRepository);
        taskServer.start();
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);
            createContexts();
            server.start();
            System.out.println("Server started on port 8080");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createContexts() {
        server.createContext("/tasks", new TaskHandler(taskService));
        server.createContext("/subtasks", new SubtaskHandler(subtaskService));
        server.createContext("/epics", new EpicHandler(epicService));
        server.createContext("/history", new HistoryHandler(historyService));
        server.createContext("/prioritized", new PrioritizedTaskHandler(prioritizedService));

    }

    public void stop() {
        server.stop(0);
        System.out.println("Server stopped");
    }
}
