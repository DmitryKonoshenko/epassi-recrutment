CREATE TABLE `books_book_shop`
(
    `id`     BIGINT    AUTO_INCREMENT,
    `book_shop_id`     BIGINT    NOT NULL,
    `book_id`     varchar(36)    NOT NULL,
    `count` BIGINT    NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


ALTER TABLE `books_book_shop`
    ADD FOREIGN KEY (book_shop_id)
        REFERENCES book_shop(id);

ALTER TABLE `books_book_shop`
    ADD FOREIGN KEY (book_id)
    REFERENCES books(id);