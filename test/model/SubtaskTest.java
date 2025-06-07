package model;

import main.interfacesForTaskProcessingAndOutput.TaskManager;
import main.managers.Managers;
import main.model.Epic;
import main.model.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static main.model.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    private static final TaskManager TASK_MANAGER = Managers.getDefault();
    private static final Epic EPIC_TEST = new Epic("Epic", "Epic Description", NEW);
    private static int idEpic;
    private Subtask subtask;

    @BeforeEach
    void beforeEach() {
        idEpic = TASK_MANAGER.addNewEpic(EPIC_TEST);
        subtask = new Subtask("Subtask", "Subtask description", NEW, idEpic);
    }

    @Test
    void getIdEpic() {
        assertEquals(idEpic, subtask.getIdEpic(), "id Эпиков не совпадает");
    }
}