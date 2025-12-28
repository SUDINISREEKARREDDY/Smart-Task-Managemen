package com.taskmanager.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "tasks")
public class Task {

    @Id
    private String id;

    private String title;
    private String description;

    private int priority; // 1 (low) - 5 (high)

    private LocalDateTime deadline;

    private String status; // PENDING, DONE

    private String userId;

    // Task dependencies (task IDs that must be completed first)
    private List<String> dependsOn;
}
