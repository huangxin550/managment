package com.example.demo.services;

import com.example.demo.models.Task;
import com.example.demo.models.TaskPriority;
import com.example.demo.models.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 任务数据访问接口
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // 根据状态查询
    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    // 根据优先级查询
    Page<Task> findByPriority(TaskPriority priority, Pageable pageable);

    // 根据状态和优先级查询
    Page<Task> findByStatusAndPriority(TaskStatus status, TaskPriority priority, Pageable pageable);
    // 根据标签查询
    @Query("SELECT t FROM Task t JOIN t.tags tg WHERE tg LIKE CONCAT('%', :tag, '%')")
    Page<Task> findByTagContaining(@Param("tag") String tag, Pageable pageable);

    // 根据状态和标签查询
    @Query("SELECT t FROM Task t JOIN t.tags tg WHERE t.status = :status AND tg LIKE CONCAT('%', :tag, '%')")
    Page<Task> findByStatusAndTagContaining(@Param("status") TaskStatus status,
                                            @Param("tag") String tag,
                                            Pageable pageable);

    // 根据优先级和标签查询
    @Query("SELECT t FROM Task t JOIN t.tags tg WHERE t.priority = :priority AND tg LIKE CONCAT('%', :tag, '%')")
    Page<Task> findByPriorityAndTagContaining(@Param("priority") TaskPriority priority,
                                              @Param("tag") String tag,
                                              Pageable pageable);

    // 根据状态、优先级和标签查询
    @Query("SELECT t FROM Task t JOIN t.tags tg WHERE t.status = :status AND t.priority = :priority AND tg LIKE CONCAT('%', :tag, '%')")
    Page<Task> findByStatusAndPriorityAndTagContaining(@Param("status") TaskStatus status,
                                                       @Param("priority") TaskPriority priority,
                                                       @Param("tag") String tag,
                                                       Pageable pageable);
    // 根据标签查询（模糊匹配）
//    @Query("SELECT t FROM Task t JOIN t.tags tag WHERE tag LIKE CONCAT('%', :tag, '%')")
//    Page<Task> findByTagContaining(@Param("tag") String tag, Pageable pageable);
//
//    // 根据状态和标签查询
//    @Query("SELECT t FROM Task t JOIN t.tags tag WHERE t.status = :status AND tag LIKE %:tag%")
//    Page<Task> findByStatusAndTagContaining(@Param("status") TaskStatus status,
//                                            @Param("tag") String tag,
//                                            Pageable pageable);
//
//    // 根据优先级和标签查询
//    @Query("SELECT t FROM Task t JOIN t.tags tag WHERE t.priority = :priority AND tag LIKE %:tag%")
//    Page<Task> findByPriorityAndTagContaining(@Param("priority") TaskPriority priority,
//                                              @Param("tag") String tag,
//                                              Pageable pageable);
//
//    // 根据状态、优先级和标签查询
//    @Query("SELECT t FROM Task t JOIN t.tags tag WHERE t.status = :status AND t.priority = :priority AND tag LIKE %:tag%")
//    Page<Task> findByStatusAndPriorityAndTagContaining(@Param("status") TaskStatus status,
//                                                       @Param("priority") TaskPriority priority,
//                                                       @Param("tag") String tag,
//                                                       Pageable pageable);

    // 查询所有依赖指定任务的任務
    @Query("SELECT t FROM Task t JOIN t.dependencies d WHERE d.id = :taskId")
    List<Task> findTasksDependingOn(@Param("taskId") Long taskId);

    // 全文搜索（标题和描述）
    @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Task> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
