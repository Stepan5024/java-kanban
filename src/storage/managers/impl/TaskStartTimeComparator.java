package storage.managers.impl;


import model.Epic;
import model.Task;

import java.time.LocalDateTime;
import java.util.Comparator;

public class TaskStartTimeComparator implements Comparator<Task> {

    public int compare(Task t1, Task t2) {
        // Проверяем, является ли один из объектов Epic
        if (t1 instanceof Epic) {
            if (t2 instanceof Epic) {
                // Если оба объекта Epic, сравниваем их по endTime
                LocalDateTime endTime1 = ((Epic) t1).getEndTime();
                LocalDateTime endTime2 = ((Epic) t2).getEndTime();
                if (endTime1 != null && endTime2 != null) {
                    return endTime1.compareTo(endTime2);
                } else if (endTime1 == null && endTime2 != null) {
                    return 1;
                } else if (endTime1 != null && endTime2 == null) {
                    return -1;
                }
                // Если endTime одинаковый или оба null, используем ID для сравнения
                return t1.getId().compareTo(t2.getId());
            } else {
                // Если t1 - Epic, а t2 - Subtask, возвращаем -1, чтобы Epic был первым
                return -1;
            }
        } else if (t2 instanceof Epic) {
            // Если t2 - Epic, а t1 - Subtask, возвращаем 1, чтобы Epic был первым
            return 1;
        } else {
            // Обычное сравнение для Subtask
            if (t1.getStartTime() != null && t2.getStartTime() != null) {
                int timeCompare = t1.getStartTime().compareTo(t2.getStartTime());
                if (timeCompare != 0) {
                    return timeCompare;
                }
            } else if (t1.getStartTime() == null && t2.getStartTime() != null) {
                return 1;
            } else if (t1.getStartTime() != null && t2.getStartTime() == null) {
                return -1;
            }
            return t1.getId().compareTo(t2.getId());

        }
    }
}