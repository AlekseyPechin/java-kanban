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
import main.models.Subtask;
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

class SubtaskHandlerTest {

    private final TaskManager taskManager = Managers.getInMemoryTaskManager(Managers.getDefaultHistory());
    private final HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    SubtaskHandlerTest() throws IOException {
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
    public void testAddSubtask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic(
                "Test 2",
                "Testing task 2",
                Status.NEW,
                LocalDateTime.now(),
                Duration.ofMinutes(5)
        );
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(
                "Subtask Test",
                "Testing subtask",
                Status.NEW,
                LocalDateTime.now(),
                Duration.ofMinutes(5),
                epic.getId()
        );

        // конвертируем её в JSON
        String subtaskJson = gson.toJson(subtask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest requestSubtask = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(requestSubtask, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Subtask> subtasksFromManager = taskManager.getAllSubtasks();

        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Subtask Test", subtasksFromManager.getFirst().getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void getSubtasks() throws IOException, InterruptedException {
        clearManager();

        Epic epic = new Epic(
                "Epic 1",
                "Test epic 1 description",
                Status.NEW,
                LocalDateTime.of(2025, 1, 1, 0, 0),
                Duration.ofHours(1)
        );
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(
                "Subtask Test",
                "Testing subtask",
                Status.NEW,
                LocalDateTime.now(),
                Duration.ofMinutes(5),
                epic.getId()
        );
        taskManager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),
                "Ожидался код 200 в процессе получения списка эпиков");

        assertEquals(response.body(), gson.toJson(taskManager.getAllSubtasks()),
                "Тело ответа от сервера должно совпадать со списком taskManager.getAllSubtasks()");
    }
}