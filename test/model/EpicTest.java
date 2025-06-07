package model;

import main.interfacesForTaskProcessingAndOutput.TaskManager;
import main.managers.Managers;
import main.model.Epic;
import main.model.Subtask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static main.model.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private static final TaskManager TASK_MANAGER = Managers.getDefault();
    private static final Epic EPIC_TEST = new Epic("Epic", "Epic Description", NEW);
    private int idEpic;
    private Subtask subtask;
    private int idSubtask;

    @BeforeEach
    void beforeEach() {
        idEpic = TASK_MANAGER.addNewEpic(EPIC_TEST);
        subtask = new Subtask("Subtask", "Subtask description", NEW, idEpic);
        idSubtask = TASK_MANAGER.addNewSubtask(subtask);
    }

    @AfterEach
    void clearingTaskLists() {
        TASK_MANAGER.clearSubtaskArrays();
        TASK_MANAGER.getEpicById(idEpic).clearIdSubtaskArrays();
        TASK_MANAGER.clearEpicArrays();
    }

    @Test
    void getIdSubtasks() {
        final List<Integer> idSubtasks = TASK_MANAGER.getEpicById(idEpic).getIdSubtasks();

        assertEquals(1, idSubtasks.size(), "Список id подзадач пуст!");
    }

    @Test
    void clearIdSubtaskArrays() {
        final List<Integer> idSubtasks = TASK_MANAGER.getEpicById(idEpic).getIdSubtasks();

        assertEquals(1, idSubtasks.size(), "Список id подзадач пуст!");

        TASK_MANAGER.clearSubtaskArrays();

        assertEquals(0, idSubtasks.size(), "Список id подзадач не пуст!");
    }

    @Test
    void addIdSubtask() {
        final List<Integer> idSubtasks = TASK_MANAGER.getEpicById(idEpic).getIdSubtasks();

        assertEquals(idSubtask, idSubtasks.getFirst(), "id подзадач не совпадают!");
    }

    @Test
    void removeSubtaskById() {
        final List<Integer> idSubtasks = TASK_MANAGER.getEpicById(idEpic).getIdSubtasks();

        assertEquals(idSubtask, idSubtasks.getFirst(), "id подзадач не совпадают!");

        TASK_MANAGER.getEpicById(idEpic).removeSubtaskById(idSubtask);
        final List<Integer> idSubtasksAfterRemove = TASK_MANAGER.getEpicById(idEpic).getIdSubtasks();

        assertEquals(0, idSubtasksAfterRemove.size(), "Список id подзадач не пуст!");
    }
}