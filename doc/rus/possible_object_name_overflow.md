# Проверка наличия объектов с названием максимальной длины

## Почему нужно отслеживать длину названий объектов БД

Максимальный размер идентификатора составляет 63 байта.
Если он превышен, то PostgreSQL без уведомления обрезает слишком длинное название.
Это может привести к ситуации, когда 2 разных объекта становятся идентичными по названию.
Например, если создается миграция, где создаются индексы с длинными названиями,
которые начинаются одинаково, то при использовании выражения `IF NOT EXISTS` может создаться только один объект вместо нескольких.

## SQL запрос

- [possible_object_name_overflow.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/possible_object_name_overflow.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется как на самой секционированной таблице (родительской), так и на отдельных секциях (потомках).

# Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo."entity_long_1234567890_1234567890_1234567890_1234567890_1234567"
(
    ref_type varchar(32),
    ref_value varchar(64),
    entity_id bigserial primary key
);

create table if not exists demo."entity_long_1234567890_1234567890_1234567890_1234567890_1234567"
(
    ref_type varchar(32),
    ref_value varchar(64),
    entity_id bigserial primary key
) partition by range (entity_id);

create index if not exists idx_entity_long_1234567890_1234567890_1234567890_1234567890_123
    on {schemaName}.entity_long_1234567890_1234567890_1234567890_1234567890_1234567 (ref_type, ref_value);

create table if not exists {schemaName}.entity_default_long_1234567890_1234567890_1234567890_1234567890
    partition of {schemaName}.entity_long_1234567890_1234567890_1234567890_1234567890_1234567 default;
```
