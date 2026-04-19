//package com.example.demo.models;
//
//import com.fasterxml.jackson.annotation.JsonCreator;
//import com.fasterxml.jackson.annotation.JsonValue;
//
///**
// * 任务优先级枚举
// */
//public enum TaskPriority {
//    LOW("low", "低"),
//    MEDIUM("medium", "中"),
//    HIGH("high", "高");
//
//    private final String code;
//    private final String description;
//
//    @JsonCreator
//    public static TaskPriority fromString(String key) {
//        return key == null ? null : valueOf(key.toUpperCase());
//    }
//
//    @JsonValue
//    public String toValue() {
//        return name().toLowerCase();
//    }
//
//    TaskPriority(String code, String description) {
//        this.code = code;
//        this.description = description;
//    }
//
//    public String getCode() {
//        return code;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public static TaskPriority fromCode(String code) {
//        for (TaskPriority priority : values()) {
//            if (priority.getCode().equals(code)) {
//                return priority;
//            }
//        }
//        throw new IllegalArgumentException("Invalid task priority: " + code);
//    }
//}

package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 任务优先级枚举
 */
public enum TaskPriority {
    LOW("low", "低"),
    MEDIUM("medium", "中"),
    HIGH("high", "高");

    private final String code;
    private final String description;

    TaskPriority(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 核心容错逻辑：支持代码匹配，忽略大小写
     */
    @JsonCreator // 将注解移到这里，统一入口。Jackson 接收前端 JSON 时会调用此方法
    public static TaskPriority fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        for (TaskPriority priority : values()) {
            // 使用 equalsIgnoreCase 忽略大小写，同时支持匹配 "high" 和 "HIGH"
            if (priority.getCode().equalsIgnoreCase(code) || priority.name().equalsIgnoreCase(code)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Invalid task priority: " + code);
    }

    /**
     * 返回给前端时统一格式化为小写的 code
     */
    @JsonValue
    public String toValue() {
        return this.code;
    }
}
