package handler;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.ITaskService;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskHandler implements HttpHandler {
    private final ITaskService taskService;
    private final Gson gson;

    public TaskHandler(ITaskService taskService) {
        this.taskService = taskService;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    public static class DurationAdapter implements JsonSerializer<Duration>, JsonDeserializer<Duration> {
        @Override
        public JsonElement serialize(Duration src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getSeconds());
        }

        @Override
        public Duration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Duration.ofSeconds(json.getAsLong());
        }
    }

    public static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(formatter.format(src));
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(), formatter);
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        try {
            switch (method) {
                case "GET":
                    if (pathParts.length == 2) {
                        // /tasks
                        handleGetTasks(exchange);
                    } else if (pathParts.length == 3) {
                        // /tasks/{id}
                        long id = Long.parseLong(pathParts[2]);
                        handleGetTaskById(exchange, id);
                    }
                    break;
                case "POST":
                    // /tasks
                    handleCreateOrUpdateTask(exchange);
                    break;
                case "DELETE":
                    if (pathParts.length == 3) {
                        // /tasks/{id}
                        Long id = Long.parseLong(pathParts[2]);
                        handleDeleteTask(exchange, id);
                    }
                    break;
                default:
                    sendResponse(exchange, 405, "Method Not Allowed");
                    break;
            }
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "Invalid task ID");
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Internal Server Error because" + e);
        } finally {
            exchange.close();
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskService.getTasks());
        sendResponse(exchange, 200, response);
    }

    private void handleGetTaskById(HttpExchange exchange, Long id) throws IOException {
        Task task = taskService.getTaskById(id);
        if (task == null) {
            sendResponse(exchange, 404, "Task not found");
        } else {
            String response = gson.toJson(task);
            sendResponse(exchange, 200, response);
        }
    }

    private void handleCreateOrUpdateTask(HttpExchange exchange) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        Task task = gson.fromJson(new InputStreamReader(requestBody, StandardCharsets.UTF_8), Task.class);

        String response;
        int statusCode;

        if (task.getId() == null) {
            Task newTask = taskService.createTask(task);

            if (newTask == null) {
                response = "Task creation failed due to time overlap with an existing task.";
                statusCode = 406; // Not Acceptable
            } else {
                response = gson.toJson(newTask);
                statusCode = 201; // Created
            }
        } else {
            Task updatedTask = taskService.updateTask(task);
            if (updatedTask == null) {
                response = "Task with ID does not exist";
                statusCode = 404; // Not Found
            } else {
                response = gson.toJson(updatedTask);
                statusCode = 200; // OK
            }
        }

        sendResponse(exchange, statusCode, response);
    }

    private void handleDeleteTask(HttpExchange exchange, Long id) throws IOException {
        try {
            boolean deleted = taskService.deleteTask(id);
            if (deleted) {
                sendResponse(exchange, 200, "Task deleted");
            } else {
                sendResponse(exchange, 409, "No Task found with the provided ID or type mismatch");
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, "Internal Server Error while attempting to delete task" + e);
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
}
