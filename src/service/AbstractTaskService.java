package service;

import model.Task;
import storage.managers.TaskRepository;

public abstract class AbstractTaskService {
    protected TaskRepository taskRepository;

    protected AbstractTaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    protected boolean isOverlap(Task task) {
        return taskRepository.getPrioritizedTasks().stream()
                .filter(existingTask -> !existingTask.getId().equals(task.getId()))
                .anyMatch(existingTask -> Task.tasksOverlap(task.getStartTime(), task.getDuration(),
                        existingTask.getStartTime(), existingTask.getDuration())
        );
    }


}
