package managers;

import main.taskManagerAndHistoryManagerInterfaces.TaskManager;
import main.managers.InMemoryHistoryManager;
import main.managers.Managers;
import main.model.Epic;
import main.model.Subtask;
import main.model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static main.model.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private static final TaskManager TASK_MANAGER = Managers.getInMemoryTaskManager(new InMemoryHistoryManager());
    private static final Task TASK_TEST =
            new Task("Test addNewTask", "Test addNewTask description", NEW);
    private static final Epic EPIC_TEST =
            new Epic("Test addNewEpic", "Test addNewEpic description", NEW);

    @AfterEach
    void clearingTaskLists() {
        TASK_MANAGER.clearTaskArrays();
        TASK_MANAGER.clearEpicArrays();
        TASK_MANAGER.clearSubtaskArrays();
    }

    @Test
    void addNewTask() {
        final Task task = TASK_MANAGER.addNewTask(TASK_TEST);
        final Task savedTask = TASK_MANAGER.getTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(TASK_TEST, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = TASK_MANAGER.printAllTask();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(TASK_TEST, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void addNewEpic() {
        final Epic epic = TASK_MANAGER.addNewEpic(EPIC_TEST);
        final Epic savedEpic = TASK_MANAGER.getEpicById(epic.getId());

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(EPIC_TEST, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = TASK_MANAGER.printAllEpic();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(EPIC_TEST, epics.getFirst(), "Эпики не совпадают.");
        assertEquals(EPIC_TEST, savedEpic, "Эпики не совпадают.");
    }

    @Test
    void addNewSubtask() {
        final Epic epic = TASK_MANAGER.addNewEpic(EPIC_TEST);
        Subtask subtaskTest =
                new Subtask("Subtask", "Subtask description", NEW, epic.getId());
        final Subtask subtask = TASK_MANAGER.addNewSubtask(subtaskTest);
        final Subtask savedSubtask = TASK_MANAGER.getSubtaskById(subtask.getId());

        assertNotNull(savedSubtask, "Подзадача не найден.");
        assertEquals(subtaskTest, savedSubtask, "Подзадачи не совпадают.");

    }

    @Test
    void updateTask() {
        final Task task = TASK_MANAGER.addNewTask(TASK_TEST);
        Task taskForUpdate =
                new Task("Test addNewTaskForUpdate", "Test addNewTaskForUpdate description", NEW);
        TASK_MANAGER.updateTask(taskForUpdate);
        final List<Task> tasks = TASK_MANAGER.printAllTask();
        final int idTaskForUpdate = tasks.getFirst().getId();

        assertNotEquals(TASK_TEST, taskForUpdate, "Задачи одинаковые!");
        assertEquals(task.getId(), idTaskForUpdate, "id задач разные!");


        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(TASK_TEST, tasks.getFirst(), "Задачи совпадают.");
    }

    @Test
    void updateEpic() {
        final Epic epic = TASK_MANAGER.addNewEpic(EPIC_TEST);
        Epic epicForUpdate =
                new Epic("Test addNewEpicForUpdate", "Test addNewEpicForUpdate description", NEW);
        TASK_MANAGER.updateEpic(epicForUpdate);
        final List<Epic> epics = TASK_MANAGER.printAllEpic();
        final int idEpicForUpdate = epics.getFirst().getId();

        assertNotEquals(EPIC_TEST, epicForUpdate, "Эпики одинаковые!");
        assertEquals(epic.getId(), idEpicForUpdate, "id эпиков одинаковые!");
        assertNotNull(epics, "Эпики не возвращаются!");
        assertEquals(EPIC_TEST, epics.getFirst(), "Эпики совпадают!");
    }

    @Test
    void updateSubtask() {
        final Epic epic = TASK_MANAGER.addNewEpic(EPIC_TEST);
        TASK_MANAGER.getEpicById(epic.getId()).clearIdSubtasks();
        Subtask subtaskTest =
                new Subtask("Subtask", "Subtask description", NEW, epic.getId());
        final Subtask subtask = TASK_MANAGER.addNewSubtask(subtaskTest);
        Subtask subtaskForUpdate =
                new Subtask("SubtaskFotUpdate", "SubtaskFotUpdate description", NEW, epic.getId());
        final List<Subtask> subtasks = TASK_MANAGER.printAllSubtask();
        TASK_MANAGER.updateSubtask(subtaskForUpdate);
        final int idSubtaskForUpdate = subtasks.getFirst().getId();

        assertNotEquals(subtaskTest, subtaskForUpdate, "Подзадачи одинаковые!");
        assertEquals(subtask.getId(), idSubtaskForUpdate, "id подзадач одинаковые!");
        assertNotNull(subtasks, "Подзадачи не возвращаются!");
        assertEquals(subtaskTest, subtasks.getFirst(), "Подзадачи совпадают!");
    }

    @Test
    void getTaskById() {
        final Task task = TASK_MANAGER.addNewTask(TASK_TEST);
        Task taskById = TASK_MANAGER.getTaskById(task.getId());

        assertNotNull(taskById, "Задача не пустая.");
        assertEquals(TASK_TEST, taskById, "Задачи одинаковые!");
    }

    @Test
    void getSubtaskById() {
        final Epic epic = TASK_MANAGER.addNewEpic(EPIC_TEST);
        TASK_MANAGER.getEpicById(epic.getId()).clearIdSubtasks();
        Subtask subtaskTest =
                new Subtask("Subtask", "Subtask description", NEW, epic.getId());
        final Subtask subtask = TASK_MANAGER.addNewSubtask(subtaskTest);
        Subtask subtaskById = TASK_MANAGER.getSubtaskById(subtask.getId());

        assertNotNull(subtaskById, "Подзадача не пустая!");
        assertEquals(subtaskTest, subtaskById, "Подзадачи одинаковые!");
    }

    @Test
    void getEpicById() {
        final Epic epic = TASK_MANAGER.addNewEpic(EPIC_TEST);
        final Epic epicById = TASK_MANAGER.getEpicById(epic.getId());

        assertNotNull(epicById, "Эпик не пустой!");
        assertEquals(EPIC_TEST, epicById, "Эпики совпадают!");
    }

    @Test
    void deleteTaskById() {
        final Task task = TASK_MANAGER.addNewTask(TASK_TEST);
        final List<Task> tasks = TASK_MANAGER.printAllTask();

        assertEquals(1, tasks.size(), "Список задач пустой!");

        TASK_MANAGER.deleteTaskById(task.getId());
        final List<Task> tasksAfterDelete = TASK_MANAGER.printAllTask();
        assertEquals(0, tasksAfterDelete.size(), "Список задач не пустой!");
    }

    @Test
    void deleteEpicById() {
        final Epic epic = TASK_MANAGER.addNewEpic(EPIC_TEST);
        TASK_MANAGER.getEpicById(epic.getId()).clearIdSubtasks();
        Subtask subtaskTest =
                new Subtask("Subtask", "Subtask description", NEW, epic.getId());
        TASK_MANAGER.addNewSubtask(subtaskTest);
        final List<Epic> epics = TASK_MANAGER.printAllEpic();

        assertEquals(1, epics.size(), "Список эпиков пустой!");

        TASK_MANAGER.deleteEpicById(epic.getId());
        final List<Epic> epicsAfterDelete = TASK_MANAGER.printAllEpic();
        assertEquals(0, epicsAfterDelete.size(), "Список эпиков не пустой!");
    }

    @Test
    void deleteSubtaskById() {
        final Epic epic = TASK_MANAGER.addNewEpic(EPIC_TEST);
        TASK_MANAGER.getEpicById(epic.getId()).clearIdSubtasks();
        Subtask subtaskTest =
                new Subtask("Subtask", "Subtask description", NEW, epic.getId());
        final Subtask subtask = TASK_MANAGER.addNewSubtask(subtaskTest);
        final List<Subtask> subtasks = TASK_MANAGER.printAllSubtask();

        assertEquals(1, subtasks.size(), "Список подзадач пустой!");

        int id = -1;
        List<Integer> idSubtasks = TASK_MANAGER.getEpicById(epic.getId()).getIdSubtasks();
        for (Integer idSubtask : idSubtasks) {
            id = idSubtask;
        }
        assertEquals(subtask.getId(), id);

        TASK_MANAGER.deleteSubtaskById(subtask.getId());
        List<Integer> idSubtasksAfterDelete = TASK_MANAGER.getEpicById(epic.getId()).getIdSubtasks();
        for (Integer idSubtask : idSubtasksAfterDelete) {
            id = idSubtask;
        }
        assertEquals(subtask.getId(), id, "Подзадача не удалена!");
    }

    @Test
    void clearTaskArrays() {
        TASK_MANAGER.addNewTask(TASK_TEST);
        final List<Task> tasks = TASK_MANAGER.printAllTask();

        assertEquals(1, tasks.size(), "Список задач пустой!");

        TASK_MANAGER.clearTaskArrays();

        final List<Task> tasksAfterClear = TASK_MANAGER.printAllTask();

        assertEquals(0, tasksAfterClear.size(), "Список задач не пустой!");
    }

    @Test
    void clearEpicArrays() {
        final Epic epic = TASK_MANAGER.addNewEpic(EPIC_TEST);
        Subtask subtaskTest =
                new Subtask("Subtask", "Subtask description", NEW, epic.getId());
        TASK_MANAGER.addNewSubtask(subtaskTest);
        final List<Epic> epics = TASK_MANAGER.printAllEpic();

        assertEquals(1, epics.size(), "Список эпиков пустой!");

        TASK_MANAGER.clearEpicArrays();

        final List<Epic> epicsAfterClear = TASK_MANAGER.printAllEpic();

        assertEquals(0, epicsAfterClear.size(), "Список эпиков не пустой");
    }

    @Test
    void clearSubtaskArrays() {
        final Epic epic = TASK_MANAGER.addNewEpic(EPIC_TEST);
        TASK_MANAGER.getEpicById(epic.getId()).clearIdSubtasks();
        Subtask subtaskTest =
                new Subtask("Subtask", "Subtask description", NEW, epic.getId());
        TASK_MANAGER.addNewSubtask(subtaskTest);
        final List<Subtask> subtasks = TASK_MANAGER.printAllSubtask();

        assertEquals(1, subtasks.size(), "Список подзадач пустой!");

        TASK_MANAGER.clearSubtaskArrays();

        final List<Subtask> subtasksAfterClear = TASK_MANAGER.printAllSubtask();

        assertEquals(0, subtasksAfterClear.size(), "Список подзадач не пустой");
    }

}