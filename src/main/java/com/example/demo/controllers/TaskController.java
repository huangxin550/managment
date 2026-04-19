package com.example.demo.controllers;

import com.example.demo.models.CreateTaskRequest;
import com.example.demo.models.TaskResponse;
import com.example.demo.models.UpdateTaskRequest;
import com.example.demo.services.TaskService;
import com.example.demo.utils.ApiResponse;
import com.example.demo.utils.PageResult;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 任务控制器 - RESTful API 接口
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    /**
     * 创建任务
     * POST /api/tasks
     */
    @PostMapping
    public ResponseEntity<ApiResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        TaskResponse task = taskService.createTask(request);
        return ResponseEntity.ok(ApiResponse.success("任务创建成功", task));
    }

    /**
     * 根据 ID 查询任务
     * GET /api/tasks/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getTaskById(@PathVariable Long id) {
        TaskResponse task = taskService.getTaskById(id);
        return ResponseEntity.ok(ApiResponse.success(task));
    }

    /**
     * 查询所有任务（分页）
     * GET /api/tasks?page=0&size=10&sortBy=createdAt&order=desc
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String order) {
        PageResult<TaskResponse> tasks = taskService.getAllTasks(page, size, sortBy, order);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    /**
     * 筛选任务
     * GET /api/tasks/filter?status=pending&priority=high&tag=urgent&page=0&size=10
     */
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse> filterTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String order) {
        PageResult<TaskResponse> tasks = taskService.filterTasks(
                status, priority, tag, page, size, sortBy, order);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    /**
     * 搜索任务
     * GET /api/tasks/search?keyword=关键词&page=0&size=10
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchTasks(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<TaskResponse> tasks = taskService.searchTasks(keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    /**
     * 更新任务
     * PUT /api/tasks/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest request) {
        TaskResponse task = taskService.updateTask(id, request);
        return ResponseEntity.ok(ApiResponse.success("任务更新成功", task));
    }

    /**
     * 删除任务
     * DELETE /api/tasks/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(ApiResponse.success("任务删除成功", null));
    }

    /**
     * 获取任务依赖树
     * GET /api/tasks/{id}/dependencies
     */
    @GetMapping("/{id}/dependencies")
    public ResponseEntity<ApiResponse> getDependencyTree(@PathVariable Long id) {
        Map<String, Object> dependencyTree = taskService.getDependencyTree(id);
        return ResponseEntity.ok(ApiResponse.success(dependencyTree));
    }
}
