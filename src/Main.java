import main.interfacesForTaskProcessingAndOutput.TaskManager;
import main.managers.Managers;
import main.model.Epic;
import main.model.Status;
import main.model.Subtask;
import main.model.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager manager = getTaskManager();

        System.out.println(manager.printAllTask());
        System.out.println(manager.printAllEpic());
        System.out.println(manager.printAllSubtask());

        System.out.println("Последние просмотренные задачи: \n" + manager.getHistory() + "\n");
        System.out.println("    ------");

        manager.deleteTaskById(1);

        System.out.println("Печать задач после удаления одной задачи");
        System.out.println(manager.printAllTask());
        System.out.println(manager.printAllEpic());
        System.out.println(manager.printAllSubtask());
        System.out.println("\n    ------");

        manager.deleteEpicById(3);
        System.out.println("Печать задач после удаления эпика с подзадачами");

        System.out.println(manager.printAllTask());
        System.out.println(manager.printAllEpic());
        System.out.println(manager.printAllSubtask());
        System.out.println("Конец \n");
    }

    private static TaskManager getTaskManager() {
        TaskManager manager = Managers.getDefault();

        Task task_1 = new Task("Task_1", "task 1", Status.NEW);
        Task task_2 = new Task("Task_2", "task 2", Status.NEW);
        int id_task_1 = manager.addNewTask(task_1);
        int id_task_2 = manager.addNewTask(task_2);

        Epic epicWithSubtasks = new Epic("Epic_1", "Epic with subtask");
        Epic epicWithoutSubtasks = new Epic("Epic_2", "Epic without subtasks");
        int id_epic_1 = manager.addNewEpic(epicWithSubtasks);
        int id_epic_2 = manager.addNewEpic(epicWithoutSubtasks);

        Subtask subtask_1 = new Subtask("Subtask_1", "subtask_1", Status.NEW, id_epic_1);
        Subtask subtask_2 = new Subtask("Subtask_2", "subtask_2", Status.NEW, id_epic_1);
        Subtask subtask_3 = new Subtask("Subtask_3", "subtask_3", Status.NEW, id_epic_1);
        manager.addNewSubtask(subtask_1);
        manager.addNewSubtask(subtask_2);
        manager.addNewSubtask(subtask_3);

        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getEpicById(3);
        manager.getEpicById(4);
        manager.getSubtaskById(5);
        manager.getSubtaskById(6);
        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getEpicById(3);
        manager.getEpicById(4);
        manager.getSubtaskById(5);
        manager.getSubtaskById(7);
        manager.getSubtaskById(6);
        manager.getTaskById(1);
        manager.getTaskById(2);
        return manager;
    }
}
