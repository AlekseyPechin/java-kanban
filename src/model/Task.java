package model;

import java.util.Objects;

public class Task {

    private String name;
    private String description;
    private int id;
    private Status status;
    private Type type;

    public Task(String name, String description, Status status, Type type) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "название='" + name + '\'' +
                ", описание='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return id == task.id;

    }

    @Override
    public int hashCode() {
        int hash = 17;
        if (name != null && description != null) {
            hash = name.hashCode() + description.hashCode();
        }
        hash = hash * 31;
        if (type != null) {
            hash = hash + type.hashCode();
        }
        return hash;
    }
}
