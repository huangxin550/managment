package com.example.demo.utils;

/**
 * 任务依赖冲突异常
 */
public class TaskDependencyException extends RuntimeException {

    public TaskDependencyException(String message) {
        super(message);
    }
}
