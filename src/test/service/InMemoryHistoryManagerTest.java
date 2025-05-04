package test.service;

import interfaces.HistoryManager;
import interfaces.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;

import java.util.List;

import static model.Status.NEW;
import static model.Type.*;
import static model.Type.TASK;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private static final TaskManager TASK_MANAGER = Managers.getDefault();
    private static final HistoryManager HISTORY_MANAGER = Managers.getDefaultHistory();

    private static int idEpic;
    private static int idTask;
    private static int idSubtask;

    @BeforeEach
    void BeforeEach() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW, TASK);
        Epic epic = new Epic("Epic", "Epic Description", NEW, EPIC);
        idEpic = TASK_MANAGER.addNewEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Subtask description", NEW, SUBTASK, idEpic);
        idTask = TASK_MANAGER.addNewTask(task);
        idSubtask = TASK_MANAGER.addNewSubtask(subtask);
    }

    @AfterEach
    void AfterEach() {
        HISTORY_MANAGER.getHistory().clear();
        TASK_MANAGER.clearTaskArrays();
        TASK_MANAGER.clearEpicArrays();
        TASK_MANAGER.clearSubtaskArrays();
    }

    @Test
    void add() {
        TASK_MANAGER.getTaskById(idTask);
        TASK_MANAGER.getEpicById(idSubtask);
        TASK_MANAGER.getSubtaskById(idEpic);

        final List<Task> histories = HISTORY_MANAGER.getHistory();

        assertNotNull(histories, "Список просмотров пуст!");
        assertEquals(3, histories.size(), "Количество просмотров не совпадает!");
    }

    @Test
    void getHistory() {
        int idTaskCount;
        int idTaskForTest;

        Task task;
        for (idTaskCount = 0; idTaskCount < 11; idTaskCount++) {
            String taskName = "Task - " + idTaskCount;
            task = new Task(taskName, taskName + " description", NEW, TASK);
            idTaskForTest = TASK_MANAGER.addNewTask(task);
            TASK_MANAGER.getTaskById(idTaskForTest);

        }
        final List<Task> historiesMax = HISTORY_MANAGER.getHistory();
        assertEquals(10, historiesMax.size(), "Количество просмотров не совпадает!");

        Task savedTask = historiesMax.getFirst();
        String nameTaskForSaved = savedTask.getName();

        assertEquals("Task - 1", nameTaskForSaved, "Имя задачи не совпадает!");
    }

}