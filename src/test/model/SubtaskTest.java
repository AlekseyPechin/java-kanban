package test.model;

import interfaces.TaskManager;
import model.Epic;
import model.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;

import static model.Status.NEW;
import static model.Type.EPIC;
import static model.Type.SUBTASK;
import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    private static final TaskManager TASK_MANAGER = Managers.getDefault();
    private static final Epic EPIC_TEST = new Epic("Epic", "Epic Description", NEW, EPIC);
    private static int idEpic;
    private Subtask subtask;

    @BeforeEach
    void BeforeEach() {
        idEpic = TASK_MANAGER.addNewEpic(EPIC_TEST);
        subtask = new Subtask("Subtask", "Subtask description", NEW, SUBTASK, idEpic);
    }
    @Test
    void getIdEpic() {
        assertEquals(idEpic, subtask.getIdEpic(), "id Эпиков не совпадает");
    }

}