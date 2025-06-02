package main.managers;

import main.interfacesForTaskProcessingAndOutput.HistoryManager;
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
            listHistory.add(temp.getTask());
            temp = temp.getPrev();
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
            tail.setNext(newNode);
            newNode.setPrev(tail);
        }
        tail = newNode;
        nodeMap.put(task.getId(), tail);
    }

    private void removeNode(Node<Task> node) {
        if (node.getPrev() == null && node.getNext() == null) {
            tail = null; // Удаляем единственный оставшийся узел
        } else if (node.getPrev() == null) { // Удаляем первый элемент
            node.getNext().setPrev(null);
        } else if (node.getNext() == null) { // Удаляем последний элемент
            node.getPrev().setNext(null);
            tail = node.getPrev(); // Теперь этот узел становится последним
        } else { // Удаляем средний элемент
            node.getNext().setPrev(node.getPrev());
            node.getPrev().setNext(node.getNext());
        }
    }

}