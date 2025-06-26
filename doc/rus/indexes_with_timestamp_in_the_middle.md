# Проверка наличия индексов, в которых столбцы с типом timestamp не являются последними в списке

## В чём проблема?

Всегда выглядит подозрительно, если в вашем индексе поле с заведомо большой вариативностью типа timestamp\timestamptz стоит не последним.
Как правило, значения timestamp-поля монотонно возрастают, а следующие поля индекса имеют только одно значение в каждой временной точке.
Подробности [в статье на Хабре](https://habr.com/ru/companies/tensor/articles/488104/).

При создании составного B-tree индекса используйте [правило ESR](https://habr.com/ru/articles/911688/) (Equality/Sort/Range):
- **E**quality: сначала колонки, по которым в запросе стоит `=`;
- **S**ort: затем те, по которым идёт сортировка `order by`;
- **R**ange: и только в конце — колонки с диапазонами `>, <, between`.

## SQL запрос

- [indexes_with_timestamp_in_the_middle.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/indexes_with_timestamp_in_the_middle.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo."t-multi"
(
    id   int primary key,
    ts   timestamp,
    name text
);

create index idx_multi_mid on demo."t-multi" (id, ts, name);
create index idx_multi_end on demo."t-multi" (id, name, ts);
create index idx_multi_none on demo."t-multi" (id, name);

create index idx_multi_expr_mid on demo."t-multi" (id, date_trunc('day', ts), name);
create index idx_multi_expr_first on demo."t-multi" (date_trunc('day', ts), id, name);

create unique index idx_unique_ts on demo."t-multi" (id, ts, id);

create table if not exists demo.t_part_parent
(
    id       int,
    "ts-bad" timestamptz
) partition by range (id);

create table t_part_p1 partition of demo.t_part_parent for values from (0) to (100);
create table t_part_p2 partition of demo.t_part_parent for values from (100) to (200);

create index idx_part_parent_end on demo.t_part_parent (id, "ts-bad");

create index idx_part_parent_mid on demo.t_part_parent ("ts-bad", id);
```
