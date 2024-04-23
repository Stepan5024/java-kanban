package handler;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Subtask;
import model.Task;
import service.impl.SubtaskService;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class SubtaskHandler implements HttpHandler {

    private final SubtaskService subtaskService;
    private final Gson gson;

    public SubtaskHandler(SubtaskService subtaskService) {
        this.subtaskService = subtaskService;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new TaskHandler.DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new TaskHandler.LocalDateTimeAdapter())
                .create();
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
                        // /subtasks
                        handleGetSubtasks(exchange);
                    } else if (pathParts.length == 3) {
                        // /tasks/{id}
                        long id = Long.parseLong(pathParts[2]);
                        handleGetSubtaskById(exchange, id);
                    }
                    break;
                case "POST":
                    // /tasks
                    handleCreateOrUpdateSubtask(exchange);
                    break;
                case "DELETE":
                    if (pathParts.length == 3) {
                        // /tasks/{id}
                        Long id = Long.parseLong(pathParts[2]);
                        handleDeleteSubtask(exchange, id);
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

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(subtaskService.getSubtasks());
        sendResponse(exchange, 200, response);
    }

    private void handleGetSubtaskById(HttpExchange exchange, Long id) throws IOException {
        Task task = subtaskService.getSubtaskById(id);
        if (task == null) {
            sendResponse(exchange, 404, "Task not found");
        } else {
            String response = gson.toJson(task);
            sendResponse(exchange, 200, response);
        }
    }

    private void handleCreateOrUpdateSubtask(HttpExchange exchange) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        Subtask task = gson.fromJson(new InputStreamReader(requestBody, StandardCharsets.UTF_8), Subtask.class);

        String response;
        int statusCode;

        if (task.getId() == null) {
            Task newTask = subtaskService.createSubtask(task);

            if (newTask == null) {
                response = "Task creation failed due to time overlap with an existing task.";
                statusCode = 406; // Not Acceptable
            } else {
                response = gson.toJson(newTask);
                statusCode = 201; // Created
            }
        } else {
            Task updatedTask = subtaskService.updateSubtask(task);
            if (updatedTask == null) {
                response = "Task with ID does not exist";
                statusCode = 404; // Not Found
            } else {
                System.out.println(updatedTask);
                response = gson.toJson(updatedTask);
                statusCode = 200; // OK
            }
        }

        sendResponse(exchange, statusCode, response);
    }

    private void handleDeleteSubtask(HttpExchange exchange, Long id) throws IOException {
        try {
            boolean deleted = subtaskService.deleteSubtask(id);
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
