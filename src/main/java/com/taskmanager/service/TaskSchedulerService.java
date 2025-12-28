package com.taskmanager.service;

import com.taskmanager.model.Task;
import com.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TaskSchedulerService {

    private final TaskRepository taskRepository;

    public TaskSchedulerService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> scheduleTasks(String userId) {

        List<Task> tasks = taskRepository.findByUserId(userId);

        // Edge case
        if (tasks.isEmpty()) {
            return Collections.emptyList();
        }

        // ---------- STEP 1: Build Graph & In-Degree ----------
        Map<String, List<String>> graph = new HashMap<>();
        Map<String, Integer> inDegree = new HashMap<>();
        Map<String, Task> taskMap = new HashMap<>();

        for (Task task : tasks) {
            taskMap.put(task.getId(), task);
            graph.put(task.getId(), new ArrayList<>());
            inDegree.put(task.getId(), 0);
        }

        for (Task task : tasks) {
            if (task.getDependsOn() != null) {
                for (String depId : task.getDependsOn()) {
                    if (graph.containsKey(depId)) {
                        graph.get(depId).add(task.getId());
                        inDegree.put(task.getId(), inDegree.get(task.getId()) + 1);
                    }
                }
            }
        }

        // ---------- STEP 2: Priority Queue (Greedy) ----------
        PriorityQueue<Task> pq = new PriorityQueue<>(
                (a, b) -> {
                    if (b.getPriority() != a.getPriority()) {
                        return b.getPriority() - a.getPriority(); // higher priority first
                    }
                    return a.getDeadline().compareTo(b.getDeadline()); // earlier deadline
                }
        );

        for (Task task : tasks) {
            if (inDegree.get(task.getId()) == 0) {
                pq.add(task);
            }
        }

        // ---------- STEP 3: Scheduling ----------
        List<Task> schedule = new ArrayList<>();

        while (!pq.isEmpty()) {
            Task current = pq.poll();
            schedule.add(current);

            for (String dependentId : graph.get(current.getId())) {
                inDegree.put(dependentId, inDegree.get(dependentId) - 1);
                if (inDegree.get(dependentId) == 0) {
                    pq.add(taskMap.get(dependentId));
                }
            }
        }

        // ---------- STEP 4: Cycle Detection ----------
        if (schedule.size() != tasks.size()) {
            throw new RuntimeException("Cycle detected in task dependencies");
        }

        return schedule;
    }
}
