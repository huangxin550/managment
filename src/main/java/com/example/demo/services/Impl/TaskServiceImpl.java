package com.example.demo.services.Impl;

import com.example.demo.models.*;
import com.example.demo.services.TaskRepository;
import com.example.demo.services.TaskService;
import com.example.demo.utils.PageResult;
import com.example.demo.utils.TaskDependencyException;
import com.example.demo.utils.TaskNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 任务服务实现类 - 核心业务逻辑
 */
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);
    private static final String TASK_CACHE_KEY = "task:";
    private static final long CACHE_EXPIRE_HOURS = 24;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 创建任务
     */
    @Override
    public TaskResponse createTask(CreateTaskRequest request) {
        logger.info("创建任务: {}", request.getTitle());

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(TaskStatus.fromCode(request.getStatus()));
        task.setPriority(TaskPriority.fromCode(request.getPriority()));
        task.setTags(request.getTags() != null ? request.getTags() : new ArrayList<>());

        // 设置依赖关系
        if (request.getDependencies() != null && !request.getDependencies().isEmpty()) {
            List<Task> dependencies = taskRepository.findAllById(request.getDependencies());
            if (dependencies.size() != request.getDependencies().size()) {
                throw new TaskDependencyException("部分依赖任务不存在");
            }
            task.setDependencies(dependencies);
        }

        Task savedTask = taskRepository.save(task);
        logger.info("任务创建成功，ID: {}", savedTask.getId());

        return new TaskResponse(savedTask);
    }

    /**
     * 根据 ID 查询任务（带缓存）
     */
    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        logger.debug("查询任务: {}", id);

        // 尝试从缓存获取
        String cacheKey = TASK_CACHE_KEY + id;
        TaskResponse cachedTask = (TaskResponse) redisTemplate.opsForValue().get(cacheKey);
        if (cachedTask != null) {
            logger.debug("从缓存获取任务: {}", id);
            return cachedTask;
        }

        // 从数据库查询
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        TaskResponse response = new TaskResponse(task);

        // 存入缓存
        redisTemplate.opsForValue().set(cacheKey, response, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        logger.debug("任务已缓存: {}", id);

        return response;
    }

    /**
     * 查询所有任务（分页）
     */
    @Override
    @Transactional(readOnly = true)
    public PageResult<TaskResponse> getAllTasks(int page, int size, String sortBy, String order) {
        logger.debug("查询任务列表: page={}, size={}, sortBy={}", page, size, sortBy);

        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy != null ? sortBy : "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Task> tasks = taskRepository.findAll(pageable);
        Page<TaskResponse> taskResponses = tasks.map(TaskResponse::new);

        return new PageResult<>(taskResponses);
    }

    /**
     * 筛选和排序任务
     */
    @Override
    @Transactional(readOnly = true)
    public PageResult<TaskResponse> filterTasks(String status, String priority, String tag,
                                                 int page, int size, String sortBy, String order) {
        logger.debug("筛选任务: status={}, priority={}, tag={}", status, priority, tag);

        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy != null ? sortBy : "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Task> tasks;

        if (status != null && priority != null && tag != null) {
            tasks = taskRepository.findByStatusAndPriorityAndTagContaining(
                    TaskStatus.fromCode(status),
                    TaskPriority.fromCode(priority),
                    tag,
                    pageable);
        } else if (status != null && priority != null) {
            tasks = taskRepository.findByStatusAndPriority(
                    TaskStatus.fromCode(status),
                    TaskPriority.fromCode(priority),
                    pageable);
        } else if (status != null && tag != null) {
            tasks = taskRepository.findByStatusAndTagContaining(
                    TaskStatus.fromCode(status),
                    tag,
                    pageable);
        } else if (priority != null && tag != null) {
            tasks = taskRepository.findByPriorityAndTagContaining(
                    TaskPriority.fromCode(priority),
                    tag,
                    pageable);
        } else if (status != null) {
            tasks = taskRepository.findByStatus(TaskStatus.fromCode(status), pageable);
        } else if (priority != null) {
            tasks = taskRepository.findByPriority(TaskPriority.fromCode(priority), pageable);
        } else if (tag != null) {
            tasks = taskRepository.findByTagContaining(tag, pageable);
        } else {
            tasks = taskRepository.findAll(pageable);
        }

        Page<TaskResponse> taskResponses = tasks.map(TaskResponse::new);
        return new PageResult<>(taskResponses);
    }

    /**
     * 全文搜索任务
     */
    @Override
    @Transactional(readOnly = true)
    public PageResult<TaskResponse> searchTasks(String keyword, int page, int size) {
        logger.debug("搜索任务: keyword={}", keyword);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Task> tasks = taskRepository.searchByKeyword(keyword, pageable);
        Page<TaskResponse> taskResponses = tasks.map(TaskResponse::new);

        return new PageResult<>(taskResponses);
    }

    /**
     * 更新任务
     */
    @Override
    public TaskResponse updateTask(Long id, UpdateTaskRequest request) {
        logger.info("更新任务: {}", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        // 检查状态变更的依赖约束
        if (request.getStatus() != null) {
            TaskStatus newStatus = TaskStatus.fromCode(request.getStatus());
            checkDependencyConstraint(task, newStatus);
            task.setStatus(newStatus);
        }

        // 更新其他字段
        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            task.setPriority(TaskPriority.fromCode(request.getPriority()));
        }
        if (request.getTags() != null) {
            task.setTags(request.getTags());
        }

        // 更新依赖关系
        if (request.getDependencies() != null) {
            List<Task> dependencies = taskRepository.findAllById(request.getDependencies());
            if (dependencies.size() != request.getDependencies().size()) {
                throw new TaskDependencyException("部分依赖任务不存在");
            }
            task.setDependencies(dependencies);
        }

        Task updatedTask = taskRepository.save(task);

        // 清除缓存
        clearTaskCache(id);

        logger.info("任务更新成功: {}", id);
        return new TaskResponse(updatedTask);
    }

    /**
     * 删除任务
     */
    @Override
    public void deleteTask(Long id) {
        logger.info("删除任务: {}", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        // 检查是否有其他任务依赖此任务
        List<Task> dependents = taskRepository.findTasksDependingOn(id);
        if (!dependents.isEmpty()) {
            List<Long> dependentIds = dependents.stream().map(Task::getId).collect(Collectors.toList());
            throw new TaskDependencyException(
                    "无法删除任务，以下任务依赖于此任务: " + dependentIds);
        }

        taskRepository.delete(task);

        // 清除缓存
        clearTaskCache(id);

        logger.info("任务删除成功: {}", id);
    }

    /**
     * 获取任务依赖树
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getDependencyTree(Long taskId) {
        logger.debug("获取任务依赖树: {}", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        Map<String, Object> tree = new HashMap<>();
        tree.put("task", new TaskResponse(task));
        tree.put("dependencies", buildDependencyTree(task.getDependencies(), new HashSet<>()));
        tree.put("dependents", buildDependentTree(task.getDependents(), new HashSet<>()));

        return tree;
    }

    /**
     * 构建依赖树（递归）
     */
    private List<Map<String, Object>> buildDependencyTree(List<Task> tasks, Set<Long> visited) {
        List<Map<String, Object>> tree = new ArrayList<>();
        for (Task task : tasks) {
            if (visited.contains(task.getId())) {
                continue; // 避免循环依赖
            }
            visited.add(task.getId());

            Map<String, Object> node = new HashMap<>();
            node.put("task", new TaskResponse(task));
            node.put("dependencies", buildDependencyTree(task.getDependencies(), visited));
            tree.add(node);
        }
        return tree;
    }

    /**
     * 构建被依赖树（递归）
     */
    private List<Map<String, Object>> buildDependentTree(List<Task> tasks, Set<Long> visited) {
        List<Map<String, Object>> tree = new ArrayList<>();
        for (Task task : tasks) {
            if (visited.contains(task.getId())) {
                continue;
            }
            visited.add(task.getId());

            Map<String, Object> node = new HashMap<>();
            node.put("task", new TaskResponse(task));
            node.put("dependents", buildDependentTree(task.getDependents(), visited));
            tree.add(node);
        }
        return tree;
    }

    /**
     * 检查依赖约束
     */
    private void checkDependencyConstraint(Task task, TaskStatus newStatus) {
        if (newStatus == TaskStatus.COMPLETED) {
            List<Task> incompleteDependencies = task.getDependencies().stream()
                    .filter(dep -> dep.getStatus() != TaskStatus.COMPLETED)
                    .collect(Collectors.toList());

            if (!incompleteDependencies.isEmpty()) {
                List<Long> incompleteIds = incompleteDependencies.stream()
                        .map(Task::getId)
                        .collect(Collectors.toList());
                throw new TaskDependencyException(
                        "无法完成任务，以下依赖任务未完成: " + incompleteIds);
            }
        }
    }

    /**
     * 清除任务缓存
     */
    private void clearTaskCache(Long taskId) {
        String cacheKey = TASK_CACHE_KEY + taskId;
        redisTemplate.delete(cacheKey);
        logger.debug("清除任务缓存: {}", taskId);
    }
}
