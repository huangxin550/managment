CREATE TABLE IF NOT EXISTS tasks (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     title VARCHAR(200) NOT NULL,
                                     description TEXT,
                                     status VARCHAR(20) NOT NULL,
                                     priority VARCHAR(20) NOT NULL,
                                     created_at DATETIME NOT NULL,
                                     updated_at DATETIME NOT NULL,
                                     INDEX idx_status (status),
                                     INDEX idx_priority (priority),
                                     INDEX idx_created_at (created_at),
                                     INDEX idx_status_priority (status, priority)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS task_tags (
                                         task_id BIGINT NOT NULL,
                                         tag VARCHAR(255),
                                         UNIQUE KEY uk_task_tag (task_id, tag),
                                         INDEX idx_tag (tag),
                                         FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS task_dependencies (
                                                 task_id BIGINT NOT NULL,
                                                 depends_on_task_id BIGINT NOT NULL,
                                                 PRIMARY KEY (task_id, depends_on_task_id),
                                                 INDEX idx_depends_on (depends_on_task_id),
                                                 FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
                                                 FOREIGN KEY (depends_on_task_id) REFERENCES tasks(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;