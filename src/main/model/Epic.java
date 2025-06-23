package main.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Epic extends Task {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private ArrayList<Integer> idSubtasks = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, int id) {
        super(name, description, id);
    }

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public Epic(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        this.endTime = super.getEndTime();
    }

    public Epic(String name, String description, int id, Status status, Type type) {
        super(name, description, id, status, type);
    }

    public ArrayList<Integer> getIdSubtasks() {
        return idSubtasks;
    }

    public void setIdSubtasks(ArrayList<Integer> idSubtasks) {
        this.idSubtasks = idSubtasks;
    }

    public void clearIdSubtasks() {
        idSubtasks.clear();
    }

    public void addIdSubtask(int idSubtask) {
        idSubtasks.add(idSubtask);
    }

    public void removeSubtaskById(int idSubtask) {
        idSubtasks.removeIf(subtask -> subtask == idSubtask);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                ". name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", startTime=" + getStartTime().format(FORMATTER) +
                ", duration=" + getDuration().toMinutes() +
                ", status=" + getStatus() +
                ", подзадачи: " + getIdSubtasks().size() + " \n";
    }
}
