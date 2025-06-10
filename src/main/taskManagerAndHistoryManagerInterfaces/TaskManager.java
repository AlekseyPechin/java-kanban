package main.taskManagerAndHistoryManagerInterfaces;

import main.model.Epic;
import main.model.Subtask;
import main.model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    Task addNewTask(Task task);

    Epic addNewEpic(Epic epic);

    Subtask addNewSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    ArrayList<Task> printAllTask();

    ArrayList<Subtask> printAllSubtask();

    ArrayList<Epic> printAllEpic();

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    void clearTaskArrays();

    void clearEpicArrays();

    void clearSubtaskArrays();

    void deleteAll();

    List<Task> getHistory();
}

