package main.managers;


import main.exceptions.ManagerSaveException;
import main.model.*;
import main.taskManagerAndHistoryManagerInterfaces.HistoryManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private final File file;
    private static final String HEADER_CSV_FILE = "id,type,name,status,description,startTime,duration,epicId\n";

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public static void loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(new InMemoryHistoryManager(), file);

        fileBackedTaskManager.readFile(file);
    }

    private void readFile(File file) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line = bufferedReader.readLine();
            while (bufferedReader.ready()) {
                line = bufferedReader.readLine();
                if (line.equals("")) {
                    break;
                }

                Task task = fromString(line);

                if (task instanceof Epic epic) {
                    addEpic(epic);
                } else if (task instanceof Subtask subtask) {
                    addSubtask(subtask);
                } else {
                    addTask(task);
                }
            }

            String lineWithHistory = bufferedReader.readLine();
            for (int id : historyFromString(lineWithHistory)) {
                addToHistory(id);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось считать данные из файла.");
        }
    }

    public void save() {
        try {
            if (Files.exists(file.toPath())) {
                Files.delete(file.toPath());
            }
            Files.createFile(file.toPath());
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось найти файл для записи данных");
        }

        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write(HEADER_CSV_FILE);

            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }

            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }

            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toString(subtask) + "\n");
            }

            writer.write("\n");
            writer.write(historyToString(getHistoryManager()));
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить в файл", e);
        }
    }

    private String getParentEpicId(Task task) {
        if (task instanceof Subtask) {
            return Integer.toString(((Subtask) task).getIdEpic());
        }
        return "";
    }

    private Type getType(Task task) {
        if (task instanceof Epic) {
            return Type.EPIC;
        } else if (task instanceof Subtask) {
            return Type.SUBTASK;
        }
        return Type.TASK;
    }

    // Метод сохранения задачи в строку
    private String toString(Task task) {
        String[] toJoin = {Integer.toString(task.getId()), getType(task).toString(), task.getName(),
                task.getStatus().toString(), task.getDescription(), task.getStartTime().format(FORMATTER),
                String.valueOf(task.getDuration()), getParentEpicId(task)};
        return String.join(",", toJoin);
    }

    // Метод создания задачи из строки
    private static Task fromString(String value) {
        String[] params = value.split(",");
        int id = Integer.parseInt(params[0]);
        String type = params[1];
        String name = params[2];
        Status status = Status.valueOf(params[3].toUpperCase());
        String description = params[4];
        LocalDateTime startTime = LocalDateTime.parse(params[5], FORMATTER);
        Duration duration = Duration.parse(params[6]);
        Integer epicId = type.equals("SUBTASK") ? Integer.parseInt(params[7]) : null;

        if (type.equals("EPIC")) {
            Epic epic = new Epic(name, description, status, startTime, duration);
            epic.setId(id);
            epic.setStatus(status);
            return epic;
        } else if (type.equals("SUBTASK")) {
            Subtask subtask = new Subtask(name, description, status, startTime, duration, epicId);
            subtask.setId(id);
            return subtask;
        } else {
            Task task = new Task(description, name, status, startTime, duration);
            task.setId(id);
            return task;
        }
    }

    @Override
    public Task createTask(Task task) {
        super.createTask(task);
        save();
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        super.createEpic(epic);
        save();
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
        return subtask;
    }

    public void addTask(Task task) {
        super.createTask(task);
    }

    public void addEpic(Epic epic) {
        super.createEpic(epic);
    }

    public void addSubtask(Subtask subtask) {
        super.createSubtask(subtask);
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllSubtasksByEpic(Epic epic) {
        super.deleteAllSubtasksByEpic(epic);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    // Метод для сохранения истории в CSV
    static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        StringBuilder str = new StringBuilder();

        if (history.isEmpty()) {
            return "";
        }

        for (Task task : history) {
            str.append(task.getId()).append(",");
        }

        if (str.length() != 0) {
            str.deleteCharAt(str.length() - 1);
        }

        return str.toString();
    }

    // Метод восстановления менеджера истории из CSV
    static List<Integer> historyFromString(String value) {
        List<Integer> toReturn = new ArrayList<>();
        if (value != null) {
            String[] id = value.split(",");

            for (String number : id) {
                toReturn.add(Integer.parseInt(number));
            }

            return toReturn;
        }
        return toReturn;
    }

}