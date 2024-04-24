import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import handler.TaskHandler;
import manager.Managers;
import model.Epic;
import model.Subtask;
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


public class HttpTaskServerTest {


    private final HttpClient client = HttpClient.newHttpClient();
    private final String baseUrl = "http://localhost:8080";

    private HttpTaskServer taskServer;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new TaskHandler.DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new TaskHandler.LocalDateTimeAdapter())
            .create();
    ;

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

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/tasks"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "Expected HTTP OK response for /tasks");
    }

    @Test
    public void testCreateAndGetEpicByIdSuccess() throws Exception {
        Epic expectedEpic = new Epic("Epic Title", "Epic Description", null);

        String jsonNewEpic = gson.toJson(expectedEpic);
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonNewEpic))
                .build();

        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, postResponse.statusCode(), "Expected HTTP Created response for POST /epics");

        Epic createdEpic = gson.fromJson(postResponse.body(), Epic.class);

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/epics/" + createdEpic.getId()))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, getResponse.statusCode(), "Expected HTTP OK response for GET /epics/{id}");
        Epic actualEpic = gson.fromJson(getResponse.body(), Epic.class);

        Assertions.assertNotNull(actualEpic, "The epic retrieved should not be null");
        Assertions.assertEquals(createdEpic.getId(), actualEpic.getId(), "Epic ID retrieved does not match the created epic ID.");
    }

    @Test
    public void testGetTasksNotFound() throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/unknown"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode(), "Expected HTTP Not Found response for invalid endpoint");
    }

    @Test
    public void testCreateAndGetTaskByIdSuccess() throws Exception {

        Task expectedTask = new Task("Task Title", "Task Description", TaskStatus.NEW, /* startTime */ null, /* duration */ null);

        String jsonNewTask = gson.toJson(expectedTask);
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonNewTask))
                .build();

        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, postResponse.statusCode(), "Expected HTTP Created response for POST /tasks");

        Task createdTask = gson.fromJson(postResponse.body(), Task.class);

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/tasks/" + createdTask.getId()))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, getResponse.statusCode(), "Expected HTTP OK response for GET /tasks/{id}");
        Task actualTask = gson.fromJson(getResponse.body(), Task.class);

        Assertions.assertNotNull(actualTask, "The task retrieved should not be null");
        Assertions.assertEquals(createdTask.getId(), actualTask.getId(), "Task ID retrieved does not match the created task ID.");
    }


    @Test
    public void testGetTaskByIdNotFound() throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/tasks/999"))  // Assuming no task has ID 999
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode(), "Expected HTTP Not Found response for a non-existent task ID");
    }

    @Test
    public void testCreateSubtaskForEpic() throws Exception {

        Epic newEpic = new Epic("Epic Title", "Epic Description", TaskStatus.NEW);
        String jsonNewEpic = gson.toJson(newEpic);
        HttpRequest postEpicRequest = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonNewEpic))
                .build();

        HttpResponse<String> postEpicResponse = client.send(postEpicRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, postEpicResponse.statusCode(), "Expected HTTP Created response for POST /epics");

        Epic createdEpic = gson.fromJson(postEpicResponse.body(), Epic.class);

        Subtask newSubtask = new Subtask("Subtask Title", "Subtask Description", TaskStatus.NEW, createdEpic.getId(), /* startTime */ null, /* duration */ null);
        String jsonNewSubtask = gson.toJson(newSubtask);
        HttpRequest postSubtaskRequest = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonNewSubtask))
                .build();

        HttpResponse<String> postSubtaskResponse = client.send(postSubtaskRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, postSubtaskResponse.statusCode(), "Expected HTTP Created response for POST /subtasks");

        Subtask createdSubtask = gson.fromJson(postSubtaskResponse.body(), Subtask.class);
        Assertions.assertNotNull(createdSubtask, "The subtask should not be null");
        Assertions.assertEquals(createdEpic.getId(), createdSubtask.getEpicId(), "Subtask epic ID should match the created epic ID");
    }

    @Test
    public void testDeleteTaskSuccess() throws Exception {

        Task newTask = new Task("Sample Task", "Description", TaskStatus.NEW, null, Duration.ZERO);
        String json = gson.toJson(newTask);

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        Task createdTask = gson.fromJson(postResponse.body(), Task.class);
        Long taskIdToDelete = createdTask.getId();  // Assuming Task has a getId() method

        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/tasks/" + taskIdToDelete))
                .DELETE()
                .build();

        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, deleteResponse.statusCode(), "Expected HTTP OK response for DELETE /tasks/{id}");

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/tasks/" + taskIdToDelete))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, getResponse.statusCode(), "Expected HTTP Not Found response for GET /tasks/{id} after deletion");
    }

    @Test
    public void testDeleteTaskNotFound() throws Exception {

        int taskIdToDelete = 9999;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/tasks/" + taskIdToDelete))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(409, response.statusCode(), "Expected HTTP Not Found response for DELETE /tasks/{id} with non-existent ID");
    }


}
