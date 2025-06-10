package managers;

import main.managers.FileBackedTaskManager;
import main.managers.Managers;
import main.model.Epic;
import main.model.Task;
import main.model.Status;
import main.model.Type;
import main.taskManagerAndHistoryManagerInterfaces.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    private static FileBackedTaskManager manager;
    private static FileBackedTaskManager fileManager;
    private static File file;


    @BeforeEach
    public void beforeEach() {
        TaskManager taskManager = Managers.getInMemoryTaskManager(Managers.getDefaultHistory());
        taskManager.deleteAll();
        try {
            file = File.createTempFile("data.test", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        manager = new FileBackedTaskManager(Managers.getDefaultHistory(), file);
    }

    @Test
    void shouldCorrectlySaveAndLoad() {
        Task task = new Task("Test", "description_Test", Status.NEW, Type.TASK);
        manager.addNewTask(task);
        Epic epic = new Epic("Посадить дерево", "Дерево", Status.NEW);
        manager.addNewEpic(epic);
        fileManager = new FileBackedTaskManager(Managers.getDefaultHistory(), file);
        assertEquals(List.of(task), fileManager.printAllTask());
        assertEquals(List.of(epic), fileManager.printAllEpic());
    }

    @Test
    public void shouldSaveAndLoadEmptyTasksEpicsSubtasks() {
        fileManager = new FileBackedTaskManager(Managers.getDefaultHistory(), file);
        fileManager.save();
        FileBackedTaskManager.loadFromFile(file);
        assertEquals(Collections.EMPTY_LIST, manager.printAllTask());
        assertEquals(Collections.EMPTY_LIST, manager.printAllEpic());
        assertEquals(Collections.EMPTY_LIST, manager.printAllSubtask());
    }
}