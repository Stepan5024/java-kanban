import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import handler.TaskHandler;
import manager.Managers;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import storage.history.HistoryRepository;
import storage.managers.TaskRepository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class HttpTaskServerTest {

    private HttpTaskServer taskServer;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new TaskHandler.DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new TaskHandler.LocalDateTimeAdapter())
            .create();;

    @BeforeEach
    public void setUp() {
        HistoryRepository historyRepository = Managers.getDefaultHistory();
        TaskRepository taskRepository = Managers.getDefault(historyRepository);
        taskServer = new HttpTaskServer(taskRepository, historyRepository);
        taskServer.start();
    }

    @AfterEach
    public void tearDown() {
        taskServer.stop();
    }

    @Test
    public void testGetTasksSuccess() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/tasks"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "Expected HTTP OK response for /tasks");
    }

    @Test
    public void testGetTasksNotFound() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/unknown"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode(), "Expected HTTP Not Found response for invalid endpoint");
    }

    @Test
    public void testGetTaskByIdSuccess() throws Exception {
        // Set up the test data and the server to return a specific task for a given ID.

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/tasks/1"))  // Assuming there's a task with ID 1
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode(), "Expected HTTP OK response for /tasks/{id}");
        Task expectedTask = new Task("Task Title", "Task Description", TaskStatus.NEW, /* startTime */ null, /* duration */ null);
        Task actualTask = gson.fromJson(response.body(), Task.class);
        Assertions.assertEquals(expectedTask, actualTask, "Task retrieved does not match the expected task.");
    }

    @Test
    public void testGetTaskByIdNotFound() throws Exception {
        // Ensure the server is set up to return a 404 for a non-existent task ID.

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/tasks/999"))  // Assuming no task has ID 999
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode(), "Expected HTTP Not Found response for a non-existent task ID");
    }
}
