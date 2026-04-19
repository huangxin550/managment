package com.example.demo.utils;

/**
 * 任务未找到异常
 */
public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(String message) {
        super(message);
    }

    public TaskNotFoundException(Long taskId) {
        super("任务不存在，ID: " + taskId);
    }
}
