package main.managers;

import main.interfaces.HistoryManager;
import main.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    protected static List<Task> historyTask = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (historyTask.size() == 10) {
            historyTask.removeFirst();
        }
        historyTask.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return historyTask;
    }

}
