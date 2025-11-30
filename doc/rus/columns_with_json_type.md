# Проверка наличия столбцов с типом json в таблицах

## Как хранить json в таблице

Для хранения данных в json-формате в PostgreSQL есть два типа json и jsonb.
Первый тип хранит точную копию записанного теста, второй - json-данные в двоичном формате.

## Почему лучше jsonb-тип

В документации [прямо говорится](https://postgrespro.ru/docs/postgresql/17/datatype-json), что jsonb предпочтительнее.
jsonb поддерживает ограничения и индексы, что дает возможность выполнять поиск по таким данным быстрее. Только вставка данных в тип jsonb будет медленнее, чем в json из-за того, что их нужно преобразовать в двоичный формат.

jsonb и json различаются при сравнении объектов каждого типа. jsonb не сохраняет порядок ключей при хранении. Если во входных данных есть дублирующиеся ключи, то сохраняется только последнее значение.
Для типа json объекты с разным порядком одинаковых пар ключ-значение будут считаться разными. Порядок ключей остается как во входных данных, причем сохраняются все пары ключ-значение с одинаковым ключом.
Обычные [операторы сравнения](https://postgrespro.ru/docs/postgrespro/17/functions-comparison#FUNCTIONS-COMPARISON-OP-TABLE) применимы только для jsonb.
Для данных в формате jsonb можно использовать оператор DISTINCT. Для json-полей DISTINCT не работает. Если запрос для поля json сгенерируется с таким оператором (например, ORM), то это приведет к ошибкам.
Таким образом, если требуются эффективный поиск или сравнение при работе с данными json-формата, то нужен jsonb тип.

## SQL запрос

- [columns_with_json_type.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/columns_with_json_type.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo."table_with_json_column"
(
    ref_type varchar(32),
    ref_value varchar(64),
    creation_date timestamp with time zone not null,
    entity_id varchar(64) not null,
    real_client_id bigint,
    raw_data json
);

create table if not exists demo."table_with_json_column_partitioned"
(
    ref_type varchar(32),
    ref_value varchar(64),
    creation_date timestamp with time zone not null,
    entity_id varchar(64) not null,
    real_client_id bigint,
    raw_data json
) partition by range (creation_date);

create table if not exists demo."table_with_json_column_partitioned_Q3"
    partition of demo."table_with_json_column_partitioned"
    for values from ('2025-07-01') to ('2025-10-01');
```
