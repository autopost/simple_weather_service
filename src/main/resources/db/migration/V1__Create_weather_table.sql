CREATE TABLE weather (
    id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    city VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    temperature NUMERIC(5, 2)
);