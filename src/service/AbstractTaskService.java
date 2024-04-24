package service;

import model.Epic;
import model.Task;
import storage.managers.TaskRepository;

public abstract class AbstractTaskService {
    protected TaskRepository taskRepository;

    protected AbstractTaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    protected boolean isOverlap(Task task) {
        if (task instanceof Epic) {
            return false; // Для Epic не учитываем перекрытие
        }

        return taskRepository.getPrioritizedTasks().stream()
                .filter(existingTask -> !existingTask.getId().equals(task.getId()))
                .anyMatch(existingTask -> {
                    if (existingTask instanceof Epic) {
                        return false; // Для Epic не учитываем перекрытие
                    }
                    return Task.tasksOverlap(task.getStartTime(), task.getDuration(),
                            existingTask.getStartTime(), existingTask.getDuration());
                });

    }


}
