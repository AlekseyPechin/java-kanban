import main.managers.FileBackedTaskManager;
import main.managers.Managers;
import main.model.Epic;
import main.model.Status;
import main.model.Subtask;
import main.model.Task;

import java.io.File;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {

        FileBackedTaskManager manager = new FileBackedTaskManager(Managers.getDefaultHistory());

        Task firstTask = new Task("Разработать лифт до луны", "Космолифт", Status.NEW);
        manager.addNewTask(firstTask);
        Task secondTask = new Task("Познакомиться", "Tinder", Status.NEW);
        manager.addNewTask(secondTask);

        Epic firstEpic = new Epic("Посадить дерево", "Дерево", Status.NEW);
        manager.addNewEpic(firstEpic);

        Subtask firstSubtask = new Subtask("Купить семена", "Семена", Status.NEW, firstEpic.getId());
        manager.addNewSubtask(firstSubtask);

        manager.getTaskById(firstTask.getId());
        manager.getTaskById(secondTask.getId());
        System.out.println();

        System.out.println("--- Считывание из файла ---");
        Path path2 = Path.of("src/saveFile.CSV");
        File file2 = new File(String.valueOf(path2));
        manager.loadFromFile(new File(String.valueOf(file2)));
        System.out.println("Задачи");
        System.out.println(manager.printAllTask());
        System.out.println("Эпики");
        System.out.println(manager.printAllEpic());
        System.out.println("Подзадачи");
        System.out.println(manager.printAllSubtask());
        System.out.println("История");
        System.out.println(manager.getHistory());

        // sprint 6

//        System.out.println("Поехали!");
//
//        TaskManager manager = getTaskManager();
//
//        System.out.println(manager.printAllTask());
//        System.out.println(manager.printAllEpic());
//        System.out.println(manager.printAllSubtask());
//
//        System.out.println("Последние просмотренные задачи: \n" + manager.getHistory() + "\n");
//        System.out.println("    ------");
//
//        manager.deleteTaskById(1);
//
//        System.out.println("Печать задач после удаления одной задачи");
//        System.out.println(manager.printAllTask());
//        System.out.println(manager.printAllEpic());
//        System.out.println(manager.printAllSubtask());
//        System.out.println("\n    ------");
//
//        manager.deleteEpicById(3);
//        System.out.println("Печать задач после удаления эпика с подзадачами");
//
//        System.out.println(manager.printAllTask());
//        System.out.println(manager.printAllEpic());
//        System.out.println(manager.printAllSubtask());
//        System.out.println("Конец \n");
//    }
//
//    private static TaskManager getTaskManager() {
//        TaskManager manager = Managers.getInMemoryTaskManager(Managers.getDefaultHistory());
//
//        manager.addNewTask(new Task("Task_1", "task 1", Status.NEW));
//        manager.addNewTask(new Task("Task_2", "task 2", Status.NEW));
//
//        manager.addNewEpic(new Epic("Epic_1", "Epic with subtask"));
//        manager.addNewEpic(new Epic("Epic_2", "Epic without subtasks"));
//
//        manager.addNewSubtask(new Subtask("Subtask_1", "subtask_1", Status.NEW, 3));
//        manager.addNewSubtask(new Subtask("Subtask_2", "subtask_2", Status.NEW, 3));
//        manager.addNewSubtask(new Subtask("Subtask_3", "subtask_3", Status.NEW, 3));
//
//        manager.getTaskById(1);
//        manager.getTaskById(2);
//        manager.getEpicById(3);
//        manager.getEpicById(4);
//        manager.getSubtaskById(5);
//        manager.getSubtaskById(6);
//        manager.getTaskById(1);
//        manager.getTaskById(2);
//        manager.getEpicById(3);
//        manager.getEpicById(4);
//        manager.getSubtaskById(5);
//        manager.getSubtaskById(7);
//        manager.getSubtaskById(6);
//        manager.getTaskById(1);
//        manager.getTaskById(2);
//        return manager;
    }
}