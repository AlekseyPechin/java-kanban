package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.HashMap;

public class TaskManager {

    private int idCount;

    protected HashMap<Integer, Task> taskArrays = new HashMap<>();
    protected HashMap<Integer, Subtask> subtaskArrays = new HashMap<>();
    protected HashMap<Integer, Epic> epicArrays = new HashMap<>();

    public TaskManager() {
        this.idCount = 0;
    }


}
