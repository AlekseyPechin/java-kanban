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

import static org.junit.jupiter.api.Assertions.*;

class HistoryHandlerTest {

    private final TaskManager taskManager = Managers.getInMemoryTaskManager(Managers.getDefaultHistory());
    private final HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();


    HistoryHandlerTest() throws IOException {
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
}