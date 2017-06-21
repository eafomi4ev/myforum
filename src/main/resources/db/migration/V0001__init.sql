--Поля с "__" - денормализованные

CREATE EXTENSION IF NOT EXISTS CITEXT;

CREATE TABLE IF NOT EXISTS users (
    id SERIAL NOT NULL PRIMARY KEY,
    nickname CITEXT NOT NULL UNIQUE,
    fullname CITEXT,
    email CITEXT NOT NULL UNIQUE,
    about TEXT
);
CREATE INDEX IF NOT EXISTS idx_users_nickname ON users (LOWER(nickname));

CREATE TABLE IF NOT EXISTS forums (
    id SERIAL NOT NULL PRIMARY KEY,
    title CITEXT NOT NULL,
    user_id INT NOT NULL,
    slug CITEXT NOT NULL UNIQUE,
    posts INTEGER DEFAULT 0,
    threads INTEGER DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_forums_user_id ON forums (user_id);
CREATE INDEX IF NOT EXISTS idx_forums_slug ON forums (LOWER(slug));


CREATE TABLE IF NOT EXISTS threads (
    id SERIAL NOT NULL PRIMARY KEY,
    title CITEXT NOT NULL,
    user_id INT NOT NULL,
    forum_id INT NOT NULL,
    message CITEXT NOT NULL,
    slug CITEXT UNIQUE,
    created TIMESTAMP DEFAULT NOW(),
    votes INT NOT NULL DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (forum_id) REFERENCES forums (id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_threads_user_id ON threads (user_id);
CREATE INDEX IF NOT EXISTS idx_threads_forum_id ON threads (forum_id);
CREATE INDEX IF NOT EXISTS idx_threads_slug ON threads (LOWER(slug));


CREATE TABLE IF NOT EXISTS votes (
    id SERIAL NOT NULL PRIMARY KEY,
    user_id INT NOT NULL,
    thread_id INT NOT NULL,
    voice SMALLINT,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (thread_id) REFERENCES threads (id) ON DELETE CASCADE,
    UNIQUE (user_id, thread_id)
);


CREATE TABLE IF NOT EXISTS posts (
    id SERIAL NOT NULL PRIMARY KEY,
    parent_id INT NOT NULL DEFAULT 0,
    user_id INT NOT NULL,
    forum_id INT NOT NULL,
    thread_id INT NOT NULL,

    is_edited BOOLEAN DEFAULT FALSE,
    message CITEXT NOT NULL,
    created TIMESTAMP DEFAULT NOW(),

    nickname CITEXT NOT NULL,
    path INT ARRAY,

    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (forum_id) REFERENCES forums (id) ON DELETE CASCADE,
    FOREIGN KEY (thread_id) REFERENCES threads (id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_posts_user_id ON posts (user_id);
CREATE INDEX IF NOT EXISTS idx_posts_forum_id ON posts (forum_id);
CREATE INDEX IF NOT EXISTS idx_posts_thread_id ON posts (thread_id);
CREATE INDEX IF NOT EXISTS idx_posts_created ON posts (created);
CREATE INDEX IF NOT EXISTS idx_posts_path1 ON posts ((path[1]));
CREATE INDEX IF NOT EXISTS idx_posts_parents ON posts(id, parent_id, thread_id);
CREATE INDEX IF NOT EXISTS idx_posts_getbyid ON posts (id, forum_id);
CREATE INDEX IF NOT EXISTS idx_posts_flat ON posts (thread_id, created, id);
CREATE INDEX IF NOT EXISTS idx_posts_tree ON posts (thread_id, path);


CREATE TABLE IF NOT EXISTS forum_users (
    user_id INT REFERENCES users(id) NOT NULL,
    forum_id INT REFERENCES forums(id) NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_forum_users_ui_user ON forum_users (user_id);
CREATE INDEX IF NOT EXISTS idx_forum_users_fi_forum ON forum_users (forum_id);


CREATE OR REPLACE FUNCTION add_forum_users() RETURNS TRIGGER AS '
  BEGIN
    INSERT INTO forum_users (user_id, forum_id) VALUES (NEW.user_id, NEW.forum_id);
    RETURN NEW;
  END;
' LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS post_insert_trigger ON posts;
CREATE TRIGGER post_insert_trigger AFTER INSERT ON posts
FOR EACH ROW EXECUTE PROCEDURE add_forum_users();

DROP TRIGGER IF EXISTS thread_insert_trigger ON threads;
CREATE TRIGGER thread_insert_trigger AFTER INSERT ON threads
FOR EACH ROW EXECUTE PROCEDURE add_forum_users();


-- DROP TABLE IF EXISTS posts;
-- DROP TABLE IF EXISTS votes;
-- DROP TABLE IF EXISTS threads;
-- DROP TABLE IF EXISTS forums;
-- DROP TABLE IF EXISTS users;
--
-- CREATE EXTENSION IF NOT EXISTS citext;
--
-- CREATE SEQUENCE IF NOT EXISTS public."Forum_id_seq"
-- INCREMENT 1
-- START 1
-- MINVALUE 1
-- MAXVALUE 9223372036854775807
-- CACHE 1;
--
-- CREATE SEQUENCE IF NOT EXISTS public.posts_id_seq
-- INCREMENT 1
-- START 1
-- MINVALUE 1
-- MAXVALUE 9223372036854775807
-- CACHE 1;
--
-- CREATE SEQUENCE IF NOT EXISTS public.thread_id_seq
-- INCREMENT 1
-- START 1
-- MINVALUE 1
-- MAXVALUE 9223372036854775807
-- CACHE 1;
--
-- CREATE SEQUENCE IF NOT EXISTS public.users_id_seq
-- INCREMENT 1
-- START 1
-- MINVALUE 1
-- MAXVALUE 9223372036854775807
-- CACHE 1;
--
-- CREATE TABLE forums
-- (
--     id INTEGER DEFAULT nextval('"Forum_id_seq"'::regclass) PRIMARY KEY NOT NULL,
--     title TEXT NOT NULL,
--     user_nick CITEXT NOT NULL,
--     slug CITEXT NOT NULL,
--     posts INTEGER DEFAULT 0,
--     threads INTEGER DEFAULT 0
-- );
--
--
-- COMMENT ON COLUMN forums.title IS 'Название форума.';
-- COMMENT ON COLUMN forums.user_nick IS 'Nickname пользователя, который отвечает за форум (уникальное поле).';
-- COMMENT ON COLUMN forums.slug IS 'Человекопонятный URL (https://ru.wikipedia.org/wiki/%D0%A1%D0%B5%D0%BC%D0%B0%D0%BD%D1%82%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%B8%D0%B9_URL).';
-- COMMENT ON COLUMN forums.posts IS 'Общее кол-во сообщений в данном форуме.';
-- COMMENT ON COLUMN forums.threads IS 'Общее кол-во ветвей обсуждения в данном форуме.';
--
--
-- CREATE TABLE posts
-- (
--     id INTEGER DEFAULT nextval('posts_id_seq'::regclass) PRIMARY KEY NOT NULL,
--     parent INTEGER,
--     author CITEXT NOT NULL,
--     message TEXT NOT NULL,
--     isedited BOOLEAN DEFAULT false,
--     forum CITEXT NOT NULL,
--     thread INTEGER,
--     created TIMESTAMP DEFAULT now() NOT NULL
-- );
--
--
-- COMMENT ON COLUMN posts.parent IS 'Идентификатор родительского сообщения (0 - корневое сообщение обсуждения)';
-- COMMENT ON COLUMN posts.message IS 'сообщение форума';
-- COMMENT ON COLUMN posts.isedited IS 'Истина, если данное сообщение было изменено';
-- COMMENT ON COLUMN posts.forum IS 'Идентификатор форума (slug) данного сообещния';
-- COMMENT ON COLUMN posts.created IS 'Дата создания сообщения на форуме';
--
--
-- CREATE TABLE threads
-- (
--     id INTEGER DEFAULT nextval('thread_id_seq'::regclass) PRIMARY KEY NOT NULL,
--     title CITEXT NOT NULL,
--     author CITEXT NOT NULL,
--     forum CITEXT,
--     message TEXT NOT NULL,
--     votes INTEGER DEFAULT 0,
--     slug CITEXT,
--     created TIMESTAMP WITH TIME ZONE DEFAULT now()
-- );
--
--
-- COMMENT ON COLUMN threads.title IS 'Заголовок ветки обсуждения.';
-- COMMENT ON COLUMN threads.author IS 'nickname пользователя, создавшего данную тему.';
-- COMMENT ON COLUMN threads.forum IS 'Форум, в котором расположена данная ветка обсуждения.';
-- COMMENT ON COLUMN threads.message IS 'Описание ветки обсуждения.';
-- COMMENT ON COLUMN threads.votes IS 'Кол-во голосов непосредственно за данное сообщение форума.';
-- COMMENT ON COLUMN threads.slug IS 'Человекопонятный URL ';
-- COMMENT ON COLUMN threads.created IS 'Дата создания ветки на форуме.';
--
--
-- CREATE TABLE users
-- (
--     id INTEGER DEFAULT nextval('users_id_seq'::regclass) PRIMARY KEY NOT NULL,
--     nickname CITEXT NOT NULL UNIQUE,
--     fullname TEXT NOT NULL,
--     about TEXT,
--     email CITEXT NOT NULL
-- );
--
--
-- COMMENT ON COLUMN users.nickname IS 'Имя пользователя (уникальное поле). Данное поле допускает только латиницу, цифры и знак подчеркивания. Сравнение имени регистронезависимо.';
-- COMMENT ON COLUMN users.fullname IS 'Полное имя пользователя.';
-- COMMENT ON COLUMN users.about IS 'Описание пользователя.';
-- COMMENT ON COLUMN users.email IS 'Почтовый адрес пользователя (уникальное поле).';
-- COMMENT ON COLUMN users.id IS 'id юзера';
--
--
-- CREATE TABLE votes
-- (
--     nickname CITEXT NOT NULL,
--     voice SMALLINT NOT NULL,
--     thread_id INTEGER
-- );
-- COMMENT ON COLUMN votes.nickname IS 'Ник пользователя';
-- COMMENT ON COLUMN votes.voice IS 'Отданный голос.';
--
--
-- ALTER TABLE forums ADD FOREIGN KEY (user_nick) REFERENCES users (nickname);
-- CREATE UNIQUE INDEX forums_slug_uindex ON forums (slug);
-- ALTER TABLE posts ADD FOREIGN KEY (author) REFERENCES users (nickname);
-- ALTER TABLE posts ADD FOREIGN KEY (forum) REFERENCES forums (slug);
-- CREATE UNIQUE INDEX posts_id_uindex ON posts (id);
-- ALTER TABLE threads ADD FOREIGN KEY (author) REFERENCES users (nickname);
-- ALTER TABLE threads ADD FOREIGN KEY (forum) REFERENCES forums (slug);
-- CREATE UNIQUE INDEX "Thread_id_uindex" ON threads (id);
-- CREATE UNIQUE INDEX thread_author_title_uindex ON threads (author, title);
-- CREATE UNIQUE INDEX "User_nickname_uindex" ON users (nickname);
-- CREATE UNIQUE INDEX "User_email_uindex" ON users (email);
-- CREATE UNIQUE INDEX users_id_uindex ON users (id);
-- ALTER TABLE votes ADD FOREIGN KEY (nickname) REFERENCES users (nickname);
-- ALTER TABLE votes ADD FOREIGN KEY (thread_id) REFERENCES threads (id);
-- CREATE UNIQUE INDEX table_name_nickname_uindex ON votes (nickname);