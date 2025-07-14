package main.http.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.exceptions.IncorrectTaskException;
import main.exceptions.NotFoundException;
import main.exceptions.TaskOverlapException;
import main.http.BaseHttpHandler;
import main.http.Endpoint;
import main.models.Task;
import main.managers.interfaces.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS:
                handleGetTask(exchange);
                break;
            case GET_TASK_ID:
                handleGetTaskById(exchange);
                break;
            case POST_TASK:
                handlePostTask(exchange);
                break;
            case DELETE_TASK_ID:
                handleDeleteTaskById(exchange);
                break;
            case UNKNOWN:
                sendNoSuchEndpoint(exchange);
                break;
        }
    }

    private void handleDeleteTaskById(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOpt = getTaskId(exchange);

        if (taskIdOpt.isEmpty()) {
            send400(exchange, "Передан некорректный идентификатор задачи");
        } else {
            try {
                taskManager.deleteTaskById(taskIdOpt.get());
                send200(exchange, "Задача удалена");
            } catch (NotFoundException e) {
                send404NotFound(exchange, e.getMessage());
            }
        }
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String jsonString = new String(inputStream.readAllBytes(), defaultCharset);

        if (jsonString.isEmpty() || jsonString.isBlank()) {
            send400(exchange, "Передана пустая задача");
        }

        try {
            Task taskFromJson = gson.fromJson(jsonString, Task.class);

            if (taskFromJson.getId() == null) {
                taskManager.createTask(taskFromJson);
                send201(exchange, "Задача добавлена");
            } else {
                taskManager.updateTask(taskFromJson);
                send201(exchange, "Задача обновлена");
            }
        } catch (IncorrectTaskException ex) {
            send400(exchange, "Некорректная задача");
        } catch (TaskOverlapException ex) {
            send406HasInteractions(exchange, "Задачи перекрываются");
        } catch (NotFoundException ex) {
            send404NotFound(exchange, ex.getMessage());
        } catch (JsonSyntaxException ex) {
            //Сервер не обнаружил запрашиваемый контент
            send500(exchange);
        }
    }

    private void handleGetTaskById(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOpt = getTaskId(exchange);

        if (taskIdOpt.isEmpty()) {
            send400(exchange, "Передан некорректный идентификатор задачи");
        } else {
            try {
                Optional<Task> taskOpt = taskManager.getTaskById(taskIdOpt.get());
                String jsonTask = gson.toJson(taskOpt.get());
                send200(exchange, jsonTask);
            } catch (NotFoundException e) {
                send404NotFound(exchange, e.getMessage());
            }
        }
    }

    private void handleGetTask(HttpExchange exchange) throws IOException {
        String jsonTasks = gson.toJson(taskManager.getAllTasks());
        send200(exchange, jsonTasks);
    }

    private Endpoint getEndpoint(String path, String requestMethod) {
        String[] pathParts = path.split("/");

        if (requestMethod.equals("GET") && pathParts.length == 2 && pathParts[1].equals("tasks")) {
            return Endpoint.GET_TASKS;
        } else if (requestMethod.equals("GET") && pathParts.length == 3 && pathParts[1].equals("tasks")) {
            return Endpoint.GET_TASK_ID;
        } else if (requestMethod.equals("POST") && pathParts.length == 2 && pathParts[1].equals("tasks")) {
            return Endpoint.POST_TASK;
        } else if (requestMethod.equals("DELETE") && pathParts.length == 3 && pathParts[1].equals("tasks")) {
            return Endpoint.DELETE_TASK_ID;
        }
        return Endpoint.UNKNOWN;
    }
}
