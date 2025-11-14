# Проверка наличия пересекающихся внешних ключей в таблицах

## Как появляются пересекающиеся внешние ключи и почему иногда нужно избавиться от них

Внешние ключи могут быть созданы по нескольким атрибутам (столбцам) целевой таблицы.
При изменении структуры данных может потребоваться новое ограничение в целевой таблице и новый внешний ключ в ссылающейся таблице.
Если устаревший внешний ключ останется, то это увеличивает когнитивную сложность и
затрудняет поддержку и развитие структуры данных в дальнейшем.

## SQL запрос

- [intersected_foreign_keys.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/intersected_foreign_keys.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.


# Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo."client_preferences_with_redundant_fk" (
id bigint not null generated always as identity,
email varchar(200) not null,
phone varchar(50) not null,
call_time_start timestamp with time zone not null,
call_time_end timestamp with time zone not null
);

create table if not exists demo.clients
(
    id bigint not null primary key,
    first_name text,
    last_name text,
    email varchar(64),
    phone varchar(15)
);

alter table if exists demo."client_preferences_with_redundant_fk"
add constraint c_client_preferences_email_phone_fk
foreign key (email, phone) references demo.clients (email, phone);

alter table if exists demo."client_preferences_with_redundant_fk"
add constraint c_client_preferences_phone_email_fk
foreign key (phone, email) references demo.clients (phone, email);

create table if not exists demo."client_preferences_with_redundant_fk_partitioned" (
id bigint not null generated always as identity,
email varchar(200) not null,
phone varchar(50) not null,
call_time_start timestamp with time zone not null,
call_time_end timestamp with time zone not null
) partition by range (id);

alter table if exists demo."client_preferences_with_redundant_fk_partitioned"
add constraint c_client_preferences_email_phone_fk
foreign key (email, phone) references demo.clients (email, phone);

alter table if exists demo."client_preferences_with_redundant_fk_partitioned"
add constraint c_client_preferences_phone_email_fk
foreign key (phone, email) references demo.clients (phone, email);

create table if not exists demo."client_preferences_with_redundant_fk_partitioned_1_1000"
    partition of demo."client_preferences_with_redundant_fk_partitioned"
    for values from (1) to (1000);
```
