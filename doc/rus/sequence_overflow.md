# Проверка состояния последовательностей на переполнение

## Почему нужно проверять состояние последовательности

Суррогатные первичные ключи часто заполнятся из последовательности.
Если данных много, то она может переполниться.
Если не отслеживать объем оставшихся значений, то исправление ошибки может привести к длительной блокировке этой таблицы и других,
если переполненный первичный ключ является для них внешним ключом.

## SQL запрос

- [sequence_overflow.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/sequence_overflow.sql)

## Тип проверки

- **runtime** (имеет смысл запускать на работающем инстансе БД)

## Поддержка секционированных таблиц

Не применима для секционированных таблиц.

# Скрипт для воспроизведения

```sql
create schema if not exists demo;

create sequence demo.seq_1 as smallint increment by 1 maxvalue 100 start 92;

create sequence demo.seq_3 as integer increment by 2 maxvalue 100 start 92;

create sequence demo.seq_5 as bigint increment by 10 maxvalue 100 start 92;

create sequence demo.seq_cycle as bigint increment by 10 maxvalue 100 start 92 cycle;
```
