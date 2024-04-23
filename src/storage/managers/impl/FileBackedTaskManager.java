package storage.managers.impl;

import model.Epic;
import model.Subtask;
import model.Task;

import java.io.FileNotFoundException;

import model.TaskStatus;
import storage.history.HistoryRepository;
import storage.managers.TaskRepository;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskRepository {

    private final String filePath;


    public FileBackedTaskManager(String filePath, HistoryRepository historyRepository) {
        super(historyRepository);
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
            writer.println(historyToString());
        } catch (FileNotFoundException e) {
            throw new ManagerSaveException("Ошибка при попытке сохранить данные в файл: файл не найден.", e);
        }
    }

    private String taskToString(Task task) {
        String type = task instanceof Epic ? "EPIC" : task instanceof Subtask ? "SUBTASK" : "TASK";
        String epicId = task instanceof Subtask ? String.valueOf(((Subtask) task).getEpicId()) : "";
        String startTimeStr = task.getStartTime() == null ? "null" : task.getStartTime().toString();
        String durationStr = task.getDuration() == null || task.getDuration().equals(Duration.ZERO) ? "PT0S" : task.getDuration().toString();
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s", task.getId(), type, task.getTitle(), task.getStatus(), task.getDescription(), epicId, startTimeStr, durationStr);
    }

    private static String historyToString() {
        StringBuilder sb = new StringBuilder();
        System.out.println("historyToString " + historyRepository.getHistory());
        for (Task task : historyRepository.getHistory()) {
            sb.append(task.getId()).append(",");
        }
        return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "";
    }

    public static TaskRepository loadFromFile(File file) throws IOException {
        TaskRepository manager = new FileBackedTaskManager(file.getPath(), historyRepository);
        Long highestId = 0l;

        String content = Files.readString(file.toPath());
        String[] lines = content.split("\n");

        for (int i = 1; i < lines.length; i++) {
            if (lines[i].trim().isEmpty()) {
                break;
            }

            Task task = fromString(lines[i]);
            manager.addTask(task);

            if (task != null && task.getId() > highestId) {
                highestId = task.getId();
            }
        }
        generateId.setId(highestId + 1);
        // Пропускаем строки до истории
        int historyLineIndex = asList(lines).indexOf("") + 1;
        if (historyLineIndex > 0 && historyLineIndex < lines.length) {
            List<Integer> historyIds = historyFromString(lines[historyLineIndex]);
            for (Integer id : historyIds) {
                Task task = manager.getEntityById(Long.valueOf(id));
                historyRepository.addTask(task);
            }
        }

        return manager;
    }

    static List<Integer> historyFromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return List.of(); // Возвращаем пустой список
        }

        return Arrays.stream(value.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",");
        long id = Long.parseLong(parts[0]);
        String type = parts[1];
        String title = parts[2];
        TaskStatus status;
        if (parts.length > 3 && parts[3] != null && !"null".equalsIgnoreCase(parts[3].trim())) {
            status = TaskStatus.valueOf(parts[3].trim());
        } else {
            status = TaskStatus.NEW;
        }
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


    @Override
    public Task getTaskById(Long id) {
        save();
        return super.getTaskById(id);
    }

    @Override
    public Subtask getSubtaskById(Long id) {
        save();
        return super.getSubtaskById(id);
    }

    @Override
    public Epic getEpicById(Long id) {
        save();
        return super.getEpicById(id);
    }

    @Override
    public boolean addTask(Task task) {
        System.out.println("File save");
        boolean result = super.addTask(task);
        save();
        return result;
    }


    @Override
    public boolean deleteTask(Task task) {
        boolean result = super.deleteTask(task);
        save();
        return result;
    }

    @Override
    public int deleteListOfTask(List<Task> list) {
        int result = super.deleteListOfTask(list);
        save();
        return result;
    }

}
