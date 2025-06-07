package main.managers;

import main.interfacesForTaskProcessingAndOutput.HistoryManager;
import main.interfacesForTaskProcessingAndOutput.TaskManager;
import main.model.Epic;
import main.model.Status;
import main.model.Subtask;
import main.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private int id;

    protected HashMap<Integer, Task> taskHashMap = new HashMap<>();
    protected HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();
    protected HashMap<Integer, Epic> epicHashMap = new HashMap<>();

    protected HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
        this.id = 0;
    }

    private Integer generateId() {
        return ++id;
    }

    public int addNewTask(Task task) {
        if (task != null) {
            int id = generateId();
            task.setId(id);
            taskHashMap.put(id, task);
            return id;
        }
        return 0;
    }

    public int addNewEpic(Epic epic) {
        if (epic != null) {
            int id = generateId();
            epic.setId(id);
            epicHashMap.put(id, epic);
            return id;
        }
        return 0;
    }

    public int addNewSubtask(Subtask subtask) {
        if (subtask != null) {
            int id = generateId();
            subtask.setId(id);
            subtaskHashMap.put(id, subtask);

            int idEpic = subtask.getIdEpic();
            epicHashMap.get(idEpic).addIdSubtask(subtask.getId());
            updateStatus(idEpic);
            return id;
        }
        return 0;
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
        ArrayList<Integer> idSubtaskArrays = epicHashMap.get(idEpic).getIdSubtasks();
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
        historyManager.add(taskHashMap.get(id));
        return taskHashMap.get(id);
    }

    public Subtask getSubtaskById(int id) {
        historyManager.add(subtaskHashMap.get(id));
        return subtaskHashMap.get(id);
    }

    public Epic getEpicById(int id) {
        historyManager.add(epicHashMap.get(id));
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

    public void deleteTaskById(int id) {
        taskHashMap.remove(id);
    }

    public void deleteEpicById(int id) {
        for (Integer idSubtask : epicHashMap.get(id).getIdSubtasks()) {
            subtaskHashMap.remove(idSubtask);
        }
        epicHashMap.remove(id);
    }

    public void deleteSubtaskById(int id) {
        int idEpic = subtaskHashMap.get(id).getIdEpic();
        epicHashMap.get(idEpic).removeSubtaskById(id);
        subtaskHashMap.remove(id);
    }

    public void clearTaskArrays() {
        taskHashMap.clear();
    }

    public void clearEpicArrays() {
        epicHashMap.clear();
        subtaskHashMap.clear();
    }

    public void clearSubtaskArrays() {
        subtaskHashMap.clear();
        for (Epic epic : epicHashMap.values()) {
            epic.clearIdSubtaskArrays();
            epic.setStatus(Status.NEW);
        }
    }

    public void deleteAll() {
        taskHashMap.clear();
        subtaskHashMap.clear();
        epicHashMap.clear();
    }

    public List<Task> getHistory() {
        List<Task> histories = historyManager.getHistory();
        if (histories.isEmpty()) {
            System.out.println("История просмотров пуста!");
        }
        return histories;
    }
}