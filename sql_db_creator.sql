CREATE TABLE "Forum"
(
    id INTEGER PRIMARY KEY NOT NULL,
    title TEXT NOT NULL,
    "user" TEXT NOT NULL,
    slug TEXT NOT NULL,
    posts INTEGER,
    threads INTEGER,
    CONSTRAINT forum___fk_user FOREIGN KEY ("user") REFERENCES "User" (nickname)
);
COMMENT ON COLUMN "Forum".title IS 'Название форума.';
COMMENT ON COLUMN "Forum"."user" IS 'Nickname пользователя, который отвечает за форум (уникальное поле).';
COMMENT ON COLUMN "Forum".slug IS 'Человекопонятный URL (https://ru.wikipedia.org/wiki/%D0%A1%D0%B5%D0%BC%D0%B0%D0%BD%D1%82%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%B8%D0%B9_URL).';
COMMENT ON COLUMN "Forum".posts IS 'Общее кол-во сообщений в данном форуме.';
COMMENT ON COLUMN "Forum".threads IS 'Общее кол-во ветвей обсуждения в данном форуме.';


CREATE TABLE "User"
(
    nickname TEXT PRIMARY KEY NOT NULL,
    fullname TEXT NOT NULL,
    about TEXT,
    email TEXT NOT NULL
);
COMMENT ON COLUMN "User".nickname IS 'Имя пользователя (уникальное поле). Данное поле допускает только латиницу, цифры и знак подчеркивания. Сравнение имени регистронезависимо.';
COMMENT ON COLUMN "User".fullname IS 'Полное имя пользователя.';
COMMENT ON COLUMN "User".about IS 'Описание пользователя.';
COMMENT ON COLUMN "User".email IS 'Почтовый адрес пользователя (уникальное поле).';
CREATE UNIQUE INDEX "User_nickname_uindex" ON "User" (nickname);
CREATE UNIQUE INDEX "User_email_uindex" ON "User" (email);
