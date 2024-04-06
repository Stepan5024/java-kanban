package controller.managers;

import controller.history.HistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.*;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final String filePath;

    public FileBackedTaskManager(HistoryManager historyManager, String filePath) {
        super(historyManager);
        this.filePath = filePath;
    }

    public void save() {
        try (PrintWriter writer = new PrintWriter(filePath)) {
            writer.println("id,type,name,status,description,epic,startTime,duration");
            for (Object task : getListOfAllEntities()) {
                if (task instanceof Task) {
                    writer.println(taskToString((Task) task));
                }
            }
            writer.println();
            writer.println(historyToString(getHistoryManager()));
        } catch (FileNotFoundException e) {
            throw new ManagerSaveException("Ошибка при попытке сохранить данные в файл: файл не найден.", e);
        }
    }

    private String taskToString(Task task) {
        String type = task instanceof Epic ? "EPIC" : task instanceof Subtask ? "SUBTASK" : "TASK";
        String epicId = task instanceof Subtask ? String.valueOf(((Subtask) task).getEpicId()) : "";
        String startTimeStr = task.getStartTime() == null ? "null" : task.getStartTime().toString();
        String durationStr = task.getDuration().equals(Duration.ZERO) ? "PT0S" : task.getDuration().toString();
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s", task.getId(), type, task.getTitle(),
                task.getStatus(), task.getDescription(), epicId, startTimeStr, durationStr);
    }

    private static String historyToString(HistoryManager manager) {
        StringBuilder sb = new StringBuilder();
        for (Task task : manager.getHistory()) {
            sb.append(task.getId()).append(",");
        }
        return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "";
    }

    public static FileBackedTaskManager loadFromFile(File file, HistoryManager historyManager) throws IOException {
        FileBackedTaskManager manager = new FileBackedTaskManager(historyManager, file.getPath());
        String content = Files.readString(file.toPath());
        String[] lines = content.split("\n");

        for (int i = 1; i < lines.length; i++) {
            if (lines[i].trim().isEmpty()) {
                break;
            }

            Task task = fromString(lines[i]);
            manager.addToTasksList(task);
            if (task instanceof Subtask) {
                manager.actualizationEpicStatus((Subtask) task);
            }
        }

        // Пропускаем строки до истории
        int historyLineIndex = asList(lines).indexOf("") + 1;
        if (historyLineIndex > 0 && historyLineIndex < lines.length) {
            List<Integer> historyIds = historyFromString(lines[historyLineIndex]);
            for (int id : historyIds) {
                Task task = (Task) manager.getEntityById(id);
                historyManager.add(task);
            }
        }

        return manager;
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",");
        long id = Long.parseLong(parts[0]);
        String type = parts[1];
        String title = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];

        Duration duration = Duration.ZERO;
        if (parts.length > 7 && !"null".equals(parts[7])) {
            try {
                // PT0S = Period Time 0 Seconds
                if (!"PT0S\r".equals(parts[7])) {
                    duration = Duration.parse(parts[7]);
                }
            } catch (DateTimeParseException e) {
                System.err.println("Error parsing Duration for task: " + value);

            }
        }

        LocalDateTime startTime = null;
        if (parts.length > 6 && !"null".equals(parts[6])) {
            try {

                startTime = LocalDateTime.parse(parts[6]);
            } catch (Exception e) {
                System.err.println("Error parsing LocalDateTime for task: " + value);

            }
        }

        Task task = null;

        switch (type) {
            case "TASK":
                task = new Task(title, description, status, startTime, duration);
                break;
            case "EPIC":
                task = new Epic(title, description, status);
                // Для эпиков startTime и duration вычисляются отдельно
                //TODO
                break;
            case "SUBTASK":
                long epicId = Long.parseLong(parts[5].trim());
                task = new Subtask(title, description, status, epicId, startTime, duration);
                break;
        }

        if (task != null) {
            task.setId(id);
        }

        return task;
    }

    private static List<Integer> historyFromString(String value) {
        return Arrays.stream(value.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    @Override
    public Task createNewTask(String title, String description, String status,
                              LocalDateTime startTime, Duration duration) {
        Task task = super.createNewTask(title, description, status, startTime, duration);
        save();
        return task;
    }

    @Override
    public Epic createNewEpic(String title, String description) {
        Epic epic = super.createNewEpic(title, description);
        save();
        return epic;
    }

    @Override
    public Subtask createNewSubtask(String title, String description, String status, long epicId,
                                    LocalDateTime startTime, Duration duration) {
        Subtask subtask = super.createNewSubtask(title, description, status, epicId, startTime, duration);
        save();
        return subtask;
    }

    @Override
    public void actualizationEpicStatus(Subtask subtask) {
        super.actualizationEpicStatus(subtask);
        save();
    }

    @Override
    public void addToTasksList(Object obj) {
        super.addToTasksList(obj);
        save();
    }

    @Override
    public Epic getEpicById(long id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(long id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public Task getTaskById(long id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public int removeEntityFromKanban(Class<?> aClass) {
        int deletedCount = super.removeEntityFromKanban(aClass);
        save();
        return deletedCount;
    }


    @Override
    public int removeTaskById(long taskId) {
        int deletedCount = super.removeTaskById(taskId);
        save();
        return deletedCount;
    }


    @Override
    public Object updateTask(Object newTask, long taskId) {
        Object task = super.updateTask(newTask, taskId);
        save();
        return task;
    }

}
