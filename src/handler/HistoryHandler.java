package handler;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.IHistoryService;
import service.impl.HistoryService;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class HistoryHandler implements HttpHandler {
    private final IHistoryService historyService;
    private final Gson gson;

    public HistoryHandler(IHistoryService historyService) {
        this.historyService = historyService;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new TaskHandler.DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new TaskHandler.LocalDateTimeAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod()) && "/history".equals(exchange.getRequestURI().toString())) {
                handleGetHistory(exchange);
            } else {
                // Respond with a 405 Method Not Allowed if the method is not GET or the URI doesn't match
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, "Internal Server Error");
        } finally {
            exchange.close();
        }
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        String response = gson.toJson(historyService.getHistory());
        sendResponse(exchange, 200, response); // HTTP OK with the history in JSON format
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
}
