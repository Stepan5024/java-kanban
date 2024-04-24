package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;
import model.Subtask;
import service.IEpicService;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class EpicHandler implements HttpHandler {
    private final IEpicService epicService;
    private final Gson gson;

    public EpicHandler(IEpicService epicService) {
        this.epicService = epicService;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new TaskHandler.DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new TaskHandler.LocalDateTimeAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    if (pathParts.length == 2) {
                        handleGetEpics(exchange);
                    } else if (pathParts.length == 3) {
                        long id = Long.parseLong(pathParts[2]);
                        handleGetEpicById(exchange, id);
                    } else if (pathParts.length == 4 && "subtasks".equals(pathParts[3])) {
                        long epicId = Long.parseLong(pathParts[2]);
                        handleGetSubtaskByEpic(exchange, epicId);
                    }
                    break;
                case "POST":
                    handleCreateOrUpdateEpic(exchange);
                    break;
                case "DELETE":
                    if (pathParts.length == 3) {
                        long id = Long.parseLong(pathParts[2]);
                        handleDeleteEpic(exchange, id);
                    }
                    break;
                default:
                    sendResponse(exchange, 405, "Method Not Allowed");
                    break;
            }
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "Invalid ID format");
        } catch (Exception e) {
            sendResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void handleGetSubtaskByEpic(HttpExchange exchange, long epicId) throws IOException {
        List<Subtask> subtasks = epicService.getSubtaskService().getSubtasksByEpicId(epicId);
        if (subtasks != null && !subtasks.isEmpty()) {
            sendResponse(exchange, 200, gson.toJson(subtasks));
        } else {
            sendResponse(exchange, 404, "No subtasks found for this epic or epic does not exist");
        }
    }

    // Handle GET for all epics
    private void handleGetEpics(HttpExchange exchange) throws IOException {
        String response = gson.toJson(epicService.getEpics());
        sendResponse(exchange, 200, response);
    }

    // Handle GET for a single epic by ID
    private void handleGetEpicById(HttpExchange exchange, long id) throws IOException {
        Epic epic = epicService.getEpicById(id);
        if (epic != null) {
            sendResponse(exchange, 200, gson.toJson(epic));
        } else {
            sendResponse(exchange, 404, "Epic not found");
        }
    }

    // Handle POST for creating or updating an epic
    private void handleCreateOrUpdateEpic(HttpExchange exchange) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        Epic epic = gson.fromJson(new InputStreamReader(requestBody, StandardCharsets.UTF_8), Epic.class);

        if (epic.getId() == null || epic.getId() == 0) {
            // ID is not provided or is 0, so create a new epic
            Epic createdEpic = epicService.createEpic(epic);
            if (createdEpic != null) {
                sendResponse(exchange, 201, gson.toJson(createdEpic));
            } else {
                sendResponse(exchange, 406, "Unable to create epic due to overlap");
            }
        } else {
            // ID is provided, try to update the epic
            Epic updatedEpic = epicService.updateEpic(epic);
            if (updatedEpic != null) {
                sendResponse(exchange, 200, gson.toJson(updatedEpic));
            } else {
                sendResponse(exchange, 404, "Epic not found or update failed");
            }
        }
    }

    // Handle DELETE for an epic by ID
    private void handleDeleteEpic(HttpExchange exchange, long id) throws IOException {
        boolean deleted = epicService.deleteEpic(id);
        if (deleted) {
            sendResponse(exchange, 200, "Epic deleted");
        } else {
            sendResponse(exchange, 404, "Epic not found or deletion failed");
        }
    }

    // Utility method for sending a response
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes(StandardCharsets.UTF_8));
        os.close();
    }
}

