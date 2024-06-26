CREATE TABLE `book_shop`
(
    `id`     BIGINT    AUTO_INCREMENT,
    `city` varchar(64)    NOT NULL,
    `address`  varchar(128)   NOT NULL,
    `zip`  INT   NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
