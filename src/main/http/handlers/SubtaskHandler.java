package main.http.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.exceptions.IncorrectTaskException;
import main.exceptions.NotFoundException;
import main.exceptions.TaskOverlapException;
import main.http.BaseHttpHandler;
import main.http.Endpoint;
import main.models.Subtask;
import main.taskManagerAndHistoryManagerInterfaces.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_SUBTASKS:
                handleGetSubtasks(exchange);
                break;
            case GET_SUBTASK_ID:
                handleGetSubtaskId(exchange);
                break;
            case POST_SUBTASK:
                handlePostSubtask(exchange);
                break;
            case DELETE_SUBTASK_ID:
                handleDeleteSubtaskId(exchange);
                break;
            case UNKNOWN:
                sendNoSuchEndpoint(exchange);
                break;
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        String jsonSubtasks = gson.toJson(taskManager.getAllSubtasks());
        send200(exchange, jsonSubtasks);
    }

    private void handleGetSubtaskId(HttpExchange exchange) throws IOException {
        Optional<Integer> subtaskIdOpt = getTaskId(exchange);

        if (subtaskIdOpt.isEmpty()) {
            send400(exchange, "Передан некорректный идентификатор подзадачи");

        } else {
            try {
                Optional<Subtask> subtaskOpt = taskManager.getSubtaskById(subtaskIdOpt.get());
                String jsonSubtask = gson.toJson(subtaskOpt.get());
                send200(exchange, jsonSubtask);
            } catch (NotFoundException ex) {
                send404NotFound(exchange, ex.getMessage());
            }
        }
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String jsonString = new String(inputStream.readAllBytes(), defaultCharset);

        //Сервер не обнаружил запрашиваемый контент
        if (jsonString.isEmpty() || jsonString.isBlank())
            send400(exchange, "Передана пустая подзадача");

        try {
            Subtask subtaskFromJson = gson.fromJson(jsonString, Subtask.class);

            if (subtaskFromJson.getId() == null) {
                taskManager.createSubtask(subtaskFromJson);
                send201(exchange, "Подзадача добавлена");
            } else {
                taskManager.updateSubtask(subtaskFromJson);
                send201(exchange, "Подзадача обновлена");
            }
        } catch (IncorrectTaskException ex) {
            send400(exchange, ex.getMessage());
        } catch (TaskOverlapException ex) {
            send406HasInteractions(exchange, ex.getMessage());
        } catch (NotFoundException ex) {
            send404NotFound(exchange, ex.getMessage());
        } catch (JsonSyntaxException ex) {
            //Сервер не обнаружил запрашиваемый контент
            send400(exchange, "Передан некорректный формат запроса");
        }
    }

    private void handleDeleteSubtaskId(HttpExchange exchange) throws IOException {
        Optional<Integer> subtaskIdOpt = getTaskId(exchange);

        if (subtaskIdOpt.isEmpty()) {
            send400(exchange, "Передан некорректный идентификатор подзадачи");
        } else {
            try {
                taskManager.deleteSubtaskById(subtaskIdOpt.get());
                send200(exchange, "Подзадача удалена");
            } catch (NotFoundException ex) {
                send404NotFound(exchange, ex.getMessage());
            }
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (requestMethod.equals("GET") && pathParts.length == 2 && pathParts[1].equals("subtasks")) {
            return Endpoint.GET_SUBTASKS;
        } else if (requestMethod.equals("GET") && pathParts.length == 3 && pathParts[1].equals("subtasks")) {
            return Endpoint.GET_SUBTASK_ID;
        } else if (requestMethod.equals("POST") && pathParts.length == 2 && pathParts[1].equals("subtasks")) {
            return Endpoint.POST_SUBTASK;
        } else if (requestMethod.equals("DELETE") && pathParts.length == 3 && pathParts[1].equals("subtasks")) {
            return Endpoint.DELETE_SUBTASK_ID;
        }

        return Endpoint.UNKNOWN;
    }
}

