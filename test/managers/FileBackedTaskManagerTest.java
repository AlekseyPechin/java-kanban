package managers;

import main.managers.FileBackedTaskManager;
import main.managers.InMemoryTaskManager;
import main.managers.Managers;
import main.model.Epic;
import main.model.Status;
import main.model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTasksManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    protected LocalDateTime startTime = LocalDateTime.now();
    protected Duration duration = Duration.ofMinutes(1);
    public static final Path path = Path.of("src/saveFileTest.CSV");
    File file = new File(String.valueOf(path));
    @BeforeEach
    public void beforeEach() {
        manager = new FileBackedTaskManager(Managers.getDefaultHistory(), file);
    }

    @AfterEach
    public void afterEach() {
        try {
            Files.delete(path);
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Test
    public void shouldCorrectlySaveAndLoad() {
        Task task = new Task(
                "Description",
                "Title",
                Status.NEW,
                startTime,
                duration
        );
        manager.createTask(task);
        Epic epic = new Epic(
                "Description",
                "Title",
                Status.NEW,
                startTime,
                duration
        );
        manager.createEpic(epic);
        FileBackedTaskManager fileManager = new FileBackedTaskManager(Managers.getDefaultHistory(), file);
        fileManager.loadFromFile(file);
        assertEquals(List.of(task), manager.getAllTasks());
        assertEquals(List.of(epic), manager.getAllEpics());
    }

    @Test
    public void shouldSaveAndLoadEmptyTasksEpicsSubtasks() {
        FileBackedTaskManager fileManager = new FileBackedTaskManager(Managers.getDefaultHistory(), file);
        fileManager.save();
        fileManager.loadFromFile(file);
        assertEquals(Collections.EMPTY_LIST, manager.getAllTasks());
        assertEquals(Collections.EMPTY_LIST, manager.getAllEpics());
        assertEquals(Collections.EMPTY_LIST, manager.getAllSubtasks());
    }

    @Test
    public void shouldSaveAndLoadEmptyHistory() {
        FileBackedTaskManager fileManager = new FileBackedTaskManager(Managers.getDefaultHistory(), file);
        fileManager.save();
        fileManager.loadFromFile(file);
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }
}
