-- Execute in MySQL Workbench or mysql CLI as root:

CREATE DATABASE IF NOT EXISTS vetturno;

CREATE USER IF NOT EXISTS vetturno_user@localhost IDENTIFIED BY 'tu_contraseña_aquí';

GRANT ALL PRIVILEGES ON vetturno.* TO vetturno_user@localhost;

FLUSH PRIVILEGES;

-- Verify
SHOW GRANTS FOR vetturno_user@localhost;
USE vetturno;
SHOW TABLES;
