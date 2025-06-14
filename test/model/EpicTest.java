package model;

import main.taskManagerAndHistoryManagerInterfaces.TaskManager;
import main.managers.InMemoryHistoryManager;
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

    private static final TaskManager TASK_MANAGER = Managers.getInMemoryTaskManager(new InMemoryHistoryManager());
    private static final Epic EPIC_TEST = new Epic("Epic", "Epic Description", NEW);
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void beforeEach() {
        epic = TASK_MANAGER.addNewEpic(EPIC_TEST);
        subtask = TASK_MANAGER.addNewSubtask(new Subtask("Subtask", "Subtask description", NEW, epic.getId()));
    }

    @AfterEach
    void clearingTaskLists() {
        TASK_MANAGER.clearSubtaskArrays();
        TASK_MANAGER.getEpicById(epic.getId()).clearIdSubtasks();
        TASK_MANAGER.clearEpicArrays();
    }

    @Test
    void getIdSubtasks() {
        final List<Integer> idSubtasks = TASK_MANAGER.getEpicById(epic.getId()).getIdSubtasks();

        assertEquals(1, idSubtasks.size(), "Список id подзадач пуст!");
    }

    @Test
    void clearIdSubtaskArrays() {
        final List<Integer> idSubtasks = TASK_MANAGER.getEpicById(epic.getId()).getIdSubtasks();

        assertEquals(1, idSubtasks.size(), "Список id подзадач пуст!");

        TASK_MANAGER.clearSubtaskArrays();

        assertEquals(0, idSubtasks.size(), "Список id подзадач не пуст!");
    }

    @Test
    void addIdSubtask() {
        final List<Integer> idSubtasks = TASK_MANAGER.getEpicById(epic.getId()).getIdSubtasks();

        assertEquals(subtask.getId(), idSubtasks.getFirst(), "id подзадач не совпадают!");
    }

    @Test
    void removeSubtaskById() {
        final List<Integer> idSubtasks = TASK_MANAGER.getEpicById(epic.getId()).getIdSubtasks();

        assertEquals(subtask.getId(), idSubtasks.getFirst(), "id подзадач не совпадают!");

        TASK_MANAGER.getEpicById(epic.getId()).removeSubtaskById(subtask.getId());
        final List<Integer> idSubtasksAfterRemove = TASK_MANAGER.getEpicById(epic.getId()).getIdSubtasks();

        assertEquals(0, idSubtasksAfterRemove.size(), "Список id подзадач не пуст!");
    }
}