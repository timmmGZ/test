-- GymCN 数据库初始化脚本（PostgreSQL 版本）
-- 创建数据库（如果不存在）

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    phone VARCHAR(20) NOT NULL UNIQUE,
    nickname VARCHAR(50) DEFAULT NULL,
    avatar VARCHAR(500) DEFAULT NULL,
    user_type VARCHAR(20) DEFAULT 'personal',
    enterprise_id BIGINT DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_users_phone ON users(phone);
CREATE INDEX IF NOT EXISTS idx_users_enterprise_id ON users(enterprise_id);

-- 场馆表
CREATE TABLE IF NOT EXISTS venues (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(50) DEFAULT '广州',
    district VARCHAR(50) DEFAULT NULL,
    longitude DECIMAL(10, 6) DEFAULT NULL,
    latitude DECIMAL(10, 6) DEFAULT NULL,
    type VARCHAR(20) DEFAULT 'gym',
    cover_image VARCHAR(500) DEFAULT NULL,
    phone VARCHAR(20) DEFAULT NULL,
    business_hours VARCHAR(100) DEFAULT NULL,
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_venues_city ON venues(city);
CREATE INDEX IF NOT EXISTS idx_venues_type ON venues(type);
CREATE INDEX IF NOT EXISTS idx_venues_status ON venues(status);

-- 企业表
CREATE TABLE IF NOT EXISTS enterprises (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(50) DEFAULT NULL,
    contact_phone VARCHAR(20) DEFAULT NULL,
    address VARCHAR(255) DEFAULT NULL,
    employee_count INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_enterprises_name ON enterprises(name);

-- 会员卡表
CREATE TABLE IF NOT EXISTS membership_cards (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    enterprise_id BIGINT DEFAULT NULL,
    card_type VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0,
    CONSTRAINT fk_membership_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_membership_enterprise FOREIGN KEY (enterprise_id) REFERENCES enterprises(id)
);

CREATE INDEX IF NOT EXISTS idx_membership_user_id ON membership_cards(user_id);
CREATE INDEX IF NOT EXISTS idx_membership_status ON membership_cards(status);
CREATE INDEX IF NOT EXISTS idx_membership_end_date ON membership_cards(end_date);

-- 入场记录表
CREATE TABLE IF NOT EXISTS checkin_records (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    venue_id BIGINT NOT NULL,
    checkin_time TIMESTAMP NOT NULL,
    checkout_time TIMESTAMP DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_checkin_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_checkin_venue FOREIGN KEY (venue_id) REFERENCES venues(id)
);

CREATE INDEX IF NOT EXISTS idx_checkin_user_id ON checkin_records(user_id);
CREATE INDEX IF NOT EXISTS idx_checkin_venue_id ON checkin_records(venue_id);
CREATE INDEX IF NOT EXISTS idx_checkin_time ON checkin_records(checkin_time);

-- 插入示例数据（广州场馆）
INSERT INTO venues (name, address, city, district, type, business_hours, status) VALUES
('威尔士健身（天河城店）', '天河区天河路208号天河城6楼', '广州', '天河区', 'gym', '06:00-23:00', 'active'),
('超级猩猩健身（珠江新城店）', '天河区珠江新城花城大道88号', '广州', '天河区', 'gym', '07:00-22:00', 'active'),
('一梵瑜伽工作室', '海珠区昌岗路江南坊2楼', '广州', '海珠区', 'yoga', '09:00-21:00', 'active'),
('力健游泳馆', '番禺区大学城外环西路28号', '广州', '番禺区', 'swimming', '06:00-21:00', 'active'),
('舒适堡健身（正佳广场店）', '天河区天河路228号正佳广场5楼', '广州', '天河区', 'gym', '10:00-22:00', 'active')
ON CONFLICT DO NOTHING;
