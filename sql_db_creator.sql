CREATE TABLE forums
(
    id INTEGER DEFAULT nextval(''"Forum_id_seq"''::regclass) PRIMARY KEY NOT NULL,
    title TEXT NOT NULL,
    user_nick CITEXT NOT NULL,
    slug CITEXT NOT NULL,
    posts INTEGER DEFAULT 0,
    threads INTEGER DEFAULT 0,
    CONSTRAINT forums_users_nickname_fk FOREIGN KEY (user_nick) REFERENCES users (nickname)
);
COMMENT ON COLUMN forums.title IS ''Название форума.'';
COMMENT ON COLUMN forums.user_nick IS ''Nickname пользователя, который отвечает за форум (уникальное поле).'';
COMMENT ON COLUMN forums.slug IS ''Человекопонятный URL (https://ru.wikipedia.org/wiki/%D0%A1%D0%B5%D0%BC%D0%B0%D0%BD%D1%82%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%B8%D0%B9_URL).'';
COMMENT ON COLUMN forums.posts IS ''Общее кол-во сообщений в данном форуме.'';
COMMENT ON COLUMN forums.threads IS ''Общее кол-во ветвей обсуждения в данном форуме.'';
CREATE UNIQUE INDEX forums_slug_uindex ON forums (slug);
CREATE TABLE posts
(
    id INTEGER DEFAULT nextval(''posts_id_seq''::regclass) PRIMARY KEY NOT NULL,
    parent INTEGER,
    author CITEXT NOT NULL,
    message TEXT NOT NULL,
    isedited BOOLEAN DEFAULT false,
    forum CITEXT,
    thread INTEGER,
    created TIMESTAMP DEFAULT now(),
    CONSTRAINT posts_users_nickname_fk FOREIGN KEY (author) REFERENCES users (nickname),
    CONSTRAINT posts_forums_slug_fk FOREIGN KEY (forum) REFERENCES forums (slug),
    CONSTRAINT posts_threads_id_fk FOREIGN KEY (thread) REFERENCES threads (id)
);
COMMENT ON COLUMN posts.parent IS ''Идентификатор родительского сообщения (0 - корневое сообщение обсуждения)'';
COMMENT ON COLUMN posts.message IS ''сообщение форума'';
COMMENT ON COLUMN posts.isedited IS ''Истина, если данное сообщение было изменено'';
COMMENT ON COLUMN posts.forum IS ''Идентификатор форума (slug) данного сообещния'';
COMMENT ON COLUMN posts.created IS ''Дата создания сообщения на форуме'';
CREATE UNIQUE INDEX posts_id_uindex ON posts (id);
CREATE TABLE threads
(
    id INTEGER DEFAULT nextval(''thread_id_seq''::regclass) PRIMARY KEY NOT NULL,
    title CITEXT NOT NULL,
    author CITEXT NOT NULL,
    forum CITEXT,
    message TEXT NOT NULL,
    votes INTEGER DEFAULT 0,
    slug CITEXT,
    created TIMESTAMP WITH TIME ZONE DEFAULT now(),
    CONSTRAINT thread_users_nickname_fk FOREIGN KEY (author) REFERENCES users (nickname),
    CONSTRAINT thread_forums_slug_fk FOREIGN KEY (forum) REFERENCES forums (slug)
);
COMMENT ON COLUMN threads.title IS ''Заголовок ветки обсуждения.'';
COMMENT ON COLUMN threads.author IS ''nickname пользователя, создавшего данную тему.'';
COMMENT ON COLUMN threads.forum IS ''Форум, в котором расположена данная ветка обсуждения.'';
COMMENT ON COLUMN threads.message IS ''Описание ветки обсуждения.'';
COMMENT ON COLUMN threads.votes IS ''Кол-во голосов непосредственно за данное сообщение форума.'';
COMMENT ON COLUMN threads.slug IS ''Человекопонятный URL'';
COMMENT ON COLUMN threads.created IS ''Дата создания ветки на форуме.'';
CREATE UNIQUE INDEX "Thread_id_uindex" ON threads (id);
CREATE UNIQUE INDEX thread_author_title_uindex ON threads (author, title);
CREATE TABLE users
(
    nickname CITEXT NOT NULL,
    fullname TEXT NOT NULL,
    about TEXT,
    email CITEXT NOT NULL,
    id INTEGER DEFAULT nextval(''users_id_seq''::regclass) PRIMARY KEY NOT NULL
);
COMMENT ON COLUMN users.nickname IS ''Имя пользователя (уникальное поле). Данное поле допускает только латиницу, цифры и знак подчеркивания. Сравнение имени регистронезависимо.'';
COMMENT ON COLUMN users.fullname IS ''Полное имя пользователя.'';
COMMENT ON COLUMN users.about IS ''Описание пользователя.'';
COMMENT ON COLUMN users.email IS ''Почтовый адрес пользователя (уникальное поле).'';
COMMENT ON COLUMN users.id IS ''id юзера'';
CREATE UNIQUE INDEX "User_nickname_uindex" ON users (nickname);
CREATE UNIQUE INDEX "User_email_uindex" ON users (email);
CREATE UNIQUE INDEX users_id_uindex ON users (id);
CREATE FUNCTION citext(BOOLEAN) RETURNS CITEXT;
CREATE FUNCTION citext(CHAR) RETURNS CITEXT;
CREATE FUNCTION citext(INET) RETURNS CITEXT;
CREATE FUNCTION citext_cmp(CITEXT, CITEXT) RETURNS INTEGER;
CREATE FUNCTION citext_eq(CITEXT, CITEXT) RETURNS BOOLEAN;
CREATE FUNCTION citext_ge(CITEXT, CITEXT) RETURNS BOOLEAN;
CREATE FUNCTION citext_gt(CITEXT, CITEXT) RETURNS BOOLEAN;
CREATE FUNCTION citext_hash(CITEXT) RETURNS INTEGER;
CREATE FUNCTION citext_larger(CITEXT, CITEXT) RETURNS CITEXT;
CREATE FUNCTION citext_le(CITEXT, CITEXT) RETURNS BOOLEAN;
CREATE FUNCTION citext_lt(CITEXT, CITEXT) RETURNS BOOLEAN;
CREATE FUNCTION citext_ne(CITEXT, CITEXT) RETURNS BOOLEAN;
CREATE FUNCTION citext_smaller(CITEXT, CITEXT) RETURNS CITEXT;
CREATE FUNCTION citextin(CSTRING) RETURNS CITEXT;
CREATE FUNCTION citextout(CITEXT) RETURNS CSTRING;
CREATE FUNCTION citextrecv(INTERNAL) RETURNS CITEXT;
CREATE FUNCTION citextsend(CITEXT) RETURNS BYTEA;
CREATE FUNCTION max(CITEXT) RETURNS CITEXT;
CREATE FUNCTION min(CITEXT) RETURNS CITEXT;
CREATE FUNCTION regexp_matches(CITEXT, CITEXT) RETURNS SETOF TEXT[];
CREATE FUNCTION regexp_matches(CITEXT, CITEXT, TEXT) RETURNS SETOF TEXT[];
CREATE FUNCTION regexp_replace(CITEXT, CITEXT, TEXT) RETURNS TEXT;
CREATE FUNCTION regexp_replace(CITEXT, CITEXT, TEXT, TEXT) RETURNS TEXT;
CREATE FUNCTION regexp_split_to_array(CITEXT, CITEXT) RETURNS TEXT[];
CREATE FUNCTION regexp_split_to_array(CITEXT, CITEXT, TEXT) RETURNS TEXT[];
CREATE FUNCTION regexp_split_to_table(CITEXT, CITEXT) RETURNS SETOF TEXT;
CREATE FUNCTION regexp_split_to_table(CITEXT, CITEXT, TEXT) RETURNS SETOF TEXT;
CREATE FUNCTION replace(CITEXT, CITEXT, CITEXT) RETURNS TEXT;
CREATE FUNCTION split_part(CITEXT, CITEXT, INTEGER) RETURNS TEXT;
CREATE FUNCTION strpos(CITEXT, CITEXT) RETURNS INTEGER;
CREATE FUNCTION texticlike(CITEXT, CITEXT) RETURNS BOOLEAN;
CREATE FUNCTION texticlike(CITEXT, TEXT) RETURNS BOOLEAN;
CREATE FUNCTION texticnlike(CITEXT, CITEXT) RETURNS BOOLEAN;
CREATE FUNCTION texticnlike(CITEXT, TEXT) RETURNS BOOLEAN;
CREATE FUNCTION texticregexeq(CITEXT, CITEXT) RETURNS BOOLEAN;
CREATE FUNCTION texticregexeq(CITEXT, TEXT) RETURNS BOOLEAN;
CREATE FUNCTION texticregexne(CITEXT, CITEXT) RETURNS BOOLEAN;
CREATE FUNCTION texticregexne(CITEXT, TEXT) RETURNS BOOLEAN;
CREATE FUNCTION translate(CITEXT, CITEXT, TEXT) RETURNS TEXT;
;