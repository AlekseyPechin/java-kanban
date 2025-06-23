package main.managers;

import main.taskManagerAndHistoryManagerInterfaces.HistoryManager;
import main.taskManagerAndHistoryManagerInterfaces.TaskManager;
import main.model.Epic;
import main.model.Subtask;
import main.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static main.model.Status.*;

public class InMemoryTaskManager implements TaskManager {

    private final HistoryManager historyManager;
    private static final Map<Integer, Task> tasks = new HashMap<>();
    private static final Map<Integer, Epic> epics = new HashMap<>();
    private static final Map<Integer, Subtask> subtasks = new HashMap<>();

    private static int id = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public int generateId() {
        return ++id;
    }

    public static Map<Integer, Task> getTasks() {
        return tasks;
    }

    public static Map<Integer, Epic> getEpics() {
        return epics;
    }

    public static Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public Task addNewTask(Task task) { // создаем новый Task
        if (task != null) {
            task.setId(generateId()); // присваиваем id Task(у)
            tasks.put(task.getId(), task); // добавляем Task в список
            return task;
        }
        return null;
    }

    @Override
    public Epic addNewEpic(Epic epic) { // создаем новый Epic
        if (epic != null) {
            epic.setId(generateId());  // присваиваем id Epic(у)
            epics.put(epic.getId(), epic); // добавляем Epic в список
            return epic;
        }
        return null;
    }

    @Override
    public Subtask addNewSubtask(Subtask subtask) { // создаем новый Subtask
        if (subtask != null) {
            subtask.setId(generateId()); // присваиваем id Subtask(у)
            subtasks.put(subtask.getId(), subtask); // добавляем Subtask в список

            Epic epic = epics.get(subtask.getIdEpic());
            epic.addIdSubtask(subtask.getId());
            updateStatus(epic);
        }
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getId())) { // проверяем, что Task не пустой и он есть в списке
            Task oldTask = tasks.get(task.getId()); // находим старый Task
            if (oldTask == null) { // проверяем, что он не пустой
                return;
            }
            tasks.put(task.getId(), task); // заменяем Task в списке
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null && epics.containsKey(epic.getId())) { // проверяем, что Epic не пустой и он есть в списке
            Epic oldEpic = epics.get(epic.getId()); // находим старый Epic
            if (oldEpic == null) { // проверяем, что он не пустой
                return;
            }
            epics.put(epic.getId(), epic); // заменяем Epic в списке
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null) { // проверяем, что Subtask не пустой и он есть в списке
            subtasks.put(subtask.getId(), subtask); // добавляем Subtask в список

            Epic epic = epics.get(subtask.getIdEpic()); // находим Epic, которому принадлежит Subtask
            updateStatus(epic); // обновляем статус Epic
        }
    }

    private void updateStatus(Epic epic) { //обновление статуса Epic(а)
        int idEpic = epic.getId();
        ArrayList<Integer> idSubtasks = epics.get(idEpic).getIdSubtasks(); // собираем в список id Subtask(ов) по Epic(у)
        ArrayList<Subtask> subtaskList = new ArrayList<>(); // создаем список Subtask(ов)
        for (Integer idubtask : idSubtasks) {
            subtaskList.add(subtasks.get(idubtask)); // добавляем в него Subtask(и)
        }

        boolean statusDone = true;
        boolean statusNew = true;

        if (idSubtasks.isEmpty()) { // если список id Subtask(ов) пустой
            epics.get(idEpic).setStatus(NEW); // то, присваиваем Epic(у) статус "NEW"
        } else {
            for (Subtask subtask : subtaskList) { // перебираем список Subtask(ов)
                if (!subtask.getStatus().equals(DONE)) { // проверяем, что статус Subtask(а) "DONE"
                    statusDone = false;
                }
                if (!subtask.getStatus().equals(NEW)) { // проверяем, что статус Subtask(а) "NEW"
                    statusNew = false;
                }
                if (!statusNew && !statusDone) {
                    break;
                }
            }

            if (statusNew) { // если статус "NEW"
                epics.get(idEpic).setStatus(NEW); // то, присваиваем Epic(у) статус "NEW"
            } else if (statusDone) { // если статус "DONE"
                epics.get(idEpic).setStatus(DONE); // то, присваиваем Epic(у) статус "DONE"
            } else {
                epics.get(idEpic).setStatus(IN_PROGRESS); // иначе присваиваем Epic(у) статус "IN_PROGRESS"
            }
        }
    }

    @Override
    public Task getTaskById(int id) { // вывод Task(а) по id
        historyManager.add(tasks.get(id)); // добавляем в Task историю просмотров
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) { // вывод Subtask(а) по id
        historyManager.add(subtasks.get(id)); // добавляем в Subtask историю просмотров
        return subtasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) { // вывод Epic(а) по id
        historyManager.add(epics.get(id)); // добавляем в Epic историю просмотров
        return epics.get(id);
    }

    @Override
    public ArrayList<Task> printAllTask() { // вывод всех Task(ов)
        if (tasks.isEmpty()) { // проверяем, что список не пуст
            System.out.println("Список Task(ов) пуст");
        }
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> printAllSubtask() { // вывод всех Subtask(ов)
        if (subtasks.isEmpty()) {
            System.out.println("Список Subtask(ов) пуст");
        }
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Epic> printAllEpic() { // вывод всех Epic(ов)
        if (epics.isEmpty()) {
            System.out.println("Список Epic(ов) пуст");
        }
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteTaskById(int id) { // удаление Task(а) по id
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) { // удаление Epic(а) по id
        for (Integer idSubtask : epics.get(id).getIdSubtasks()) { // удаление всех Subtask(ов) по Epic(у)
            subtasks.remove(idSubtask);
        }
        epics.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) { // удаление Subtask(а) по id
        int idEpic = subtasks.get(id).getIdEpic();
        epics.get(idEpic).removeSubtaskById(id);  // удаление Subtask(а) из Epic(а)
        subtasks.remove(id);
    }

    @Override
    public void clearTaskArrays() { // удаление всех Task(ов)
        tasks.clear();
    }

    @Override
    public void clearEpicArrays() { // удаление всех Epic(ов)
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void clearSubtaskArrays() { // удаление всех Subtask(ов)
        subtasks.clear();
        for (Epic epic : epics.values()) { // перебор всех Epic(ов)
            epic.clearIdSubtasks(); // удаление всех Subtask(ов) из Epic(а)
            epic.setStatus(NEW); // присвоение Epic(у) статус NEW
        }
    }

    @Override
    public void deleteAll() { // удаление всех задач
        tasks.clear();
        subtasks.clear();
        epics.clear();

    }

    @Override
    public List<Task> getHistory() {
        List<Task> histories = historyManager.getHistory();
        if (histories.isEmpty()) {
            System.out.println("История просмотров пуста!");
        }
        return histories;
    }
}