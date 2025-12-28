package com.taskmanager.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.taskmanager.model.Task;
import com.taskmanager.repository.TaskRepository;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(Task task) {
        task.setStatus("PENDING");
        return taskRepository.save(task);
    }

    public List<Task> getTasksByUser(String userId) {
        return taskRepository.findByUserId(userId);
    }
}
