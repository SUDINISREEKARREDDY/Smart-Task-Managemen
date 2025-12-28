package com.taskmanager.controller;

import com.taskmanager.model.Task;
import com.taskmanager.service.TaskSchedulerService;
import com.taskmanager.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskSchedulerService schedulerService;

    public TaskController(TaskService taskService,
                          TaskSchedulerService schedulerService) {
        this.taskService = taskService;
        this.schedulerService = schedulerService;
    }

    // ===============================
    // CREATE TASK
    // ===============================
    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }

    // ===============================
    // GET TASKS BY USER
    // ===============================
    @GetMapping("/user/{userId}")
    public List<Task> getTasksByUser(@PathVariable String userId) {
        return taskService.getTasksByUser(userId);
    }

    // ===============================
    // 🔥 SCHEDULE TASKS (DSA ENGINE)
    // ===============================
    @GetMapping("/schedule/{userId}")
    public List<Task> scheduleTasks(@PathVariable String userId) {
        return schedulerService.scheduleTasks(userId);
    }
}
