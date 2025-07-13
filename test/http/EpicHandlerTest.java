package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.adapters.DurationAdapter;
import main.adapters.LocalDateTimeAdapter;
import main.http.HttpTaskServer;
import main.managers.Managers;
import main.managers.interfaces.TaskManager;
import main.models.Epic;
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

import static org.junit.jupiter.api.Assertions.*;

class EpicHandlerTest {

    private final TaskManager taskManager = Managers.getInMemoryTaskManager(Managers.getDefaultHistory());
    private final HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    EpicHandlerTest() throws IOException {
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
        assertEquals("Test 2", epicsFromManager.getFirst().getName(), "Некорректное имя эпика");
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
    public void testDeleteEpic() throws IOException, InterruptedException {
        clearManager();

        // создаём задачу
        Epic epic = new Epic(
                "Epic 1",
                "Test epic 1 description",
                Status.NEW,
                LocalDateTime.of(2025, 1, 1, 0, 0),
                Duration.ofHours(1)
        );

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        taskManager.createEpic(epic);
        Epic epic1 = taskManager.getAllEpics().getFirst();

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url + "/" + epic1.getId())).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = taskManager.getAllTasks();

        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }
}