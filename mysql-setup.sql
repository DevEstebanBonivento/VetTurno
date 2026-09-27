-- Execute in MySQL Workbench or mysql CLI:

-- Create database
CREATE DATABASE IF NOT EXISTS vetturno;

-- Create user with permissions (use your actual password)
CREATE USER IF NOT EXISTS 'vetturno_user'@'localhost' IDENTIFIED BY 'tu_contraseña';
GRANT ALL PRIVILEGES ON vetturno.* TO 'vetturno_user'@'localhost';
FLUSH PRIVILEGES;

-- Verify
SHOW GRANTS FOR 'vetturno_user'@'localhost';
USE vetturno;
SHOW TABLES;
