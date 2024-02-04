package Controller;

import Model.Epic;
import Model.Subtask;
import Model.Task;
import Model.TaskStatus;

import java.util.ArrayList;

public class TaskManager {
    private static long taskId;
    private final ArrayList<Object> listOfAllTasks = new ArrayList<>();

    public static long generateId(){
        return taskId++;
    }

    public Task createNewTask(String title, String description, String status){

        Task task = new Task(title,
                description,
                TaskStatus.valueOf(status));
        addToTasksList(task);
        return task;
    }

    public Epic createNewEpic(String title, String description, String status) {
        Epic epic = new Epic(new Task(title, description,  TaskStatus.valueOf(status)));
        addToTasksList(epic);
        return epic;
    }

    public Subtask createNewSubtask(String title, String description, String status, long epicId){
        Subtask subtask = new Subtask(new Task(title, description,  TaskStatus.valueOf(status)), epicId);
        addToTasksList(subtask);
        return subtask;
    }
    private void addToTasksList(Object obj){
        listOfAllTasks.add(obj);
    }

    public ArrayList<Object> getListOfAllTasks(){
        return listOfAllTasks;
    }
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Object obj: getListOfAllTasks()) {
            if(obj.getClass().equals(Task.class)) {
                tasks.add((Task) obj);
            }
        }
        return tasks;
    }
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> epics = new ArrayList<>();
        for (Object obj: getListOfAllTasks()) {
            if(obj.getClass().equals(Epic.class)) {
                epics.add((Epic) obj);
            }
        }
        return epics;
    }
    public ArrayList<Subtask> getAllSubtask() {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (Object obj: getListOfAllTasks()) {
            if(obj.getClass().equals(Subtask.class)) {
                subtasks.add((Subtask) obj);
            }
        }
        return subtasks;
    }
}
