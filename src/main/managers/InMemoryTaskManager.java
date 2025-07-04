package main.managers;


import main.exceptions.ManagerValidateException;
import main.models.Epic;
import main.models.Status;
import main.models.Subtask;
import main.models.Task;
import main.taskManagerAndHistoryManagerInterfaces.HistoryManager;
import main.taskManagerAndHistoryManagerInterfaces.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.Set;

public class InMemoryTaskManager implements TaskManager {
    private static int id = 0;

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager;
    private final Comparator<Task> taskComparator = Comparator.comparing(Task::getStartTime);
    protected Set<Task> prioritizedTasks = new TreeSet<>(taskComparator);

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public int generateId() {
        return ++id;
    }

    @Override
    public Task createTask(Task task) {
        if (task == null) return null;
        int newTaskId = generateId();
        task.setId(newTaskId);
        addNewPrioritizedTask(task);
        tasks.put(newTaskId, task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        if (epic == null) return null;
        int newEpicId = generateId();
        epic.setId(newEpicId);
        epics.put(newEpicId, epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (subtask == null) return null;
        int newSubtaskId = generateId();
        subtask.setId(newSubtaskId);
        Epic epic = epics.get(subtask.getIdEpic());
        if (epic != null) {
            addNewPrioritizedTask(subtask);
            subtasks.put(newSubtaskId, subtask);
            epic.addIdSubtask(newSubtaskId);
            updateStatus(epic);
            updateTimeEpic(epic);
            return subtask;
        } else {
            System.out.println("Epic not found");
            return null;
        }
    }

    @Override
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            prioritizedTasks.removeIf(task -> task.getId() == id);
            tasks.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Task not found");
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            epic.getIdSubtasks().forEach(subtaskId -> {
                prioritizedTasks.removeIf(task -> Objects.equals(task.getId(), subtaskId));
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            });
            epics.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Epic not found");
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getIdEpic());
            epic.getIdSubtasks().remove((Integer) subtask.getId());
            updateStatus(epic);
            updateTimeEpic(epic);
            prioritizedTasks.remove(subtask);
            subtasks.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Subtask not found");
        }
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        prioritizedTasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            for (int subtaskId : epic.getIdSubtasks()) {
                Subtask subtask = subtasks.get(subtaskId);
                prioritizedTasks.remove(subtask);
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            epic.getIdSubtasks().clear();
        }
    }

    @Override
    public void deleteAllSubtasksByEpic(Epic epic) {
        if (epic != null) {
            for (int subtaskId : epic.getIdSubtasks()) {
                Subtask subtask = subtasks.get(subtaskId);
                prioritizedTasks.remove(subtask);
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            epic.getIdSubtasks().clear();
        }
    }

    @Override
    public Optional<Task> getTaskById(Integer id) {
        var taskOpt = Optional.ofNullable(tasks.get(id));
        if (taskOpt.isPresent()) {
            historyManager.add(taskOpt.get());
            return taskOpt;
        }
        return null;
    }

    @Override
    public Optional<Epic> getEpicById(Integer id) {
        var epicOpt = Optional.ofNullable(epics.get(id));
        if (epicOpt.isPresent()) {
            historyManager.add(epicOpt.get());
            return epicOpt;
        }
        return null;
    }

    @Override
    public Optional<Subtask> getSubtaskById(Integer id) {
        var subtaskOpt = Optional.ofNullable(subtasks.get(id));
        if (subtaskOpt.isPresent()) {
            historyManager.add(subtaskOpt.get());
            return subtaskOpt;
        }
        return null;
    }

    @Override
    public List<Task> getAllTasks() {
        if (tasks.size() == 0) {
            System.out.println("Task list is empty");
            return Collections.emptyList();
        }
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        if (epics.size() == 0) {
            System.out.println("Epic list is empty");
            return Collections.emptyList();
        }
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        if (subtasks.size() == 0) {
            System.out.println("Subtasks list is empty");
            return Collections.emptyList();
        }
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasksByEpicId(Integer id) {
        if (epics.containsKey(id)) {
            List<Subtask> subtasksNew = new ArrayList<>();
            Epic epic = epics.get(id);
            for (int i = 0; i < epic.getIdSubtasks().size(); i++) {
                subtasksNew.add(subtasks.get(epic.getIdSubtasks().get(i)));
            }
            return subtasksNew;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<Task> updateTask(Task task) {
        if (task == null) {
            System.out.println("Task not found");
            return Optional.empty();
        }
        addNewPrioritizedTask(task);
        tasks.put(task.getId(), task);
        return Optional.of(task);

    }

    @Override
    public Optional<Epic> updateEpic(Epic epic) {
        if (epic == null) {
            System.out.println("Epic not found");
            return Optional.empty();
        }
        epics.put(epic.getId(), epic);
        updateStatus(epic);
        updateTimeEpic(epic);
        return Optional.of(epic);
    }

    @Override
    public void updateStatus(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            if (epic.getIdSubtasks().size() == 0) {
                epic.setStatus(Status.NEW);
            } else {
                List<Subtask> subtasksNew = new ArrayList<>();
                int countDone = 0;
                int countNew = 0;

                for (int i = 0; i < epic.getIdSubtasks().size(); i++) {
                    subtasksNew.add(subtasks.get(epic.getIdSubtasks().get(i)));
                }

                for (Subtask subtask : subtasksNew) {
                    if (subtask.getStatus() == Status.DONE) {
                        countDone++;
                    }
                    if (subtask.getStatus() == Status.NEW) {
                        countNew++;
                    }
                    if (subtask.getStatus() == Status.IN_PROGRESS) {
                        epic.setStatus(Status.IN_PROGRESS);
                        return;
                    }
                }

                if (countDone == epic.getIdSubtasks().size()) {
                    epic.setStatus(Status.DONE);
                } else if (countNew == epic.getIdSubtasks().size()) {
                    epic.setStatus(Status.NEW);
                } else {
                    epic.setStatus(Status.IN_PROGRESS);
                }
            }
        } else {
            System.out.println("Epic not found");
        }
    }

    public void updateTimeEpic(Epic epic) {
        List<Subtask> subtasks = getAllSubtasksByEpicId(epic.getId());
        LocalDateTime startTime = subtasks.get(0).getStartTime();
        LocalDateTime endTime = subtasks.get(0).getEndTime();

        for (Subtask subtask : subtasks) {
            if (subtask.getStartTime().isBefore(startTime)) startTime = subtask.getStartTime();
            if (subtask.getEndTime().isAfter(endTime)) endTime = subtask.getEndTime();
        }

        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
        Duration duration = Duration.between(startTime, endTime);
        epic.setDuration(duration);
    }

    @Override
    public Optional<Subtask> updateSubtask(Subtask subtask) {
        if (subtask == null) {
            System.out.println("Subtask not found");
            return Optional.empty();
        }
        addNewPrioritizedTask(subtask);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getIdEpic());
        updateStatus(epic);
        updateTimeEpic(epic);
        return Optional.of(subtask);
    }

    @Override
    public void printTasks() {
        if (tasks.size() == 0) {
            System.out.println("Task list is empty");
            return;
        }
        for (Task task : tasks.values()) {
            System.out.println("Task{" +
                    "description='" + task.getDescription() + '\'' +
                    ", id=" + task.getId() +
                    ", name='" + task.getName() + '\'' +
                    ", status=" + task.getStatus() +
                    '}');
        }
    }

    @Override
    public void printEpics() {
        if (epics.size() == 0) {
            System.out.println("Epic list is empty");
            return;
        }
        for (Epic epic : epics.values()) {
            System.out.println("Epic{" +
                    "subtasksIds=" + epic.getIdSubtasks() +
                    ", description='" + epic.getDescription() + '\'' +
                    ", id=" + epic.getId() +
                    ", name='" + epic.getName() + '\'' +
                    ", status=" + epic.getStatus() +
                    '}');
        }
    }

    @Override
    public void printSubtasks() {
        if (subtasks.size() == 0) {
            System.out.println("Subtask list is empty");
            return;
        }
        for (Subtask subtask : subtasks.values()) {
            System.out.println("Subtask{" +
                    "epicId=" + subtask.getIdEpic() +
                    ", description='" + subtask.getDescription() + '\'' +
                    ", id=" + subtask.getId() +
                    ", name='" + subtask.getName() + '\'' +
                    ", status=" + subtask.getStatus() +
                    '}');
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public void addToHistory(int id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
        } else if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
        } else if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
        }
    }

    @Override
    public void remove(int id) {
        historyManager.remove(id);
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    private void addNewPrioritizedTask(Task task) {
        prioritizedTasks.add(task);
        validateTaskPriority();
    }

    public boolean isTasksOverlap(Task task) {
        List<Task> tasks = List.copyOf(prioritizedTasks);
        int sizeTimeNull = 0;
        if (tasks.size() > 0) {
            for (Task taskSave : tasks) {
                if (taskSave.getStartTime() != null && taskSave.getEndTime() != null) {
                    if (task.getStartTime().isBefore(taskSave.getStartTime())
                            && task.getEndTime().isBefore(taskSave.getStartTime())) {
                        return false;
                    } else if (task.getStartTime().isAfter(taskSave.getEndTime())
                            && task.getEndTime().isAfter(taskSave.getEndTime())) {
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    sizeTimeNull++;
                }

            }
            return sizeTimeNull == tasks.size();
        } else {
            return false;
        }
    }

    private void validateTaskPriority() {
        List<Task> tasks = getPrioritizedTasks();

        for (int i = 1; i < tasks.size(); i++) {
            Task task = tasks.get(i);

            boolean taskHasIntersections = isTasksOverlap(task);

            if (!taskHasIntersections) {
                throw new ManagerValidateException(
                        "Задачи # " + task + " и # " + tasks.get(i - 1) + " пересекаются");
            }
        }
    }

    private List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }
}
