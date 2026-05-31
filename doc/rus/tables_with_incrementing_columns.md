# Проверка наличия таблиц с нумерованными именами колонок

Таблицы, в которых есть группы колонок с одинаковым базовым именем и числовым суффиксом
(например, `phone1`, `phone2`, `phone3` или `address1`, `address2`, `address3`) — признак денормализации:
несколько значений одного и того же понятия хранятся в виде отдельных колонок вместо того,
чтобы быть вынесенными в отдельную дочернюю таблицу, связанную внешним ключом.

Такой паттерн часто называют **повторяющейся группой** (repeating group), и он нарушает Первую нормальную форму (1НФ).
Это порождает ряд практических проблем:

- Добавление нового значения (например, `phone4`) требует миграции схемы.
- Для получения всех значений необходимо явно перечислять имена колонок.
- Ограничения (уникальность, NOT NULL, ссылки) приходится повторять для каждой нумерованной колонки.
- Поиск по всем значениям требует конструкций `OR` или `UNION` вместо простого поиска по индексу.

Проверка выявляет таблицы, в которых две и более колонок имеют одинаковый нечисловой префикс.

Аналог [SchemaCrawler `LinterTableWithIncrementingColumns`](https://www.schemacrawler.com/lint.html).

## SQL запрос

- [tables_with_incrementing_columns.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/tables_with_incrementing_columns.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo.orders (
    id       bigint generated always as identity primary key,
    phone1   text,
    phone2   text,
    address1 text,
    address2 text,
    address3 text,
    created_at timestamptz,
    created_by text,
    sku1 text,
    "updatedAt" timestamptz,
    "updatedBy" text
);

create table if not exists demo.events (
    id         bigint    not null,
    event_date date      not null,
    tag1       text,
    tag2       text
) partition by range (event_date);

create table if not exists demo.events_2024
    partition of demo.events
        for values from ('2024-01-01') to ('2025-01-01');
```

## Как исправить

Вынесите повторяющуюся группу в отдельную дочернюю таблицу и свяжите её внешним ключом.

```sql
-- До: повторяющиеся колонки в родительской таблице
-- orders.phone1, orders.phone2, ...

-- После: отдельная дочерняя таблица
create table demo.order_phones (
    id       bigint generated always as identity primary key,
    order_id bigint not null references demo.orders (id) on delete cascade,
    phone    text   not null
);
```
