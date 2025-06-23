package main.managers;

import main.taskManagerAndHistoryManagerInterfaces.HistoryManager;
import main.taskManagerAndHistoryManagerInterfaces.TaskManager;

public class Managers {

    public static TaskManager getInMemoryTaskManager(HistoryManager historyManager) {
        return new InMemoryTaskManager(historyManager);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}