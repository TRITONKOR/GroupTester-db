DROP TABLE IF EXISTS test_tag CASCADE ;
DROP TABLE IF EXISTS results CASCADE;
DROP TABLE IF EXISTS answers CASCADE;
DROP TABLE IF EXISTS questions CASCADE;
DROP TABLE IF EXISTS reports CASCADE;
DROP TABLE IF EXISTS tests CASCADE;
DROP TABLE IF EXISTS tags CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users (
    PRIMARY KEY (id),
    id          UUID            NOT NULL,
    username    VARCHAR(128)     NOT NULL UNIQUE,
    password    VARCHAR(128)     NOT NULL,
    email       VARCHAR(128)     NOT NULL UNIQUE,
    birthday    DATE            NOT NULL,
    avatar      BYTEA               NULL,
    role        VARCHAR(16)     NOT NULL
);

CREATE TABLE tags(
    PRIMARY KEY (id),
    id UUID NOT NULL ,
    name VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE tests (
    PRIMARY KEY (id),
    id              UUID        NOT NULL,
    title           VARCHAR(64) NOT NULL UNIQUE,
    owner_id           UUID        NOT NULL,
                    FOREIGN KEY (owner_id) REFERENCES users(id)
                    ON UPDATE CASCADE
                    ON DELETE CASCADE,
    create_date     TIMESTAMP   NOT NULL
);

CREATE TABLE questions(
    PRIMARY KEY (id),
    id          UUID            NOT NULL,
    test_id        UUID            NOT NULL,
                FOREIGN KEY (test_id) REFERENCES tests(id)
                ON UPDATE CASCADE
                ON DELETE CASCADE,
    text        VARCHAR (256)   NOT NULL
);

CREATE TABLE answers (
    PRIMARY KEY (id),
    id          UUID            NOT NULL,
    question_id    UUID            NOT NULL,
                FOREIGN KEY (question_id) REFERENCES questions(id)
                ON UPDATE CASCADE
                ON DELETE CASCADE,
    text        VARCHAR (256)   NOT NULL,
    is_correct  BOOLEAN         NOT NULL
);

CREATE TABLE reports (
    PRIMARY KEY (id),
    id          UUID            NOT NULL,
    owner_id       UUID         NOT NULL,
                FOREIGN KEY (owner_id) REFERENCES users(id)
                ON UPDATE CASCADE
                ON DELETE CASCADE,
    test_id        UUID         NOT NULL,
                FOREIGN KEY (test_id) REFERENCES tests(id)
                ON UPDATE CASCADE
                ON DELETE CASCADE,
    create_date TIMESTAMP
);

CREATE TABLE results (
    PRIMARY KEY (id),
    id          UUID            NOT NULL,
    test_id        UUID         NOT NULL,
                FOREIGN KEY (test_id) REFERENCES tests(id)
                ON UPDATE CASCADE
                ON DELETE CASCADE,
    owner_id       UUID         NOT NULL,
                FOREIGN KEY (owner_id) REFERENCES users(id)
                ON UPDATE CASCADE
                ON DELETE CASCADE,
    report_id      UUID         NOT NULL,
                FOREIGN KEY (report_id) REFERENCES reports(id)
                ON UPDATE CASCADE
                ON DELETE CASCADE,
    mark        INT             NOT NULL,
    create_date TIMESTAMP
);


CREATE TABLE test_tag(
    test_id UUID NOT NULL,
    tag_id  UUID NOT NULL,
    PRIMARY KEY(test_id, tag_id),
    CONSTRAINT fk_test_tag_tests FOREIGN KEY(test_id) REFERENCES tests(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_test_tag_tags FOREIGN KEY(tag_id) REFERENCES tags(id) ON DELETE CASCADE ON UPDATE CASCADE
);
