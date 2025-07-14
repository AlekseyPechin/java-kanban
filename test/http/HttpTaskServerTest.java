package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.adapters.DurationAdapter;
import main.adapters.LocalDateTimeAdapter;
import main.http.HttpTaskServer;
import main.managers.Managers;
import main.managers.interfaces.TaskManager;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private final TaskManager taskManager = Managers.getInMemoryTaskManager(Managers.getDefaultHistory());
    private final HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();


    HttpTaskServerTest() throws IOException {
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
    void addTaskInternalServerError() throws IOException, InterruptedException {
        clearManager();
        String jsonString = "{\"name\":\"Test 17\"," +
                "\"description\":\"Testing task 17\"," +
                "\"status\":\"NEW\"," +
                "\"startTime\":\"21.06.2025 01:00:00\"" +
                "\"duration\":\"75m\"}";

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(500, response.statusCode());
    }
}