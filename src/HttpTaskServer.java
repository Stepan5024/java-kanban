
import com.sun.net.httpserver.HttpServer;
import handler.*;
import service.impl.TaskService;
import storage.history.HistoryRepository;
import storage.history.InMemoryHistoryManager;
import storage.managers.TaskRepository;
import storage.managers.impl.InMemoryTaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private HttpServer server;
    private TaskService taskService;

    public HttpTaskServer(TaskRepository taskRepository) {
        this.taskService = new TaskService(taskRepository); // Instantiate TaskService here
        // You can pass additional dependencies to TaskService if needed
    }

    public static void main(String[] args) {
        HistoryRepository historyRepository = new InMemoryHistoryManager();
        HttpTaskServer taskServer = new HttpTaskServer(new InMemoryTaskManager(historyRepository));
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
      /*  server.createContext("/subtasks", new SubtaskHandler());
        server.createContext("/epics", new EpicHandler());
        server.createContext("/history", new HistoryHandler());
        server.createContext("/prioritized", new PrioritizedTaskHandler());


       */
    }

    public void stop() {
        server.stop(0);
        System.out.println("Server stopped");
    }
}
