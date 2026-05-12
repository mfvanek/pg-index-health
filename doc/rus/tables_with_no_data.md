# Проверка наличия таблиц без данных

Находит таблицы, которые не содержат данных, на основании статистики хранения из `pg_catalog`.

Для **обычных таблиц** признаком отсутствия данных служит `relpages = 0` — то есть ни одна страница данных не была выделена.

Для **секционированных таблиц** значение `relpages` родительской таблицы всегда равно нулю (данные хранятся только в секциях),
поэтому проверка использует `pg_partition_tree()` для суммирования `relpages` по всем листовым секциям.
Секционированная таблица считается пустой только в том случае, если все её листовые секции не имеют выделенных страниц данных.

> **Примечание:** таблицы, из которых были удалены строки, но ещё не выполнена операция `VACUUM`, будут иметь `relpages > 0` и не будут обнаружены данной проверкой.

Схожий подход используется в [SchemaCrawler `LinterTableEmpty`](https://www.schemacrawler.com/lint.html).

## SQL запрос

- [tables_with_no_data.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/tables_with_no_data.sql)

## Тип проверки

- **runtime** (требует накопленной статистики)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка агрегирует статистику хранения по всем листовым секциям с помощью `pg_partition_tree()`.
Отдельные секции (потомки) не проверяются независимо.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

-- Regular tables

create table demo.regular_empty
(
    id  bigint primary key,
    val text
);

create table demo.regular_with_data
(
    id  bigint primary key,
    val text
);
insert into demo.regular_with_data (id, val) values (1, 'hello');

create table demo.regular_deleted_no_vacuum
(
    id  bigint primary key,
    val text
);
insert into demo.regular_deleted_no_vacuum (id, val) values (1, 'to be deleted');
analyze demo.regular_deleted_no_vacuum;
delete from demo.regular_deleted_no_vacuum;
-- vacuum analyze demo.regular_deleted_no_vacuum;

-- Single-level partitioned tables

create table demo.partitioned_no_parts
(
    id  bigint,
    val text
) partition by range (id);

create table demo.partitioned_empty_parts
(
    id  bigint,
    val text
) partition by range (id);

create table demo.partitioned_empty_parts_p1 partition of demo.partitioned_empty_parts for values from (1) to (100);
create table demo.partitioned_empty_parts_p2 partition of demo.partitioned_empty_parts for values from (100) to (200);

create table demo.partitioned_with_data
(
    id  bigint,
    val text
) partition by range (id);

create table demo.partitioned_with_data_p1 partition of demo.partitioned_with_data for values from (1) to (100);
create table demo.partitioned_with_data_p2 partition of demo.partitioned_with_data for values from (100) to (200);

insert into demo.partitioned_with_data (id, val) values (50, 'hello');
analyze demo.partitioned_with_data;

-- Two-level partitioned tables (sub-partitioning)

create table demo.two_level_empty
(
    id     bigint,
    region text,
    val    text
) partition by range (id);

create table demo.two_level_empty_2020 partition of demo.two_level_empty for values from (1) to (1000) partition by list (region);
create table demo.two_level_empty_2020_us partition of demo.two_level_empty_2020 for values in ('us');
create table demo.two_level_empty_2020_eu partition of demo.two_level_empty_2020 for values in ('eu');

create table demo.two_level_with_data
(
    id     bigint,
    region text,
    val    text
) partition by range (id);

create table demo.two_level_with_data_2020 partition of demo.two_level_with_data for values from (1) to (1000) partition by list (region);
create table demo.two_level_with_data_2020_us partition of demo.two_level_with_data_2020 for values in ('us');
create table demo.two_level_with_data_2020_eu partition of demo.two_level_with_data_2020 for values in ('eu');

insert into demo.two_level_with_data (id, region, val) values (50, 'us', 'hello');
analyze demo.two_level_with_data;
```

## Как исправить

Проверьте таблицы, выявленные данной проверкой, и определите, являются ли они намеренно пустыми.
Таблицы, которые были созданы, но никогда не заполнялись данными, могут указывать на мёртвые или неиспользуемые объекты схемы
и являться кандидатами на удаление.
