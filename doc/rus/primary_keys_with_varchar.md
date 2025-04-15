# Проверка наличия первичных ключей с типом varchar фиксированной длины в 32, 36 или 38 символов

## Почему следует обращать внимание на такие первичные ключи?

Использование типа `varchar` для хранения GUID/UUID в БД является антипаттерном.

PostgreSQL имеет встроенный тип [uuid](https://postgrespro.ru/docs/postgresql/17/datatype-uuid),
который должен использоваться для хранения всех подобных идентификаторов.
Он имеет более компактное представление и более эффективную реализацию.

## Способы представления uuid в текстовом виде

```
b9b1f6f5-7f90-4b68-a389-f0ad8bb5784b - 36 символов
b9b1f6f57f904b68a389f0ad8bb5784b - 32 символа (без использования дефисов)
{b9b1f6f5-7f90-4b68-a389-f0ad8bb5784b} - 38 символов (с фигурными скобками)
```

## SQL запрос

- [primary_keys_with_varchar.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/primary_keys_with_varchar.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo."t-varchar-short" (
 "id-short" varchar(32) not null primary key
);

insert into demo."t-varchar-short" values (replace(gen_random_uuid()::text, '-', ''));
select "id-short" from demo."t-varchar-short";
select "id-short"::uuid from demo."t-varchar-short";

create table if not exists demo.t_varchar_long (
    id_long varchar(36) not null primary key
);

insert into demo.t_varchar_long values (gen_random_uuid());
select id_long from demo.t_varchar_long;

create table if not exists demo.t_link (
    id_long varchar(36) not null references demo.t_varchar_long (id_long),
    "id-short" varchar(32) not null references demo."t-varchar-short" ("id-short"),
    primary key (id_long, "id-short")
);

create table if not exists demo.t_varchar_long_not_pk (
    id_long varchar(36) not null
);

create table if not exists demo.t_uuid (
    id uuid not null primary key
);

insert into demo.t_uuid values (gen_random_uuid());
select id from demo.t_uuid;
```
