package main.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.http.BaseHttpHandler;
import main.http.Endpoint;
import main.managers.interfaces.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_HISTORY:
                handleGetHistory(exchange);
                break;
            case UNKNOWN:
                sendNoSuchEndpoint(exchange);
                break;
        }
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        String jsonTasks = gson.toJson(taskManager.getHistory());
        send200(exchange, jsonTasks);
    }

    private Endpoint getEndpoint(String path, String requestMethod) {
        String[] pathParts = path.split("/");

        if (requestMethod.equals("GET") && pathParts.length == 2 && pathParts[1].equals("history")) {
            return Endpoint.GET_HISTORY;
        }
        return Endpoint.UNKNOWN;
    }
}
