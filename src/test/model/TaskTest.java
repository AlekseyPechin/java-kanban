package test.model;

import interfaces.TaskManager;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;

import static model.Status.NEW;
import static model.Type.TASK;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private static final TaskManager TASK_MANAGER = Managers.getDefault();
    private static final Task TASK_FOR_TEST = new Task(
            "Test addNewTask",
            "Test addNewTask description",
            NEW,
            TASK
    );

    private Task savedTask;
    private int taskId;

    @BeforeEach
    void BeforeEach() {
        taskId = TASK_MANAGER.addNewTask(TASK_FOR_TEST);
        savedTask = TASK_MANAGER.getTaskById(taskId);
    }

    @Test
    void getName() {
        final String nameTask = TASK_FOR_TEST.getName();
        final String nameSavedTask = savedTask.getName();

        assertEquals(nameTask, nameSavedTask, "Неверное имя задачи");
    }

    @Test
    void getDescription() {
        final String descriptionTask = TASK_FOR_TEST.getDescription();
        final String descriptionSavedTask = savedTask.getDescription();

        assertEquals(descriptionTask, descriptionSavedTask, "Неверное описание задачи");
    }

    @Test
    void getId() {
        final int idSavedTask = savedTask.getId();

        assertEquals(taskId, idSavedTask, "Неверный Id задачи");
    }

}