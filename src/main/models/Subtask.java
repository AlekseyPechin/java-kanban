package main.models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Subtask extends Task {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private int idEpic;

    public Subtask(String name, String description, Status status, Integer idEpic) {
        super(name, description, status);
        this.idEpic = idEpic;
    }

    public Subtask(String name, String description, Status status, LocalDateTime startTime, Duration duration, Integer idEpic) {
        super(name, description, status, startTime, duration);
        this.idEpic = idEpic;
    }

    public Subtask(String name, String description, Integer idEpic) {
        super(name, description);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }

    public void setIdEpic(Integer idEpic) {
        this.idEpic = idEpic;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                ". name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", startTime=" + getStartTime().format(FORMATTER) +
                ", duration=" + getDuration().toMinutes() +
                ", endTime=" + getEndTime().format(FORMATTER) +
                ", epic subtask: " + getIdEpic() + " \n";
    }
}