# Проверка наличия столбцов с типом money в таблицах

## Почему не стоит использовать

Разработчики PostgreSQL [не рекомендуют](https://wiki.postgresql.org/wiki/Don%27t_Do_This#Don.27t_use_money) в настоящее время использовать тип `money`.

Тип `money` не обрабатывает доли цента (или эквиваленты в других валютах).
То есть он не подходит для хранения курсов валют и выполнения операций, связанных с конвертацией валют.

В нем не хранится валюта со значением, а предполагается, что все столбцы `money` содержат валюту, указанную в параметре `lc_monetary` для базы данных.
Если вы по какой-либо причине измените параметр `lc_monetary`, то все столбцы `money` по сути будут содержать неверное значение.
Это означает, что если вы введете "$10,00", а для параметра `lc_monetary` задано значение "en_US.UTF-8",
то полученное значение может быть "10,00 Lei" или "¥1000", если параметр `lc_monetary` изменен.

Для хранения денежных величин лучше использовать тип `numeric` (с указанием нужной точности)
и с указанием [ISO кода](https://en.wikipedia.org/wiki/ISO_4217) используемой валюты в соседнем столбце.

## SQL запрос

- [columns_with_money_type.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/columns_with_money_type.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo."bad-money"
(
    id bigint generated always as identity primary key,
    "amount-bad" money not null,
    amount numeric(22, 2) null,
    currency_code text not null
);

create table if not exists demo."bad-money_partitioned"
(
    id uuid not null primary key,
    "amount-bad" money not null,
    amount numeric(22, 2) null,
    currency_code text not null
) partition by hash (id);

create table if not exists demo."bad_money_partitioned_hash_p0"
    partition of demo."bad-money_partitioned"
        for values with (modulus 4, remainder 0);
```

## Как исправить

Измените тип целевого столбца на `numeric` и добавьте отдельный столбец типа `text` для хранения кода валюты.
Не забудьте внести необходимые правки в приложение, работающее с БД.
