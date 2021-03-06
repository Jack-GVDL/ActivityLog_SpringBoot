-- CREATE TABLE LogDate (
--     id_date     INT PRIMARY KEY     NOT NULL,
--     event_date  DATE                NOT NULL
-- );
--
--
-- CREATE TABLE LogEvent (
--     id_event            INT PRIMARY KEY     NOT NULL,
--     id_date             INT                 NOT NULL,
--     event_name          CHAR(50)            NOT NULL,
--     time_start_hour     INT                 NOT NULL,
--     time_start_minute   INT                 NOT NULL,
--     time_end_hour       INT                 NOT NULL,
--     time_end_minute     INT                 NOT NULL,
--     FOREIGN KEY (id_date) REFERENCES LogDate(id_date)
--     ON DELETE CASCADE
-- );
--
--
-- CREATE TABLE Tag (
--     id_tag      INT PRIMARY KEY     NOT NULL,
--     id_event    INT                 NOT NULL,
--     tag_name    CHAR(50)            NOT NULL,
--     tag_type    INT                 NOT NULL,
--     FOREIGN KEY (id_event) REFERENCES LogEvent(id_event)
--     ON DELETE CASCADE
-- );
