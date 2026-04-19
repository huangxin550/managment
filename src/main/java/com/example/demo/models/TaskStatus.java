//package com.example.demo.models;
//
//import com.fasterxml.jackson.annotation.JsonCreator;
//import com.fasterxml.jackson.annotation.JsonValue;
//
///**
// * 任务状态枚举
// */
//public enum TaskStatus {
//    PENDING("pending", "待处理"),
//    IN_PROGRESS("in_progress", "进行中"),
//    COMPLETED("completed", "已完成");
//
//    private final String code;
//    private final String description;
//
//    @JsonCreator
//    public static TaskStatus fromString(String key) {
//        return key == null ? null : valueOf(key.toUpperCase());
//    }
//
//    @JsonValue
//    public String toValue() {
//        return name().toLowerCase();
//    }
//
//    TaskStatus(String code, String description) {
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
//    public static TaskStatus fromCode(String code) {
//        for (TaskStatus status : values()) {
//            if (status.getCode().equals(code)) {
//                return status;
//            }
//        }
//        throw new IllegalArgumentException("Invalid task status: " + code);
//    }
//}

package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 任务状态枚举
 */
public enum TaskStatus {
    PENDING("pending", "待处理"),
    IN_PROGRESS("in_progress", "进行中"),
    COMPLETED("completed", "已完成");

    private final String code;
    private final String description;

    TaskStatus(String code, String description) {
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
     * 核心容错逻辑：支持代码匹配，忽略大小写。
     * 整合了原有的 fromString 功能，统一入口。
     */
    @JsonCreator
    public static TaskStatus fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        for (TaskStatus status : values()) {
            // 使用 equalsIgnoreCase 忽略大小写
            if (status.getCode().equalsIgnoreCase(code) || status.name().equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid task status: " + code);
    }

    /**
     * 返回给前端时统一格式化为小写的 code
     */
    @JsonValue
    public String toValue() {
        return this.code;
    }
}
