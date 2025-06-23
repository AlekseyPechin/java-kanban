package model;

import main.taskManagerAndHistoryManagerInterfaces.TaskManager;
import main.managers.InMemoryHistoryManager;
import main.managers.Managers;
import main.model.Epic;
import main.model.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static main.model.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    private static final TaskManager TASK_MANAGER = Managers.getInMemoryTaskManager(new InMemoryHistoryManager());
    private static final Epic EPIC_TEST = new Epic(
            "Epic",
            "Epic Description",
            NEW,
            LocalDateTime.now(),
            Duration.ofMinutes(1)
    );
    private static Epic epic;
    private Subtask subtask;

    @BeforeEach
    void beforeEach() {
        epic = TASK_MANAGER.createEpic(EPIC_TEST);
        subtask = new Subtask("Subtask", "Subtask description", NEW, epic.getId());
    }

    @Test
    void getIdEpic() {
        assertEquals(epic.getId(), subtask.getIdEpic(), "id Эпиков не совпадает");
    }
}