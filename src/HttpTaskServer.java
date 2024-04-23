
import com.sun.net.httpserver.HttpServer;
import handler.*;
import manager.Managers;
import service.IHistoryService;
import service.impl.HistoryService;
import service.impl.SubtaskService;
import service.impl.TaskService;
import storage.history.HistoryRepository;
import storage.history.InMemoryHistoryManager;
import storage.managers.TaskRepository;
import storage.managers.impl.InMemoryTaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private HttpServer server;
    private final TaskService taskService;
    private final SubtaskService subtaskService;
    private final IHistoryService historyService;

    public HttpTaskServer(TaskRepository taskRepository, HistoryRepository historyRepository) {
        this.historyService = new HistoryService(historyRepository);
        this.taskService = new TaskService(taskRepository, historyService); // Instantiate TaskService here
        this.subtaskService = new SubtaskService(taskRepository, historyService);


    }

    public static void main(String[] args) {
        HistoryRepository historyRepository = Managers.getDefaultHistory();
        TaskRepository taskRepository = Managers.getDefault();


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
        //server.createContext("/epics", new EpicHandler());
        server.createContext("/history", new HistoryHandler(historyService));
        /* server.createContext("/prioritized", new PrioritizedTaskHandler());


       */
    }

    public void stop() {
        server.stop(0);
        System.out.println("Server stopped");
    }
}
