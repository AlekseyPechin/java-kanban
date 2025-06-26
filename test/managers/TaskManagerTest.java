package managers;


import main.managers.InMemoryHistoryManager;
import main.managers.InMemoryTaskManager;
import main.models.Epic;
import main.models.Status;
import main.models.Subtask;
import main.models.Task;
import main.taskManagerAndHistoryManagerInterfaces.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    TaskManager taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
    protected T manager;
    protected LocalDateTime startTime = LocalDateTime.now();
    protected Duration duration = Duration.ofMinutes(1);
    protected Task createTask() {
        return new Task(
                "Title",
                "Description",
                Status.NEW,
                startTime,
                duration);
    }
    protected Epic createEpic() {

        return new Epic(
                "Title",
                "Description",
                Status.NEW,
                startTime,
                duration);
    }
    protected Subtask createSubtask(Epic epic) {
        return new Subtask(
                "Title",
                "Description",
                Status.NEW,
                startTime,
                duration,
                epic.getId()
        );
    }

    @BeforeEach
    public void beforeEach() {
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();
    }

    @Test
    public void shouldCreateTask() {
        Task task = createTask();
        manager.createTask(task);
        List<Task> tasks = manager.getAllTasks();
        assertNotNull(task.getStatus());
        assertEquals(Status.NEW, task.getStatus());
        assertEquals(List.of(task), tasks);
    }

    @Test
    public void shouldCreateEpic() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        List<Epic> epics = manager.getAllEpics();
        assertNotNull(epic.getStatus());
        assertEquals(Status.NEW, epic.getStatus());
        assertEquals(Collections.EMPTY_LIST, epic.getIdSubtasks());
        assertEquals(List.of(epic), epics);
    }

    @Test
    public void shouldCreateSubtask() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        List<Subtask> subtasks = manager.getAllSubtasks();
        assertNotNull(subtask.getStatus());
        assertEquals(epic.getId(), subtask.getIdEpic());
        assertEquals(Status.NEW, subtask.getStatus());
        System.out.println(subtasks.size());
        assertEquals(List.of(subtask), subtasks);
        assertEquals(List.of(subtask.getId()), epic.getIdSubtasks());
    }

    @Test
    void shouldReturnNullWhenCreateTaskNull() {
        Task task = manager.createTask(null);
        assertNull(task);
    }

    @Test
    void shouldReturnNullWhenCreateEpicNull() {
        Epic epic = manager.createEpic(null);
        assertNull(epic);
    }

    @Test
    void shouldReturnNullWhenCreateSubtaskNull() {
        Subtask subtask = manager.createSubtask(null);
        assertNull(subtask);
    }

    @Test
    public void shouldUpdateTaskStatusToInProgress() {
        Task task = createTask();
        manager.createTask(task);
        task.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task);
        assertEquals(Status.IN_PROGRESS, manager.getTaskById(task.getId()).get().getStatus());
    }

    @Test
    public void shouldUpdateEpicStatusToInProgress() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        epic.setStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).get().getStatus());
    }

    @Test
    public void shouldUpdateSubtaskStatusToInProgress() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        subtask.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask);
        assertEquals(Status.IN_PROGRESS, manager.getSubtaskById(subtask.getId()).get().getStatus());
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).get().getStatus());
    }

    @Test
    public void shouldUpdateTaskStatusToInDone() {
        Task task = createTask();
        manager.createTask(task);
        task.setStatus(Status.DONE);
        manager.updateTask(task);
        assertEquals(Status.DONE, manager.getTaskById(task.getId()).get().getStatus());
    }

    @Test
    public void shouldUpdateEpicStatusToInDone() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        epic.setStatus(Status.DONE);
        assertEquals(Status.DONE, manager.getEpicById(epic.getId()).get().getStatus());
    }

    @Test
    public void shouldUpdateSubtaskStatusToInDone() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        subtask.setStatus(Status.DONE);
        manager.updateSubtask(subtask);
        assertEquals(Status.DONE, manager.getSubtaskById(subtask.getId()).get().getStatus());
        assertEquals(Status.DONE, manager.getEpicById(epic.getId()).get().getStatus());
    }

    @Test
    public void shouldNotUpdateTaskIfNull() {
        Task task = createTask();
        manager.createTask(task);
        manager.updateTask(null);
        assertEquals(Optional.of(task).toString(), manager.getTaskById(task.getId()).toString());
    }

    @Test
    public void shouldNotUpdateEpicIfNull() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        manager.updateEpic(null);
        assertEquals(Optional.of(epic).toString(), manager.getEpicById(epic.getId()).toString());
    }

    @Test
    public void shouldNotUpdateSubtaskIfNull() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        manager.updateSubtask(null);
        assertEquals(Optional.of(subtask).toString(), manager.getSubtaskById(subtask.getId()).toString());
    }

    @Test
    public void shouldDeleteAllTasks() {
        Task task = createTask();
        manager.createTask(task);
        manager.deleteAllTasks();
        assertEquals(Collections.EMPTY_LIST, manager.getAllTasks());
    }

    @Test
    public void shouldDeleteAllEpics() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        manager.deleteAllEpics();
        assertEquals(Collections.EMPTY_LIST, manager.getAllEpics());
    }

    @Test
    public void shouldDeleteAllSubtasks() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        manager.deleteAllSubtasks();
        assertTrue(epic.getIdSubtasks().isEmpty());
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    public void shouldDeleteAllSubtasksByEpic() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        manager.deleteAllSubtasksByEpic(epic);
        assertTrue(epic.getIdSubtasks().isEmpty());
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    public void shouldDeleteTaskById() {
        Task task = createTask();
        manager.createTask(task);
        manager.deleteTaskById(task.getId());
        assertEquals(Collections.EMPTY_LIST, manager.getAllTasks());
    }

    @Test
    public void shouldDeleteEpicById() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        manager.deleteEpicById(epic.getId());
        assertEquals(Collections.EMPTY_LIST, manager.getAllEpics());
    }

    @Test
    public void shouldNotDeleteTaskIfBadId() {
        Task task = createTask();
        manager.createTask(task);
        manager.deleteTaskById(999);
        assertEquals(List.of(task), manager.getAllTasks());
    }

    @Test
    public void shouldNotDeleteEpicIfBadId() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        manager.deleteEpicById(999);
        assertEquals(List.of(epic), manager.getAllEpics());
    }

    @Test
    public void shouldNotDeleteSubtaskIfBadId() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        manager.deleteSubtaskById(999);
        assertEquals(List.of(subtask), manager.getAllSubtasks());
        assertEquals(List.of(subtask.getId()), manager.getEpicById(epic.getId()).get().getIdSubtasks());
    }

    @Test
    public void shouldDoNothingIfTaskHashMapIsEmpty(){
        manager.deleteAllTasks();
        manager.deleteTaskById(999);
        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    public void shouldDoNothingIfEpicHashMapIsEmpty(){
        manager.deleteAllEpics();
        manager.deleteEpicById(999);
        assertTrue(manager.getAllEpics().isEmpty());
    }

    @Test
    public void shouldDoNothingIfSubtaskHashMapIsEmpty(){
        manager.deleteAllEpics();
        manager.deleteSubtaskById(999);
        assertEquals(0, manager.getAllSubtasks().size());
    }

    @Test
    void shouldReturnEmptyListWhenGetSubtaskByEpicIdIsEmpty() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        List<Subtask> subtasks = manager.getAllSubtasksByEpicId(epic.getId());
        assertTrue(subtasks.isEmpty());
    }

    @Test
    public void shouldReturnEmptyListTasksIfNoTasks() {
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    public void shouldReturnEmptyListEpicsIfNoEpics() {
        assertTrue(manager.getAllEpics().isEmpty());
    }

    @Test
    public void shouldReturnEmptyListSubtasksIfNoSubtasks() {
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    public void shouldReturnNullIfTaskDoesNotExist() {
        assertNull(manager.getTaskById(999));
    }

    @Test
    public void shouldReturnNullIfEpicDoesNotExist() {
        assertNull(manager.getEpicById(999));
    }

    @Test
    public void shouldReturnNullIfSubtaskDoesNotExist() {
        assertNull(manager.getSubtaskById(999));
    }

    @Test
    public void shouldReturnEmptyHistory() {
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }

    @Test
    public void shouldReturnEmptyHistoryIfTasksNotExist() {
        manager.getTaskById(999);
        manager.getSubtaskById(999);
        manager.getEpicById(999);
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    public void shouldReturnHistoryWithTasks() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        manager.getEpicById(epic.getId());
        manager.getSubtaskById((Integer) subtask.getId());
        List<Task> list = manager.getHistory();
        assertEquals(2, list.size());
        assertTrue(list.contains(subtask));
        assertTrue(list.contains(epic));
    }

}
