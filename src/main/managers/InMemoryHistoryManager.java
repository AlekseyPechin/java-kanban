package main.managers;

import main.interfaces.HistoryManager;
import main.model.Node;
import main.model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    protected HashMap<Integer, Node<Task>> nodeMap = new HashMap<>();
    protected Node<Task> tail;

    @Override
    public void add(Task task) {
        if (nodeMap.containsKey(task.getId())) {
            remove(task.getId());
            nodeMap.remove(task.getId());
        }
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        ArrayList<Task> listHistory = new ArrayList<>(nodeMap.size());
        Node<Task> temp = tail;
        while (temp != null) {
            listHistory.add(temp.task);
            temp = temp.prev;
        }
        Collections.reverse(listHistory);
        return listHistory;
    }

    @Override
    public void remove(int id) {
        if (nodeMap.containsKey(id)) {
            Node<Task> nodeId = nodeMap.get(id);
            removeNode(nodeId);
            nodeMap.remove(nodeId);
        }
    }

    private void linkLast(Task task) {
        final Node<Task> newNode = new Node<>(null, task, null);
        if (tail != null) {
            tail.next = newNode;
            newNode.prev = tail;
        }
        tail = newNode;
        nodeMap.put(task.getId(), tail);
    }

    private void removeNode(Node<Task> node) {
        if (node.prev == null && node.next == null) {
            tail = null; // Удаляем единственный оставшийся узел
        } else if (node.prev == null) { // Удаляем первый элемент
            node.next.prev = null;
        } else if (node.next == null) { // Удаляем последний элемент
            node.prev.next = null;
            tail = node.prev; // Теперь этот узел становится последним
        } else { // Удаляем средний элемент
            node.next.prev = node.prev;
            node.prev.next = node.next;
        }
    }

}
