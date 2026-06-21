-- 创建数据库并建表
-- 使用时：mysql -u root -p < V20260621_000__init_database.sql

CREATE DATABASE IF NOT EXISTS history_museum CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 朝代表
CREATE TABLE IF NOT EXISTS dynasties (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uid VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    full_name VARCHAR(100),
    period VARCHAR(200),
    period_start INT,
    period_end INT,
    founder VARCHAR(100),
    last_ruler VARCHAR(100),
    capital VARCHAR(200),
    duration VARCHAR(50),
    highlights VARCHAR(200),
    description TEXT,
    fall_reason VARCHAR(500),
    legacy TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_period_start (period_start),
    INDEX idx_uid (uid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 事件表
CREATE TABLE IF NOT EXISTS events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uid VARCHAR(64) NOT NULL UNIQUE,
    title VARCHAR(200) NOT NULL,
    year INT NOT NULL,
    year_display VARCHAR(100),
    year_precision VARCHAR(20),
    category VARCHAR(50) NOT NULL,
    dynasty_id BIGINT,
    description TEXT,
    `fulltext` TEXT,
    tags TEXT,
    related_events TEXT,
    related_persons TEXT,
    source VARCHAR(500),
    crawl_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_year (year),
    INDEX idx_category (category),
    INDEX idx_dynasty (dynasty_id),
    FOREIGN KEY (dynasty_id) REFERENCES dynasties(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 人物表
CREATE TABLE IF NOT EXISTS persons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uid VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    courtesy_name VARCHAR(100),
    dynasty_id BIGINT,
    years TEXT,
    years_display VARCHAR(100),
    gender VARCHAR(20),
    roles TEXT,
    quote VARCHAR(500),
    bio TEXT,
    tags TEXT,
    related_events TEXT,
    related_persons TEXT,
    source VARCHAR(500),
    crawl_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name),
    INDEX idx_gender (gender),
    INDEX idx_dynasty (dynasty_id),
    FOREIGN KEY (dynasty_id) REFERENCES dynasties(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- 知识卡片表
CREATE TABLE IF NOT EXISTS knowledge_cards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uid VARCHAR(64) NOT NULL UNIQUE,
    title VARCHAR(200) NOT NULL,
    start_year INT,
    start_year_display VARCHAR(100),
    dynasty_id BIGINT,
    description TEXT,
    `fulltext` TEXT,
    tags TEXT,
    relevant_events TEXT,
    relevant_persons TEXT,
    `meta` VARCHAR(2000),
    source VARCHAR(500),
    crawl_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (dynasty_id) REFERENCES dynasties(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
