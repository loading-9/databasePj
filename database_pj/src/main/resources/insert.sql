
-- Drop tables in reverse order of dependencies to avoid foreign key constraint errors
DROP TABLE IF EXISTS work_order_technician;
DROP TABLE IF EXISTS feedback;
DROP TABLE IF EXISTS repair_record;
DROP TABLE IF EXISTS material;
DROP TABLE IF EXISTS work_order;
DROP TABLE IF EXISTS vehicle;
DROP TABLE IF EXISTS technician;
-- DROP TABLE IF EXISTS user;
-- Creating table for User
-- CREATE TABLE user (
--                       user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
   --                   username VARCHAR(50) NOT NULL UNIQUE,
     --                 password VARCHAR(100) NOT NULL,
       --               name VARCHAR(50),
         --             contact_info VARCHAR(100),
           --           register_time DATETIME DEFAULT CURRENT_TIMESTAMP,
             --         INDEX idx_username (username)
-- );

-- Creating table for Vehicle
CREATE TABLE vehicle (
                         vehicle_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         license_plate VARCHAR(20) NOT NULL UNIQUE,
                         vehicle_type VARCHAR(50),
                         brand VARCHAR(50),
                         manufacture_year INT,
                         FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
                         INDEX idx_user_id (user_id),
                         INDEX idx_license_plate (license_plate)
);

-- Creating table for Technician
CREATE TABLE technician (
                            technician_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            username VARCHAR(50) NOT NULL UNIQUE,
                            password VARCHAR(100) NOT NULL,
                            name VARCHAR(50),
                            job_type ENUM('漆工', '焊工', '机修') NOT NULL,
                            hourly_rate DECIMAL(10, 2) NOT NULL,
                            contact_info VARCHAR(100),
                            income DECIMAL(10, 2) DEFAULT 0.0,
                            INDEX idx_username (username),
                            INDEX idx_job_type (job_type)
);

-- Creating table for WorkOrder (technician_id directly references Technician)
CREATE TABLE work_order (
                            work_order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            user_id BIGINT NOT NULL,
                            vehicle_id BIGINT NOT NULL,
                            technician_id BIGINT,
                            status ENUM('待分配', '进行中', '已完成') DEFAULT '待分配',
                            problem VARCHAR(50),
                            progress_description VARCHAR(100),
                            submit_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                            complete_time DATETIME,
                            labor_cost DECIMAL(10, 2) DEFAULT 0.0,
                            material_cost DECIMAL(10, 2) DEFAULT 0.0,
                            total_cost DECIMAL(10, 2) DEFAULT 0.0,
                            progress DOUBLE DEFAULT 0.0,
                            FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
                            FOREIGN KEY (vehicle_id) REFERENCES vehicle(vehicle_id) ON DELETE CASCADE,
                            FOREIGN KEY (technician_id) REFERENCES technician(technician_id) ON DELETE SET NULL,
                            INDEX idx_user_id (user_id),
                            INDEX idx_vehicle_id (vehicle_id),
                            INDEX idx_status (status),
                            INDEX idx_technician_id (technician_id)
);

-- Creating table for Material
CREATE TABLE material (
                          material_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          work_order_id BIGINT NOT NULL,
                          material_name VARCHAR(100) NOT NULL,
                          quantity DECIMAL(10, 2) NOT NULL,
                          unit_price DECIMAL(10, 2) NOT NULL,
                          total_price DECIMAL(10, 2) NOT NULL,
                          FOREIGN KEY (work_order_id) REFERENCES work_order(work_order_id) ON DELETE CASCADE,
                          INDEX idx_work_order_id (work_order_id)
);

-- Creating table for RepairRecord
CREATE TABLE repair_record (
                               record_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               work_order_id BIGINT NOT NULL,
                               technician_id BIGINT NOT NULL,
                               description VARCHAR(500),
                               repair_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (work_order_id) REFERENCES work_order(work_order_id) ON DELETE CASCADE,
                               FOREIGN KEY (technician_id) REFERENCES technician(technician_id) ON DELETE CASCADE,
                               INDEX idx_work_order_id (work_order_id),
                               INDEX idx_technician_id (technician_id)
);

-- Creating table for Feedback
CREATE TABLE feedback (
                          feedback_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          work_order_id BIGINT NOT NULL UNIQUE,
                          user_id BIGINT NOT NULL,
                          rating INT,
                          comment VARCHAR(500),
                          feedback_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (work_order_id) REFERENCES work_order(work_order_id) ON DELETE CASCADE,
                          FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
                          INDEX idx_work_order_id (work_order_id)
);

DROP TABLE notifications;
-- 移除外键后的简化版本
CREATE TABLE notifications (
                               notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               work_order_id BIGINT NOT NULL,
                               user_id BIGINT NOT NULL,
                               technician_id BIGINT NOT NULL,
                               message VARCHAR(255) NOT NULL,
                               remind_time DATETIME NOT NULL,
                               type VARCHAR(255) NOT NULL,
                               INDEX idx_user_id (user_id),
                               INDEX idx_technician_id (technician_id)
);

CREATE TABLE work_order_technician (
                                       work_order_id BIGINT NOT NULL,
                                       technician_id BIGINT NOT NULL,
                                       PRIMARY KEY (work_order_id, technician_id),
                                       FOREIGN KEY (work_order_id) REFERENCES work_order(work_order_id),
                                       FOREIGN KEY (technician_id) REFERENCES technician(technician_id)
);

CREATE TABLE operation_log (
                               log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               entity_type VARCHAR(50) NOT NULL, -- 实体类型（如 WorkOrder, Technician）
                               entity_id BIGINT NOT NULL, -- 实体ID
                               operation VARCHAR(50) NOT NULL, -- 操作类型（INSERT, UPDATE, DELETE）
                               old_data TEXT, -- 变更前的JSON数据
                               new_data TEXT, -- 变更后的JSON数据
                               operation_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                               operated_by BIGINT, -- 操作者ID（如 technician_id 或 user_id）
                               INDEX idx_entity_type (entity_type),
                               INDEX idx_entity_id (entity_id)
);

