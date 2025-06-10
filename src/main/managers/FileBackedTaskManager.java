package main.managers;

import main.exceptions.ManagerSaveException;
import main.taskManagerAndHistoryManagerInterfaces.HistoryManager;
import main.model.Epic;
import main.model.Subtask;
import main.model.Task;
import main.model.Status;
import main.model.Type;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final String HEADER_CSV_FILE = "id,type,name,status,description,startTime,duration,epicId\n";
    private File file = new File("src/saveFile.CSV");

    public FileBackedTaskManager(HistoryManager historyManager) {
        super(historyManager);
    }

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
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

            for (Task task : printAllTask()) {
                writer.write(toString(task) + "\n");
            }

            for (Epic epic : printAllEpic()) {
                writer.write(toString(epic) + "\n");
            }

            for (Subtask subtask : printAllSubtask()) {
                writer.write(toString(subtask) + "\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить в файл", e);
        }
    }

    public static Task fromString(String value) {
        String[] taskFromString = value.split(",");

        int id = Integer.parseInt(taskFromString[0]);
        Type type = Type.valueOf(taskFromString[1].toUpperCase());
        String name = taskFromString[2];
        Status status = Status.valueOf(taskFromString[3]);
        String description = taskFromString[4];

        int idEpic = -1;
        if (type.equals(Type.SUBTASK)) {
            idEpic = Integer.parseInt(taskFromString[5]);
        }

        if (type.equals(Type.EPIC)) {
            Epic epic = new Epic(name, description);
            epic.setId(id);
            epic.setStatus(status);
            return epic;
        } else if (type.equals(Type.SUBTASK)) {
            Subtask subtask = new Subtask(name, description, status, idEpic);
            subtask.setId(id);
            return subtask;
        } else {
            Task task = new Task(name, description, status);
            task.setId(id);
            return task;
        }
    }

    private String toString(Task task) {
        String name = task.getName();
        String description = task.getDescription();
        String id = String.valueOf(task.getId());
        String status = String.valueOf(task.getStatus());
        Type type = getType(task);
        String idEpic = getParentEpicId(task);

        return String.join(",", id, type.toString(), name, status, description, idEpic);
    }

    public static void loadFromFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {

            String line = reader.readLine();
            while (reader.ready()) {
                line = reader.readLine();
                if (line.equals("")) {
                    return;
                }

                Task task = fromString(line);
                if (task instanceof Epic epic) {
                    getEpics().put(epic.getId(), epic);
                } else if (task instanceof Subtask subtask) {
                    getSubtasks().put(subtask.getId(), subtask);
                } else {
                    getTasks().put(task.getId(), task);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Type getType(Task task) {
        if (task instanceof Epic) {
            return Type.EPIC;
        } else if (task instanceof Subtask) {
            return Type.SUBTASK;
        }
        return Type.TASK;
    }

    private String getParentEpicId(Task task) {
        if (task instanceof Subtask) {
            return Integer.toString(((Subtask) task).getIdEpic());
        }
        return "";
    }

    @Override
    public Task addNewTask(Task task) {
        super.addNewTask(task);
        save();
        return task;
    }

    @Override
    public Epic addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        save();
        return epic;
    }

    @Override
    public Subtask addNewSubtask(Subtask subtask) {
        super.addNewSubtask(subtask);
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

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
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
    public void clearTaskArrays() {
        super.clearTaskArrays();
        save();
    }

    @Override
    public void clearEpicArrays() {
        super.clearEpicArrays();
        save();
    }

    @Override
    public void clearSubtaskArrays() {
        super.clearSubtaskArrays();
        save();
    }

    @Override
    public void deleteAll() {
        super.deleteAll();
        save();
    }

    @Override
    public ArrayList<Task> printAllTask() {
        return super.printAllTask();
    }

    @Override
    public ArrayList<Subtask> printAllSubtask() {
        return super.printAllSubtask();
    }

    @Override
    public ArrayList<Epic> printAllEpic() {
        return super.printAllEpic();
    }
}
