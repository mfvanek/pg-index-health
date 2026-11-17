# Проверка циклических внешних ключей между таблицами

## Что это такое и почему их следует избегать

Цикл внешних ключей — это цепочка внешних ключей, которая ведёт от таблицы обратно к ней самой через одну или несколько других таблиц.
Простейший пример: `orders.customer_id` ссылается на `customers`, а `customers.last_order_id` ссылается на `orders`.

Такие циклические связи считаются признаком неудачного проектирования:

- Для вставки и удаления данных нужен особый порядок выражений с последующим обновлением либо отложенные ограничения.
- `pg_dump --data-only` предупреждает о циклических внешних ключах, и такой дамп может не восстановиться без `--disable-triggers`.
- `truncate` работает только для всех таблиц цикла сразу (либо с `cascade`).
- Фикстуры и ORM не могут вывести порядок загрузки данных.

Проверка сообщает о **любом** цикле, даже если его внешние ключи объявлены как `deferrable` или построены на nullable-столбцах.
Каждый цикл возвращается ровно один раз в виде упорядоченной цепочки внешних ключей:
каждый внешний ключ ссылается на таблицу следующего внешнего ключа в цепочке, а последний — на первую таблицу.
Цикл начинается с таблицы с наименьшим именем; первая таблица не имеет особого смысла.

Циклы перечисляются по ограничениям, а не по парам таблиц:
два параллельных внешних ключа между одними и теми же таблицами дают два отдельных цикла, потому что разбираться нужно с обоими.

Проверка не сообщает о:

- внешних ключах, ссылающихся на свою же таблицу;
- циклах, проходящих через таблицу в другой схеме.

Внешние ключи, объявленные как `not enforced` (PostgreSQL 18+), обрабатываются как обычные внешние ключи.

Количество простых циклов может расти экспоненциально в плотно связанном графе,
поэтому на большой схеме с множеством переплетённых циклов проверка может выполняться долго.

Если цикл является осознанным проектным решением, исключите его по имени любой из его таблиц (`SkipTablesByNamePredicate`)
или по имени любого из его ограничений (`SkipByConstraintNamePredicate`).

См. также линтер [LinterTableCycles](https://www.schemacrawler.com/lint.html) в SchemaCrawler.

## SQL запрос

- [cyclic_foreign_keys.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/cyclic_foreign_keys.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются:
внешний ключ, объявленный непосредственно на секции или ссылающийся непосредственно на секцию, в циклах не участвует.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

-- Для обычных (не секционированных) таблиц

create table if not exists demo.customers(
    id bigint primary key generated always as identity,
    name text not null,
    last_order_id bigint
);

create table if not exists demo.orders(
    id bigint primary key generated always as identity,
    customer_id bigint not null,
    created_at timestamptz not null default current_timestamp,
    constraint orders_customer_fk foreign key (customer_id) references demo.customers (id)
);

alter table if exists demo.customers
    add constraint customers_last_order_fk foreign key (last_order_id) references demo.orders (id);

-- Для секционированных таблиц

create table if not exists demo.customers_partitioned(
    id bigint not null primary key,
    name text not null,
    last_order_id bigint
) partition by range (id);

create table if not exists demo.customers_default
    partition of demo.customers_partitioned default;

create table if not exists demo.orders_for_partitioned(
    id bigint primary key generated always as identity,
    customer_id bigint not null,
    created_at timestamptz not null default current_timestamp,
    constraint orders_for_partitioned_customer_fk foreign key (customer_id) references demo.customers_partitioned (id)
);

alter table if exists demo.customers_partitioned
    add constraint customers_partitioned_last_order_fk foreign key (last_order_id) references demo.orders_for_partitioned (id);
```

## Как исправить

Цикл устраняется только изменением модели данных, поэтому универсальной миграции не существует:

- Удалите обратную ссылку и вычисляйте значение запросом
  (например, находите последний заказ клиента через `order by created_at desc limit 1`, а не храните `last_order_id`).
- Вынесите связь в отдельную связующую таблицу, которая ссылается на обе таблицы.

Разумная подсказка при выборе ограничения для удаления — внешний ключ, который участвует в наибольшем числе найденных циклов.

Если сделать внешний ключ `deferrable` или его столбцы nullable, с циклом станет можно работать, но он не исчезнет: проверка по-прежнему о нём сообщит.
