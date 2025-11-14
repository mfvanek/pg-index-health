# Проверка дублирующихся внешних ключей в таблицах

## Как они появляются и почему нужно избавляться от дублирующихся внешних ключей

Внешние ключи могут быть созданы по нескольким атрибутам целевой таблицы - ссылка на ограничение по нескольким столбцам.
При этом возможна ошибка - создание другого внешнего ключа с такими же атрибутами.
Дублирование сущностей увеличивает когнитивную сложность и затрудняет поддержку и развитие схемы данных в будущем.

## SQL запрос

- [duplicated_foreign_keys.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/duplicated_foreign_keys.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo.client
(
    id bigint not null primary key,
    first_name text,
    last_name text
);

create table if not exists demo."account_with_dublicated_fk"
(
    id bigint not null primary key,
    account_number varchar(50) not null unique,
    client_id bigint not null references demo.client (id),
    created timestamp with time zone not null
);

alter table if exists demo."account_with_dublicated_fk"
    add constraint account_fk_client_id_duplicate
    foreign key (client_id) references demo.client (id);

create table if not exists demo."account_with_dublicated_fk_partitioned"
(
    id bigint not null primary key,
    account_number varchar(50) not null unique,
    client_id bigint not null references demo.client (id),
    created timestamp with time zone not null
) partition by range (created);

alter table if exists demo."account_with_dublicated_fk_partitioned"
    add constraint account_p_fk_client_id_duplicate
    foreign key (client_id) references demo.client (id);

create table if not exists demo."account_with_dublicated_fk_partitioned_Q3"
    partition of demo."account_without_fk_partitioned"
    for values from ('2025-07-01') to ('2025-10-01');
```
