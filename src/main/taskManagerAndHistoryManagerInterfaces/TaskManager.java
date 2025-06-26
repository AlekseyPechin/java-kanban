package main.taskManagerAndHistoryManagerInterfaces;

import main.models.Epic;
import main.models.Subtask;
import main.models.Task;

import java.util.List;
import java.util.Optional;

public interface TaskManager {
    List<Task> getHistory();

    void remove(int id);

    Task createTask(Task task);

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    void deleteAllSubtasksByEpic(Epic epic);

    Optional<Task> getTaskById(Integer id);

    Optional<Epic> getEpicById(Integer id);

    Optional<Subtask> getSubtaskById(Integer id);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    List<Subtask> getAllSubtasksByEpicId(Integer id);

    Optional<Task> updateTask(Task task);

    Optional<Epic> updateEpic(Epic epic);

    void updateStatus(Epic epic);

    Optional<Subtask> updateSubtask(Subtask subtask);

    void printTasks();

    void printEpics();

    void printSubtasks();
}