package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.adapters.DurationAdapter;
import main.adapters.LocalDateTimeAdapter;
import main.http.HttpTaskServer;
import main.managers.Managers;
import main.managers.interfaces.TaskManager;
import main.models.Status;
import main.models.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TaskHandlerTest {
    private final TaskManager taskManager = Managers.getInMemoryTaskManager(Managers.getDefaultHistory());
    private final HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    TaskHandlerTest() throws IOException {
    }

    @BeforeEach
    void setUp() {
        httpTaskServer.start();
    }

    private void clearManager() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubtasks();
        taskManager.deleteAllEpics();
    }

    @AfterEach
    void stopServer() {
        httpTaskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        clearManager();

        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = taskManager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void getTasks() throws IOException, InterruptedException {
        clearManager();

        Task task1 = new Task(
                "Task 1",
                "Test task 1 description",
                Status.NEW,
                LocalDateTime.of(2025, 1, 1, 0, 0),
                Duration.ofHours(1)
        );

        Task task2 = new Task(
                "Task 2",
                "Test task 2 description",
                Status.NEW,
                LocalDateTime.of(2025, 1, 2, 8, 0),
                Duration.ofHours(1)
        );

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),
                "Ожидался код 200 в процессе получения списка задач");
        assertEquals(response.body(), gson.toJson(taskManager.getAllTasks()),
                "Тело ответа от сервера должно совпадать со списком taskManager.getAllTasksList()");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        clearManager();

        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        taskManager.createTask(task);
        Task task1 = taskManager.getAllTasks().getFirst();

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url + "/" + task1.getId())).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = taskManager.getAllTasks();

        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }
}