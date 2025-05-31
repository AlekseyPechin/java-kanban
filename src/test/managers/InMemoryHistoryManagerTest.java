package test.managers;

import main.interfaces.HistoryManager;
import main.interfaces.TaskManager;
import main.managers.InMemoryHistoryManager;
import main.managers.Managers;
import main.model.Epic;
import main.model.Status;
import main.model.Subtask;
import main.model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static main.model.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    InMemoryHistoryManager history;

    @BeforeEach
    public void init() {
        history = new InMemoryHistoryManager();
    }

    @Test
    void add() {
        Task task1 = new Task("Имя 1", "Описание 1",1); // создаём задачу
        Task task2 = new Task("Имя 2", "Описание 2",2); // создаём задачу
        Task task3 = new Task("Имя 1", "Описание 1",1); // создаём задачу
        history.add(task1); // добавляет запрос в историю просмотров
        history.add(task2); // добавляет запрос в историю просмотров
        history.add(task3); // добавляет запрос в историю просмотров
        // проверяем, что при повторном добавлении существующей задачи она перемещается в конец списка.
        assertEquals(task1, history.getHistory().get(1), "Task не одинаковые");
    }

    @Test
    void getHistory() {
        List<Task> historyList = history.getHistory();
        assertEquals(0, historyList.size(), "История должна быть пустой, если она пуста или все задачи удалены");
    }

    @Test
    void remove(){
        Task task1 = new Task("Имя 1", "Описание 1",1);
        Task task2 = new Task("Имя 2", "Описание 2",2);
        Task task3 = new Task("Имя 3", "Описание 3",3);
        Task task4 = new Task("Имя 4", "Описание 4",4);
        history.add(task1);
        history.add(task2);
        history.add(task3);
        history.add(task4);


        history.remove(2);
        assertEquals(3, history.getHistory().size(), "размер Истории не соответствует ожидаемому");

        history.remove(1);
        assertEquals(2, history.getHistory().size(), "размер Истории не соответствует ожидаемому");

        history.remove(4);
        assertEquals(1, history.getHistory().size(), "размер Истории не соответствует ожидаемому");

        history.remove(3);
        assertEquals(0, history.getHistory().size(), "размер Истории не соответствует ожидаемому");

        history.remove(2);
        assertEquals(0, history.getHistory().size(), "размер Истории не соответствует ожидаемому");
    }

    @Test
    void add_comparNumberOfTaskInList() {
        int addNumberOfTask = 30;
        for (int i = 1; i <= addNumberOfTask; i++) {
            history.add(new Task("Название задачи: " + i, "Описание задачи: " + i, i));
        }
        assertEquals(addNumberOfTask, history.getHistory().size(),
                "Не совпадает ограничение и размер таблицы");
        assertEquals("Название задачи: " + addNumberOfTask, history.getHistory().get(addNumberOfTask - 1).getName(),
                "Не совпадают последние задачи");
    }

    @Test
    void delete_comparNumberOfTaskInList() {
        int addNumberOfTask = 30;
        for (int i = 1; i <= addNumberOfTask; i++) {
            history.add(new Task("Название задачи: " + i, "Описание задачи: " + i, i));
        }
        int deleteNumberOfTask = 10;
        for (int i = 1; i <= deleteNumberOfTask; i++) {
            history.remove(i);
        }
        assertEquals(addNumberOfTask - deleteNumberOfTask, history.getHistory().size(),
                "Не совпадает ограничение и размер таблицы");
    }

}