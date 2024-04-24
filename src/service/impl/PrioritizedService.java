package service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import handler.TaskHandler;
import model.Task;
import service.*;
import storage.managers.TaskRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class PrioritizedService extends AbstractTaskService implements IPrioritizedService {

    public PrioritizedService(TaskRepository taskRepository, IHistoryService historyService) {
        super(taskRepository);
    }


    @Override
    public String getPrioritizedTasks() {
        List<Task> list = taskRepository.getListOfAllEntities();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new TaskHandler.DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new TaskHandler.LocalDateTimeAdapter())
                .create();
        return gson.toJson(list);
    }
}
