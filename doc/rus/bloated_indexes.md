# Проверка раздувания индексов в таблицах

## Почему раздуваются индексы

Частое обновление данных может привести к раздуванию индекса по той же причине, что и
к [раздуванию таблицы](bloated_tables.md)

## Почему нужно следить за раздуванием индексов

Если индекс раздувается, то снижается производительность (приходится читать больше страниц с диска).

## SQL запрос

- [bloated_indexes.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/bloated_indexes.sql)

## Тип проверки

- **runtime** (требует наличия накопленной статистики)

## Поддержка секционированных таблиц

Не работает для секционированных таблиц. [Тикет на исправление](https://github.com/mfvanek/pg-index-health/issues/589)

## Как работает эта проверка

Для выполнения запроса пользователю необходимы права на чтение проверяемых таблиц.

### Принцип работы

Выполняется SQL-запрос к таблицам системной схемы pg_catalog. Они содержат статистическую информацию об основных объектах:
таблицах, индексах, столбцах.

Сначала в запросе собираются данные о B-tree индексах в указанной схеме. Каждый столбец индекса рассматривается отдельно. Индексы связываются с соответствующими столбцами таблицы. Собранные параметры позволяют оценить количество страниц, которое должно быть использовано индексом. Оно сравнивается с фактическим количеством страниц. Учитываются те индексы, по которым доступна статистика.
После этого вычисляется разница между фактическим и оцененным количеством страниц индекса и по ней процент раздутости индекса. 
Если он превышает заданное значение (дефолтное составляет 10%), то индекс считается раздутым. Результаты сортируются по имени таблицы и имени индекса.

# Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo."client_with_bloat"
(
    id bigint not null primary key,
    first_name text,
    last_name text
);

create table if not exists demo."account_with_bloat"
(
    id bigint not null primary key,
    account_number varchar(50) not null unique,
    client_id bigint not null references demo.client (id) on delete cascade
);

insert into demo."client_with_bloat" (id, first_name, last_name) values (generate_series(1, 1000),'first', 'last');

insert into demo."account_with_bloat" (id, account_number, client_id) values (generate_series(1, 1000),'account number', generate_series(1, 1000));

delete from demo."client_with_bloat" where id > 500;
```


