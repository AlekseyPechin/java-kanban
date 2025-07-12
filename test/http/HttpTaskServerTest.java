package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.adapters.DurationAdapter;
import main.adapters.LocalDateTimeAdapter;
import main.http.HttpTaskServer;
import main.managers.Managers;
import main.models.Epic;
import main.models.Status;
import main.models.Task;
import main.taskManagerAndHistoryManagerInterfaces.TaskManager;
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

class HttpTaskServerTest {
    private TaskManager taskManager = Managers.getInMemoryTaskManager(Managers.getDefaultHistory());
    private HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();


    HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    void setUp() throws IOException {
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
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic(
                "Test 2",
                "Testing task 2",
                Status.NEW,
                LocalDateTime.now(),
                Duration.ofMinutes(5)
        );

        // конвертируем её в JSON
        String taskJson = gson.toJson(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Epic> epicsFromManager = taskManager.getAllEpics();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Test 2", epicsFromManager.get(0).getName(), "Некорректное имя эпика");
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
    public void getEpics() throws IOException, InterruptedException {
        clearManager();

        Epic epic1 = new Epic(
                "Epic 1",
                "Test epic 1 description",
                Status.NEW,
                LocalDateTime.of(2025, 1, 1, 0, 0),
                Duration.ofHours(1)
        );
        Epic epic2 = new Epic(
                "Epic 2",
                "Test epic 2 description",
                Status.NEW,
                LocalDateTime.of(2025, 1, 2, 0, 0),
                Duration.ofHours(1)
        );

        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),
                "Ожидался код 200 в процессе получения списка эпиков");

        assertEquals(response.body(), gson.toJson(taskManager.getAllEpics()),
                "Тело ответа от сервера должно совпадать со списком taskManager.getAllEpicsList()");
    }

    @Test
    public void shouldGetHistory() throws IOException, InterruptedException {
        clearManager();

        Task task1 = new Task(
                "Task 1",
                "Test task 1 description",
                Status.NEW,
                LocalDateTime.of(2025, 1, 1, 6, 0),
                Duration.ofHours(1)
        );
        Epic epic1 = new Epic(
                "Epic 1",
                "Test epic 1 description",
                Status.NEW,
                LocalDateTime.of(2025, 1, 1, 8, 0),
                Duration.ofHours(1)
        );
        Epic epic2 = new Epic(
                "Epic 2",
                "Test epic 2 description",
                Status.NEW,
                LocalDateTime.of(2025, 1, 2, 10, 0),
                Duration.ofHours(1)
        );

        taskManager.createTask(task1);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic2.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),
                "Ожидался код 200 в процессе получения списка истории задач");

        assertEquals(response.body(), gson.toJson(taskManager.getHistory()),
                "Тело ответа от сервера должно совпадать со списком taskManager.getHistory()");
    }

    @Test
    public void shouldGetPrioritized() throws IOException, InterruptedException {
        clearManager();

        Task task1 = new Task(
                "Task 1",
                "Test task 1 description",
                Status.NEW,
                LocalDateTime.of(2025, 1, 1, 6, 0),
                Duration.ofHours(1)
        );
        Epic epic1 = new Epic(
                "Epic 1",
                "Test epic 1 description",
                Status.NEW,
                LocalDateTime.of(2025, 1, 1, 8, 0),
                Duration.ofHours(1)
        );
        Epic epic2 = new Epic(
                "Epic 2",
                "Test epic 2 description",
                Status.NEW,
                LocalDateTime.of(2025, 1, 2, 10, 0),
                Duration.ofHours(1)
        );

        taskManager.createTask(task1);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),
                "Ожидался код 200 в процессе получения списка приоритетных задач");

        var res = taskManager.getPrioritizedTasks();

        assertEquals(response.body(), gson.toJson(taskManager.getPrioritizedTasks()),
                "Тело ответа от сервера должно совпадать со списком taskManager.getPrioritizedTasks()");
    }
}