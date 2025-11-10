# Проверка присутствия внешних ключей без индексов

## Особенности создания внешних ключей

Когда [создается ограничение внешнего ключа](https://postgrespro.ru/docs/postgresql/17/ddl-constraints#DDL-CONSTRAINTS-FK),
то автоматически **не** создается индекс на столбец (или группу столбцов) с внешним ключом.
Требования создавать такой индекс вручную тоже нет.

## Почему нужно создавать внешние ключи с индексами

Если у столбца с внешним ключом нет индекса, то при поиске строк с одинаковым внешним ключом идет
последовательное сканирование всей таблицы, что снижает производительность.
Также при удалении данных из главной таблицы идет проверка на ссылочную целостность,
что тоже вызывает последовательное сканирование связанной таблицы с внешним ключом без индекса на нем.
При этом нужно оценить, насколько часто идет поиск или изменение данных по этому столбцу и размер самой таблицы.
Если поиск выполняется редко и/или таблица, содержащая столбец с внешним ключом, небольшого размера,
то добавление индекса может не улучшить производительность.

## SQL запрос

- [foreign_keys_without_index.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/foreign_keys_without_index.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo.client
(
    id bigint not null,
    real_id bigint unique not null,
    first_name text,
    last_name text
);

create table if not exists demo."account_with_dublicated_fk_partitioned"
(
    id bigint not null primary key,
    account_number varchar(50) not null unique,
    client_id bigint not null references demo.client (id),
    client_real_id bigint not null,
    timestamp with time zone not null,
    foregn key (client_id, client_real_id) references demo.client (id, real_id)
) partition by range (created);

create table if not exists demo."account_with_dublicated_fk_partitioned_Q3"
    partition of demo."account_without_fk_partitioned"
    for values from ('2025-07-01') to ('2025-10-01');
```
