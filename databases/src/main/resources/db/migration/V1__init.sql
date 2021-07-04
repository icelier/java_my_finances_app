CREATE TABLE IF NOT EXISTS categories (
id              SERIAL,
title           VARCHAR(50) NOT NULL,
UNIQUE (title),
PRIMARY KEY (id)
);

INSERT INTO categories (title) VALUES ("utility bills"), ("loan payment"),
("food"), ("pet food"), ("children"),
("gasoline"), ("drugs"), ("medical services"),
("clothes"), ("sport"), ("restaurants"), ("entertainment");

CREATE TABLE IF NOT EXISTS account_types (
id              SERIAL,
title           VARCHAR(50) NOT NULL,
UNIQUE (title),
PRIMARY KEY (id)
);

INSERT INTO account_types (title) VALUES ("cash"), ("salary card"),
("credit card"), ("bank loan"), ("borrowed money"),
("deposit");

CREATE TABLE IF NOT EXISTS users (
id              SERIAL,
username        VARCHAR(50) NOT NULL,
password        VARCHAR(80) NOT NULL,
fullname        VARCHAR(100) NOT NULL,
age             INT,
email           VARCHAR(50) DEFAULT "",
UNIQUE (username),
PRIMARY KEY (id)
);

INSERT INTO users (username, password, fullname, age, email) VALUES
('daddy', '$2y$12$LSBYHvx/ACO5GQQfDyWrnudJvpjeqCLVNiPdIto.a0lFwiM15gqAS',
'Ivan Sokolov', 55, 'sokol@gmail.com'),
('mommy', '$2y$12$rtn8PcIVEhnV5F9donGdnuhp/nKED8.kqeeLtvq5seqtr/cMK4B.6',
'Elena Sokolova', 52, 'mom@gmail.com');

CREATE TABLE IF NOT EXISTS accounts (
id              SERIAL,
type_id         INT NOT NULL,
user_id         INT NOT NULL,
bank            VARCHAR(30) DEFAULT NULL,
total           NUMERIC(15, 2) NOT NULL DEFAULT 0,
PRIMARY KEY (id),
CONSTRAINT FK_USER_ID FOREIGN KEY (user_id)
REFERENCES users (id)
ON DELETE NO ACTION ON UPDATE CASCADE,
CONSTRAINT FK_TYPE_ID FOREIGN KEY (type_id)
REFERENCES account_types (id)
ON DELETE NO ACTION ON UPDATE CASCADE
);

INSERT INTO accounts (type_id, user_id, bank, total) VALUES (2, 1, "PSB", 150000),
(2, 2, "NOVIKOMBANK", 26500), (2, 3, "NOVIKOMBANK", 110000);

CREATE TYPE operation AS ENUM ('debet', 'credit');

CREATE TABLE IF NOT EXISTS transactions (
id              SERIAL,
transfer        NUMERIC(15, 2) NOT NULL,
type            operation,
account_id      INT NOT NULL,
category_id     INT NOT NULL,
ts              TIMESTAMP NOT NULL,
PRIMARY KEY (id),
CONSTRAINT FK_ACCOUNT_ID FOREIGN KEY (account_id)
REFERENCES accounts (id)
ON DELETE NO ACTION ON UPDATE CASCADE,
CONSTRAINT FK_CATEGORY_ID FOREIGN KEY (category_id)
REFERENCES categories (id)
ON DELETE NO ACTION ON UPDATE CASCADE
);

INSERT INTO transactions (transfer, type, account_id, category_id, ts)
VALUES (2000, 'CREDIT', 2, 3, '2021-06-22 19:10:25');

CREATE TABLE IF NOT EXISTS roles (
    id                      serial,
    name                    VARCHAR(50) DEFAULT NULL,
    PRIMARY KEY (id)
);
INSERT INTO roles (name)
VALUES
('ROLE_USER'),
('ROLE_ADMIN');

CREATE TABLE IF NOT EXISTS users_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),

    CONSTRAINT FK_USER_ID FOREIGN KEY (user_id)
    REFERENCES users (id)
    ON DELETE NO ACTION ON UPDATE NO ACTION,

    CONSTRAINT FK_ROLE_ID FOREIGN KEY (role_id)
    REFERENCES roles (id)
    ON DELETE NO ACTION ON UPDATE NO ACTION
);
INSERT INTO users_roles
VALUES
(1, 1),
(1, 2),
(2, 1);