package model;

public class Subtask extends Task {

    private int idEpic;

    public Subtask(String name, String description, Status status, Type type, int idEpic) {
        super(name, description, status, type);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }

    public void setIdEpic(int idEpic) {
        this.idEpic = idEpic;
    }
}
