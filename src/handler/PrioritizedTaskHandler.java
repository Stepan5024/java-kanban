package handler;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.IPrioritizedService;
import service.impl.PrioritizedService;

import java.io.IOException;
import java.io.OutputStream;

public class PrioritizedTaskHandler implements HttpHandler {

    private IPrioritizedService prioritizedService;

    public PrioritizedTaskHandler(IPrioritizedService prioritizedService) {
        this.prioritizedService = prioritizedService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "";
        int statusCode = 200;

        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    // Обрабатываем GET запрос
                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    response = handleGetPrioritizedTasks();
                    break;
                default:
                    // Если получен не поддерживаемый метод, возвращаем код 405 Method Not Allowed
                    statusCode = 405;
                    response = "Method Not Allowed";
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // В случае возникновения ошибки возвращаем код 500 Internal Server Error
            statusCode = 500;
            response = "Internal Server Error";
        }

        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private String handleGetPrioritizedTasks() {

        return prioritizedService.getPrioritizedTasks();

    }
}
