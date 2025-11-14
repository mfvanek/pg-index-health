# Проверка наличия ограничений, которые не были провалидированы

## Почему данные могут не соответствовать существующим ограничениям

- [Источник](https://habr.com/ru/articles/800121/)

Некоторые типы ограничений (в настоящее время это ограничение-проверка `CHECK` и, с некоторыми оговорками, `FOREIGN KEY`),
могут создаваться с ключом `NOT VALID`.
При создании ограничений для больших таблиц проверка уже имеющихся данных может быть длительной,
поэтому разработчики пользуются удобным механизмом в PostgreSQL и разделяют процессы создания ограничения и проверки всех данных.

Важно отметить, что ограничение сразу после создания будет действовать при добавлении или изменении данных,
и любая из этих операций будет прервана, если новые данные не соответствуют ограничению.
Однако на уровне базы данных будет установлен признак, что ограничение не было проверено для всех данных,
пока оно не будет проверено командой `VALIDATE CONSTRAINT`.

## SQL запрос

- [not_valid_constraints.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/not_valid_constraints.sql)

## Тип проверки

- **runtime** (имеет смысл запускать на работающем инстансе БД после выполнения миграций)
- **static** (может выполняться в компонентных\интеграционных тестах для проверки корректности миграций)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo."client_with_not_valid"
(
    id bigint not null primary key,
    first_name text,
    last_name text
);

alter table if exists demo."client_with_not_valid"
                add constraint last_name_not_validated_yet
                check (last_name != '') not valid;

create table if not exists demo."account_with_not valid_partitioned"
(
    id bigint not null primary key,
    account_number varchar(50) not null unique,
    client_id bigint not null references demo.client (id) on delete cascade
) partition by hash (account_number);

alter table if exists demo."client_with_not_valid"
                add constraint account_number_not_validated_yet
                check (account_number != '') not valid;

create table if not exists demo."account_with_not valid_partitioned_hash_p0"
    partition of demo."account_with_not valid_partitioned"
    for values with (modulus 4, remainder 0);
    
insert into demo."client_with_not_valid" (id, first_name, last_name) values (generate_series(1, 20),'first', 'last');
insert into demo."client_with_not_valid" (id, first_name, last_name) values (21,'firstn', '');

insert into demo."account_with_not valid_partitioned" (id, account_number, client_id) values (generate_series(1, 20), account_number, generate_series(1, 20));
insert into demo."account_with_not valid_partitioned" (id, account_number, client_id) values (21, '', 21);
```
