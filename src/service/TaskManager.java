package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private int idCount;

    protected HashMap<Integer, Task> taskHashMap = new HashMap<>();
    protected HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();
    protected HashMap<Integer, Epic> epicHashMap = new HashMap<>();

    public TaskManager() {
        this.idCount = 0;
    }

    private Integer generateId() {
        return ++idCount;
    }

    public void addNewTask(Task task) {
        if (task != null) {
            int id = generateId();
            task.setId(id);
            taskHashMap.put(id, task);
        }
    }

    public void addNewEpic(Epic epic) {
        if (epic != null) {
            int id = generateId();
            epic.setId(id);
            epicHashMap.put(id, epic);
        }
    }

    public void addNewSubtask(Subtask subtask) {
        if (subtask != null) {
            int id = generateId();
            subtask.setId(id);
            subtaskHashMap.put(id, subtask);

            int idEpic = subtask.getIdEpic();
            epicHashMap.get(idEpic).addIdSubtask(subtask.getId());
            updateStatus(idEpic);
        }
    }

    public void updateTask(Task task) {
        if (task != null && taskHashMap.containsKey(task.getId())) {
            int id = task.getId();
            Task taskOld = taskHashMap.get(id);
            if (taskOld == null) {
                return;
            }
            taskHashMap.put(id, task);
        }
    }

    public void updateEpic(Epic epic) {
        if (epic != null) {
            int id = epic.getId();
            Epic epicOld = epicHashMap.get(id);
            if (epicOld == null) {
                return;
            }
            epicHashMap.put(id, epic);
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtask != null) {
            subtaskHashMap.put(subtask.getId(), subtask);
            updateStatus(subtask.getIdEpic());
        }
    }

    private void updateStatus(int idEpic) {
        ArrayList<Integer> idSubtaskArrays = epicHashMap.get(idEpic).getIdSubtaskArrays();
        ArrayList<Subtask> subtaskArray = new ArrayList<>();
        for (Integer idSubtask : idSubtaskArrays) {
            subtaskArray.add(subtaskHashMap.get(idSubtask));
        }

        boolean statusDone = true;
        boolean statusNew = true;

        if (idSubtaskArrays.isEmpty()) {
            epicHashMap.get(idEpic).setStatus(Status.NEW);
        } else {
            for (Subtask subtask : subtaskArray) {
                if (!subtask.getStatus().equals(Status.DONE)) {
                    statusDone = false;
                }
                if (!subtask.getStatus().equals(Status.NEW)) {
                    statusNew = false;
                }
                if (!statusNew && !statusDone) {
                    break;
                }
            }

            if (statusNew) {
                epicHashMap.get(idEpic).setStatus(Status.NEW);
            } else if (statusDone) {
                epicHashMap.get(idEpic).setStatus(Status.DONE);
            } else {
                epicHashMap.get(idEpic).setStatus(Status.IN_PROGRESS);
            }
        }
    }

    public Task getTaskById(int id) {
        return epicHashMap.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtaskHashMap.get(id);
    }

    public Epic getEpicById(int id) {
        return epicHashMap.get(id);
    }

    public ArrayList<Task> printAllTask() {
        if (taskHashMap.isEmpty()) {
            System.out.println("Список задач пуст");
        }
        return new ArrayList<>(taskHashMap.values());
    }

    public ArrayList<Subtask> printAllSubtask() {
        if (subtaskHashMap.isEmpty()) {
            System.out.println("Список подзадач пуст");
        }
        return new ArrayList<>(subtaskHashMap.values());
    }

    public ArrayList<Epic> printAllEpic() {
        if (epicHashMap.isEmpty()) {
            System.out.println("Список эпиков пуст");
        }
        return new ArrayList<>(epicHashMap.values());
    }


}
