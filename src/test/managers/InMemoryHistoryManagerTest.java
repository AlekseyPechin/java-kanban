package test.managers;

import main.interfaces.HistoryManager;
import main.interfaces.TaskManager;
import main.managers.Managers;
import main.model.Epic;
import main.model.Subtask;
import main.model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static main.model.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private static final TaskManager TASK_MANAGER = Managers.getDefault();
    private static final HistoryManager HISTORY_MANAGER = Managers.getDefaultHistory();

    private static int idEpic;
    private static int idTask;
    private static int idSubtask;

    @BeforeEach
    void BeforeEach() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        Epic epic = new Epic("Epic", "Epic Description", NEW);
        idEpic = TASK_MANAGER.addNewEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Subtask description", NEW, idEpic);
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

        List<Task> histories = TASK_MANAGER.getHistory();
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
            task = new Task(taskName, taskName + " description", NEW);
            idTaskForTest = TASK_MANAGER.addNewTask(task);
            TASK_MANAGER.getTaskById(idTaskForTest);

        }

        List<Task> histories = TASK_MANAGER.getHistory();
        assertEquals(10, histories.size(), "Количество просмотров не совпадает!");

        Task savedTask = histories.getFirst();
        String nameTaskForSaved = savedTask.getName();

        assertEquals("Task - 1", nameTaskForSaved, "Имя задачи не совпадает!");
    }
}