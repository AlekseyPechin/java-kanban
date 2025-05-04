package model;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> idSubtasks = new ArrayList<>();

    public Epic(String name, String description, Status status, Type type) {
        super(name, description, status, type);
    }

    public ArrayList<Integer> getIdSubtasks() {
        return idSubtasks;
    }

    public void setIdSubtasks(ArrayList<Integer> idSubtasks) {
        this.idSubtasks = idSubtasks;
    }

    public void clearIdSubtaskArrays() {
        idSubtasks.clear();
    }

    public void addIdSubtask(int idSubtask) {
        idSubtasks.add(idSubtask);
    }

    public void removeSubtaskById(int idSubtask) {
        idSubtasks.removeIf(subtask -> subtask == idSubtask);
    }
}
