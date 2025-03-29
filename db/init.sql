-- 시간 설정
SET GLOBAL time_zone = '+9:00';
SET time_zone = '+9:00';

-- 데이터베이스 선택
USE bsslab;

-- 관리자 계정 생성 (비밀번호: adminpassword)
INSERT INTO users (username, email, password, role, status, created_at, updated_at)
SELECT 'admin', 'admin@bsslab.com',
       '$2a$10$oEm2dHMudOV0zYIVYD27Nu.rleuKzV5Ir8bNyW4cIQGqvqqeuC4e6',
       'ROLE_ADMIN', 'ACTIVE', NOW(), NOW()
FROM dual
WHERE NOT EXISTS (
    SELECT * FROM users WHERE username = 'admin'
);