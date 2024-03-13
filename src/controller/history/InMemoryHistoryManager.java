package controller.history;

import model.Node;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private Node head;
    private Node tail;
    private final HashMap<Long, Node> recentTasks = new HashMap<>();

    public HashMap<Long, Node> getRecentTasks() {
        return recentTasks;
    }

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> historyList = new ArrayList<>();
        Node current = head;
        while (current != null) {
            historyList.add(current.task);
            current = current.next;
        }
        return historyList;
    }

    @Override
    public void add(Task task) {

        if (recentTasks.containsKey(task.getId())) {
            removeNode(recentTasks.get(task.getId()));
        }
        linkLast(task);

    }

    private void linkLast(Task task) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        }
        else {
            oldTail.next = newNode;
        }
        recentTasks.put(task.getId(), newNode);
    }

    @Override
    public void remove(long id) {
        if (recentTasks.containsKey(id)) {
            removeNode(recentTasks.get(id));
        }
    }

    private void removeNode(Node node) {
        final Node next = node.next;
        final Node prev = node.prev;

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }

        recentTasks.remove(node.task.getId());
    }

}
