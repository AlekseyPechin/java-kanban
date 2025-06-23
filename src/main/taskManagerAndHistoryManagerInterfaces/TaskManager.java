package main.taskManagerAndHistoryManagerInterfaces;

import main.model.Epic;
import main.model.Subtask;
import main.model.Task;

import java.util.List;

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

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    List<Subtask> getAllSubtasksByEpicId(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateStatus(Epic epic);

    void updateSubtask(Subtask subtask);

    void printTasks();

    void printEpics();

    void printSubtasks();
}