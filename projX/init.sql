DROP USER IF EXISTS 'Evera_staff'@'localhost';

CREATE USER IF NOT EXISTS 'Evera_staff'@'%' IDENTIFIED BY 'senha1234';
GRANT ALL PRIVILEGES ON Evera.* TO 'Evera_staff'@'%';
FLUSH PRIVILEGES;