# Table needed

## Brief

- Date
- Event
- Tag

## Detail

### Date

Item | Description
----- |-----
date_id | key
event_date | date (YYYYMMDD)

```sql
CREATE TABLE LogDate (
    id_date     INT PRIMARY KEY     NOT NULL,
    event_date  DATE                NOT NULL
);
```

### Event

Item | Description
----- |-----
event_id | key
date_id | connector to Date
time_start | time (hhmm)
time_end | time (hhmm)

```sql
CREATE TABLE LogEvent (
    id_event            INT PRIMARY KEY     NOT NULL,
    id_date             INT                 NOT NULL,
    event_name          CHAR(50)            NOT NULL,
    time_start_hour     INT                 NOT NULL,
    time_start_minute   INT                 NOT NULL
    time_end_hour       INT                 NOT NULL,
    time_end_minute     INT                 NOT NULL,
    FOREIGN KEY (id_date) REFERENCES LogDate(id_date)
    ON DELETE CASCADE
);
```

### Tag

Item | Description
----- |-----
tag_id | key
event_id | connector to Event
name | string
type | int (enum?)

```sql
CREATE TABLE Tag (
    id_tag      INT PRIMARY KEY     NOT NULL,
    id_event    INT                 NOT NULL,
    tag_name    CHAR(50)            NOT NULL,
    tag_type    INT                 NOT NULL,
    FOREIGN KEY (id_event) REFERENCES LogEvent(id_event)
    ON DELETE CASCADE
);
```

## Operation

### Create

```sql
-- format of date
-- 'YYYY-MM-DD'
-- where single quote is needed

----- create date -----
-- column of primary use auto-increment and just ignore this column
INSERT INTO LogDate (event_date) 
VALUES ($(date));

----- create event -----
INSERT INTO LogEvent (id_date, event_name, time_start_hour, time_start_minute, time_end_hour, time_end_minute)
VALUES (_, _, _, _, _, _);

----- create tag -----
INSERT INTO Tag (id_event, tag_name, tag_type)
VALUES (_, _, _);
```

### Get

Get ID

```sql
----- get date id -----
SELECT id_date FROM LogDate
WHERE event_date = $(event_date);

------ get event id -----
SELECT id_event FROM LogEvent
WHERE id_date = $(id_date);
```

Get Item

```sql
----- get event list -----
SELECT event_name, time_start_hour, time_start_minute, time_end_hour, time_end_minute FROM LogEvent
WHERE id_date = $(id_date);

----- get tag list -----
SELECT tag_name, tag_type FROM Tag
WHERE id_event = $(id_event);
```

### Destroy

```sql
----- destroy date -----
DELETE FROM LogDate WHERE id_date = $(id_date);
DELETE FROM LogDate WHERE event_date = $(event_date);

----- destroy event -----
DELETE FROM LogEvent WHERE id_event = $(id_event);

----- destroy tag -----
DELETE FROM Tag WHERE id_tag = $(id_tag);
```

## Reference

[PostgreSQL - CreateTable - TutorialsPoint](https://www.tutorialspoint.com/postgresql/postgresql_create_table.htm)
