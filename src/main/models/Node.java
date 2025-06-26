package main.models;

public class Node<T> {
    private T task;
    private Node<T> prev;
    private Node<T> next;

    public Node<T> getNext() {
        return next;
    }

    public Node<T> getPrev() {
        return prev;
    }

    public T getTask() {
        return task;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }

    public void setPrev(Node<T> prev) {
        this.prev = prev;
    }

    public void setTask(T task) {
        this.task = task;
    }
}