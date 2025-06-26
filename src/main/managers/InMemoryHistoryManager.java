package main.managers;

import main.models.Node;
import main.taskManagerAndHistoryManagerInterfaces.HistoryManager;
import main.models.Task;

import java.util.*;


public class InMemoryHistoryManager implements HistoryManager {

    private static class CustomLinkedList {
        private final Map<Integer, Node<Task>> table = new HashMap<>();
        private Node<Task> head;
        private Node<Task> tail;

        private void linkLast(Task task) {
            Node<Task> element = new Node();
            element.setTask(task);

            if (table.containsKey(Optional.of(task).get().getId())) {
                removeNode(table.get(Optional.of(task).get().getId()));
            }

            if (head == null) {
                tail = element;
                head = element;
                element.setNext(null);
                element.setPrev(null);
            } else {
                element.setPrev(tail);
                element.setNext(null);
                tail.setNext(element);
                tail = element;
            }

            table.put(task.getId(), element);
        }

        private List<Task> getTask() {
            List<Task> result = new ArrayList<>();
            Node<Task> element = head;
            while (element != null) {
                result.add(element.getTask());
                element = element.getNext();
            }
            return result;
        }

        private void removeNode(Node<Task> node) {
            if (node != null) {
                table.remove(node.getTask().getId());
                Node<Task> prev = node.getPrev();
                Node<Task> next = node.getNext();

                if (head == node) {
                    head = node.getNext();
                }
                if (tail == node) {
                    tail = node.getPrev();
                }

                if (prev != null) {
                    prev.setNext(next);
                }
                if (next != null) {
                    next.setPrev(prev);
                }
            }
        }

        private Node<Task> getNode(int id) {
            return table.get(id);
        }
    }

    private final CustomLinkedList list = new CustomLinkedList();

    // Добавление нового просмотра задачи в историю
    @Override
    public void add(Task task) {
        list.linkLast(Optional.of(task).get());
    }

    // Получение истории просмотров
    @Override
    public List<Task> getHistory() {
        return list.getTask();
    }

    // Удаление просмотра из истории
    @Override
    public void remove(int id) {
        list.removeNode(list.getNode(id));
    }
}