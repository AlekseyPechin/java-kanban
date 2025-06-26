package model;

import main.taskManagerAndHistoryManagerInterfaces.TaskManager;
import main.managers.InMemoryHistoryManager;
import main.models.Task;

import static main.models.Status.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.managers.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private static final TaskManager TASK_MANAGER = Managers.getInMemoryTaskManager(new InMemoryHistoryManager());
    private static final Task TASK_FOR_TEST = new Task(
            "Test addNewTask",
            "Test addNewTask description",
            NEW,
            LocalDateTime.now(),
            Duration.ofMinutes(1)
    );

    private Optional<Task> savedTask;
    private Task task;

    @BeforeEach
    void creatingTasks() {
        task = TASK_MANAGER.createTask(TASK_FOR_TEST);
        savedTask = TASK_MANAGER.getTaskById(Optional.of(task.getId()).get());
    }

    @Test
    void getName() {
        final String nameTask = TASK_FOR_TEST.getName();
        final String nameSavedTask = savedTask.get().getName();

        assertEquals(nameTask, nameSavedTask, "Неверное имя задачи");
    }

    @Test
    void getDescription() {
        final String descriptionTask = TASK_FOR_TEST.getDescription();
        final String descriptionSavedTask = savedTask.get().getDescription();

        assertEquals(descriptionTask, descriptionSavedTask, "Неверное описание задачи");
    }

    @Test
    void getId() {
        final int idSavedTask = savedTask.get().getId();

        assertEquals(task.getId(), idSavedTask, "Неверный Id задачи");
    }
}