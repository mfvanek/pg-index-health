# Проверка раздувания таблиц

## Почему раздуваются таблицы

Частые UPDATE и DELETE операции могут вызвать заметное увеличение размера таблицы,
потому что старые версии строк [не удаляются сразу](https://postgrespro.ru/docs/postgresql/17/routine-vacuuming).
Неблокирующая очистка помечает эти устаревшие версии как удаленные и они могу потом использоваться для добавления новых строк,
но физическое место возвращается системе только, если эти удаленные строки были в конце таблице.

## Почему нужно следить за раздуванием таблиц

Хотя устаревшие записи постепенно обработаются демоном автоочистки, размер таблицы останется слишком большим, а таблица разреженной.
Это приведет к снижению производительности, потому что сканирование таблицы будет происходить медленнее.
Поэтому важно отслеживать резкое изменение размера таблиц, если данные часто обновляются.
Данные о слишком быстром росте размера таблицы могут также говорить о том, что автоочистка настроена неправильно и нужно менять эти настройки.

## SQL запрос

- [bloated_tables.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/bloated_tables.sql)

## Тип проверки

- **runtime** (требует наличия накопленной статистики)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы. Процент раздувания считается для каждой секции отдельно.

## Как работает эта проверка

Для выполнения запроса пользователю необходимы права на чтение проверяемых таблиц.

### Принцип работы

Выполняется SQL-запрос к таблицам системной схемы pg_catalog. Они содержат статистическую информацию об основных объектах:
таблицах, индексах, столбцах.

Сначала в запросе собираются данные о таблицах. Проверяется, доступна ли статистика по таблице.

Потом по этим данным определяется размер одного кортежа и общее количество страниц, используемых таблицей. Дальше оценивается количество страниц, которое должно быть использовано таблицей, и сравнивает его с фактическим количеством страниц.
И в итоге вычисляется раздутость таблицы в байтах (разница в страницах умноженная на размер блока) и в процентах. Если он превышает заданное значение (дефолтное составляет 10%), то таблица считается раздутой.

# Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo."client_with_bloat"
(
    id bigint not null primary key,
    first_name text,
    last_name text
);

create table if not exists demo."account_with_bloat_partitioned"
(
    id bigint not null primary key,
    account_number varchar(50) not null unique,
    client_id bigint not null references demo.client (id) on delete cascade
) partition by range (id);

create table if not exists demo."account_with_bloat_partitioned_1_500"
    partition of demo."duplicated_indexes_partitioned"
    for values from (1) to (500);
    
create table if not exists demo."account_with_bloat_partitioned_500_1000"
    partition of demo."duplicated_indexes_partitioned"
    for values from (500) to (1001);

insert into demo."client_with_bloat" (id, first_name, last_name) values (generate_series(1, 1000),'first', 'last');

insert into demo."account_with_bloat_partitioned" (id, account_number, client_id) values (generate_series(1, 1000),'account number', generate_series(1, 1000));

delete from demo."client_with_bloat" where id < 300;
```
