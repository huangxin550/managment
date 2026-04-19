package com.example.demo.services;

import com.example.demo.models.CreateTaskRequest;
import com.example.demo.models.TaskResponse;
import com.example.demo.models.UpdateTaskRequest;
import com.example.demo.utils.PageResult;

import java.util.Map;

/**
 * 任务服务接口
 */
public interface TaskService {

    /**
     * 创建任务
     */
    TaskResponse createTask(CreateTaskRequest request);

    /**
     * 根据 ID 查询任务
     */
    TaskResponse getTaskById(Long id);

    /**
     * 查询所有任务（分页）
     */
    PageResult<TaskResponse> getAllTasks(int page, int size, String sortBy, String order);

    /**
     * 筛选和排序任务
     */
    PageResult<TaskResponse> filterTasks(String status, String priority, String tag,
                                         int page, int size, String sortBy, String order);

    /**
     * 全文搜索任务
     */
    PageResult<TaskResponse> searchTasks(String keyword, int page, int size);

    /**
     * 更新任务
     */
    TaskResponse updateTask(Long id, UpdateTaskRequest request);

    /**
     * 删除任务
     */
    void deleteTask(Long id);

    /**
     * 获取任务依赖树
     */
    Map<String, Object> getDependencyTree(Long taskId);
}
