# Проверка наличия колонок с одинаковым именем, но разными типами данных в разных таблицах

В пределах одной схемы колонка с заданным именем должна во всех таблицах иметь один и тот же тип данных.
Если одно и то же имя колонки (например, `id` или `created_at`) объявлено с разными типами в разных таблицах,
это делает операции соединения (joins) в sql-запросах и код приложения подверженными ошибкам:
могут потребоваться неявные приведения типов,
индексы могут не использоваться, а при передаче значений между таблицами появляются трудноуловимые баги.

Классический пример — первичный ключ с именем `id`, который в одной таблице объявлен как `bigint`,
в другой — как `int`, а в третьей — как `uuid`. Другой частый случай — колонка `created_at`,
которая в одних таблицах использует тип `timestamp` (без часового пояса), а в других — `timestamptz`.

Эта проверка похожа на [schemacrawler LinterColumnTypes](https://www.schemacrawler.com/lint.html).

## SQL запрос

- [columns_with_inconsistent_types.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/columns_with_inconsistent_types.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

-- Обычные таблицы

create table if not exists demo.clients
(
    id bigint primary key,
    created_at timestamp not null default now()
);

create table if not exists demo.orders
(
    id int primary key,
    created_at timestamptz not null default now()
);

create table if not exists demo.payments
(
    id uuid primary key,
    created_at timestamptz not null default now()
);

-- Секционированные таблицы

create table if not exists demo.events
(
    id bigint not null,
    created_at timestamptz not null default now(),
    primary key (id, created_at)
) partition by range (created_at);

create table if not exists demo.events_default
    partition of demo.events default;
```

## Как исправить

Выберите единый канонический тип для каждого имени колонки в пределах схемы и приведите все таблицы к нему.
Для первичных ключей предпочтительно использовать `bigint` (или `uuid`, если нужны глобально уникальные идентификаторы)
и применять его единообразно. Для меток времени везде используйте `timestamptz`.
Не забудьте внести необходимые изменения в приложение, работающее с базой данных.
