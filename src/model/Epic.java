package model;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> idSubtaskArrays = new ArrayList<>();

    public Epic(String name, String description, Status status, Type type) {
        super(name, description, status, type);
    }

    public ArrayList<Integer> getIdSubtaskArrays() {
        return idSubtaskArrays;
    }

    public void setIdSubtaskArrays(ArrayList<Integer> idSubtaskArrays) {
        this.idSubtaskArrays = idSubtaskArrays;
    }

    public void clearIdSubtaskArrays() {
        idSubtaskArrays.clear();
    }

    public void addIdSubtask(int idSubtask) {
        idSubtaskArrays.add(idSubtask);
    }

    public void removeSubtaskById(int idSubtask) {
        idSubtaskArrays.remove(idSubtask);
    }
}
