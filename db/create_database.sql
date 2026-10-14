-- Run once as a MySQL/MariaDB admin. The app logs in with the database name as the user name
-- (that is how the login screen works), so the user and database share a name.
CREATE DATABASE IF NOT EXISTS safari;
CREATE USER IF NOT EXISTS 'safari'@'%' IDENTIFIED BY 'safari';   -- local demo password
GRANT ALL PRIVILEGES ON safari.* TO 'safari'@'%';
FLUSH PRIVILEGES;
