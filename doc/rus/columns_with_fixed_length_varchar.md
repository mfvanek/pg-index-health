# Проверка наличия столбцов с типом varchar(n) в таблицах

## Рекомендация

В [документации](https://wiki.postgresql.org/wiki/Don%27t_Do_This#Don.27t_use_varchar.28n.29_by_default) говорится, что не следует использовать тип `varchar(n)` по умолчанию.
Вместо него стоит использовать `varchar` (без ограничения длины) или `text`.

## Почему не стоит использовать

`varchar(n)` — это текстовое поле переменной длины, которое выдаст ошибку,
если вы попытаетесь вставить в него строку длиннее **n** символов.
`varchar (без (n))` или `text` похожи, но без ограничения длины.
Если вы вставите одну и ту же строку в три типа полей, они займут одинаковое количество места,
и вы не сможете оценить разницу в производительности.
Если вам нужно ограничить значение в поле, вам, вероятно, нужно что-то более конкретное, чем максимальная длина.

## Когда использовать

Если вам нужно текстовое поле, которое выдаст ошибку, если вы вставите в него слишком длинную строку, и вы не хотите использовать явный check-constraint, то varchar(n) — вполне подходящий тип.

## SQL запрос

- [columns_with_fixed_length_varchar.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/columns_with_fixed_length_varchar.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo."bad_varchar_limit"
(
    id int not null primary key,
    name varchar(20) -- Limits future flexibility
);

create table if not exists demo."bad_varchar_limit_partitioned"
(
    id int not null,
    name varchar(20), -- Limits future flexibility
    primary key (id, name)
) partition by hash (name);

create table if not exists demo."bad_varchar_limit_partitioned_hash_p0"
    partition of demo."bad_varchar_limit_partitioned"
    for values with (modulus 4, remainder 0);
```

## Как исправить

Измените тип столбца на `text`. Не забудьте внести необходимые правки в приложение, работающее с БД.
