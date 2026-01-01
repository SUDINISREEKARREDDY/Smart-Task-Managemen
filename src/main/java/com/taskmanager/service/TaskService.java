package com.taskmanager.service;

import com.taskmanager.model.Task;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.util.CycleDetector;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // ===============================
    // CREATE TASK
    // ===============================
    public Task createTask(Task task) {
        task.setStatus("PENDING");

        if (task.getDependsOn() == null) {
            task.setDependsOn(new ArrayList<>());
        }

        return taskRepository.save(task);
    }

    // ===============================
    // GET TASKS BY USER
    // ===============================
    public List<Task> getTasksByUser(String userId) {
        return taskRepository.findByUserId(userId);
    }

    // ===============================
    // ADD DEPENDENCY WITH CYCLE CHECK
    // ===============================
    public Task addDependency(String taskId, String dependsOnId) {

        // Fetch main task
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Fetch dependency task
        Task dependencyTask = taskRepository.findById(dependsOnId)
                .orElseThrow(() -> new RuntimeException("Dependency task not found"));

        // Ensure same user
        if (!task.getUserId().equals(dependencyTask.getUserId())) {
            throw new RuntimeException("Tasks belong to different users");
        }

        // Initialize dependsOn list
        if (task.getDependsOn() == null) {
            task.setDependsOn(new ArrayList<>());
        }

        // Avoid duplicate dependency
        if (task.getDependsOn().contains(dependsOnId)) {
            return task;
        }

        // ===============================
        // BUILD GRAPH FOR CYCLE CHECK
        // ===============================
        List<Task> tasks = taskRepository.findByUserId(task.getUserId());
        Map<String, List<String>> graph = new HashMap<>();

        for (Task t : tasks) {
            graph.put(
                t.getId(),
                t.getDependsOn() == null
                    ? new ArrayList<>()
                    : new ArrayList<>(t.getDependsOn())
            );
        }

        // Add new dependency temporarily
        graph.get(taskId).add(dependsOnId);

        // ===============================
        // CYCLE DETECTION (DFS)
        // ===============================
        if (CycleDetector.hasCycle(graph)) {
            throw new RuntimeException("Cycle detected! Dependency not allowed.");
        }

        // ===============================
        // SAVE DEPENDENCY
        // ===============================
        task.getDependsOn().add(dependsOnId);
        return taskRepository.save(task);
    }
}
