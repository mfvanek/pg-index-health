# Проверка наличия самоссылающихся внешних ключей без `ON DELETE CASCADE` или `ON DELETE SET NULL`

Самоссылающийся внешний ключ (рекурсивный или self-join FK) — это ограничение, при котором
столбец внешнего ключа таблицы ссылается на первичный ключ той же самой таблицы.
Этот шаблон часто используется для моделирования иерархических или древовидных структур данных:
деревья категорий, организационные схемы, ветки комментариев, спецификации изделий и т. п.

Когда для `ON DELETE` указано действие `NO ACTION` (поведение PostgreSQL по умолчанию, если правило не задано) или `RESTRICT`,
попытка удалить родительскую строку, на которую ссылаются дочерние строки, завершится ошибкой нарушения внешнего ключа.
Чтобы удалить узел из такой иерархии, приложение должно сначала рекурсивно удалить или перепривязать
всех потомков — что требует сложной логики и тщательного порядка выполнения транзакций.
В условиях высокой конкурентности это также существенно повышает риск взаимных блокировок (deadlock).

Предпочтительные альтернативы:
- `ON DELETE CASCADE` — автоматически удаляет все дочерние строки при удалении родителя;
  безопасен, когда всё поддерево должно удаляться вместе с родителем.
- `ON DELETE SET NULL` — устанавливает `NULL` в FK-столбце дочерних строк, отвязывая их от удалённого родителя
  и превращая в новые корневые узлы; требует, чтобы FK-столбец допускал `NULL`.

Подробности в [официальной документации](https://www.postgresql.org/docs/current/ddl-constraints.html#DDL-CONSTRAINTS-FK).

## SQL запрос

- [self_referenced_foreign_keys.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/self_referenced_foreign_keys.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

-- Дерево категорий: каждая категория может иметь родительскую категорию в той же таблице.
-- FK использует ON DELETE NO ACTION по умолчанию, что делает удаление поддерева ненадёжным.
create table demo.bad_categories
(
    id        bigint generated always as identity primary key,
    parent_id bigint,
    name      text not null,
    constraint bad_categories_parent_fk
        foreign key (parent_id) references demo.bad_categories (id)
        -- ON DELETE NO ACTION — неявное умолчание; удаление родителя с дочерними строками завершается ошибкой
);

insert into demo.bad_categories (parent_id, name) values (null, 'Root');
insert into demo.bad_categories (parent_id, name) values (1, 'Child A');
insert into demo.bad_categories (parent_id, name) values (1, 'Child B');

-- Попытка удалить корневую строку завершится ошибкой, т. к. дочерние строки ещё ссылаются на неё:
-- delete from demo.bad_categories where id = 1; -- ERROR: update or delete on table "bad_categories"
--                                                -- violates foreign key constraint

-- Исправленный вариант с ON DELETE CASCADE
create table demo.good_categories_cascade
(
    id        bigint generated always as identity primary key,
    parent_id bigint,
    name      text not null,
    constraint good_categories_cascade_parent_fk
        foreign key (parent_id) references demo.good_categories_cascade (id)
        on delete cascade
);

insert into demo.good_categories_cascade (parent_id, name) values (null, 'Root');
insert into demo.good_categories_cascade (parent_id, name) values (1, 'Child A');
insert into demo.good_categories_cascade (parent_id, name) values (1, 'Child B');

-- Удаление корневой строки автоматически удаляет всех её потомков:
delete from demo.good_categories_cascade where id = 1;

table demo.good_categories_cascade; -- возвращает 0 строк

-- Исправленный вариант с ON DELETE SET NULL
create table demo.good_categories_set_null
(
    id        bigint generated always as identity primary key,
    parent_id bigint, -- nullable: обязательно для ON DELETE SET NULL
    name      text not null,
    constraint good_categories_set_null_parent_fk
        foreign key (parent_id) references demo.good_categories_set_null (id)
        on delete set null
);

insert into demo.good_categories_set_null (parent_id, name) values (null, 'Root');
insert into demo.good_categories_set_null (parent_id, name) values (1, 'Child A');
insert into demo.good_categories_set_null (parent_id, name) values (1, 'Child B');

-- Удаление корневой строки отвязывает её дочерние элементы (они становятся новыми корнями):
delete from demo.good_categories_set_null where id = 1;

table demo.good_categories_set_null; -- возвращает 2 строки, обе с parent_id = null

-- Пример составного самоссылающегося внешнего ключа:
-- Мультитенантное дерево категорий, где (tenant_id, category_id) — составной первичный ключ.
-- Родительская категория должна принадлежать тому же тенанту, поэтому FK охватывает оба столбца.
create table demo.bad_tenant_categories
(
    tenant_id          integer not null,
    category_id        integer not null,
    parent_tenant_id   integer,
    parent_category_id integer,
    name               text not null,
    primary key (tenant_id, category_id),
    constraint bad_tenant_categories_parent_fk
        foreign key (parent_tenant_id, parent_category_id)
            references demo.bad_tenant_categories (tenant_id, category_id)
    -- ON DELETE NO ACTION — неявное умолчание; удаление родителя с дочерними строками завершается ошибкой
);

insert into demo.bad_tenant_categories (tenant_id, category_id, parent_tenant_id, parent_category_id, name)
    values (1, 1, null, null, 'Root');
insert into demo.bad_tenant_categories (tenant_id, category_id, parent_tenant_id, parent_category_id, name)
    values (1, 2, 1, 1, 'Child A');
insert into demo.bad_tenant_categories (tenant_id, category_id, parent_tenant_id, parent_category_id, name)
    values (1, 3, 1, 1, 'Child B');

-- Попытка удалить корневую строку завершится ошибкой, т. к. дочерние строки ещё ссылаются на неё:
-- delete from demo.bad_tenant_categories where tenant_id = 1 and category_id = 1;
-- ERROR: update or delete on table "bad_tenant_categories" violates foreign key constraint

-- Исправленный вариант с ON DELETE CASCADE
create table demo.good_tenant_categories
(
    tenant_id          integer not null,
    category_id        integer not null,
    parent_tenant_id   integer,
    parent_category_id integer,
    name               text not null,
    primary key (tenant_id, category_id),
    constraint good_tenant_categories_parent_fk
        foreign key (parent_tenant_id, parent_category_id)
            references demo.good_tenant_categories (tenant_id, category_id)
            on delete cascade
);

insert into demo.good_tenant_categories (tenant_id, category_id, parent_tenant_id, parent_category_id, name)
    values (1, 1, null, null, 'Root');
insert into demo.good_tenant_categories (tenant_id, category_id, parent_tenant_id, parent_category_id, name)
    values (1, 2, 1, 1, 'Child A');
insert into demo.good_tenant_categories (tenant_id, category_id, parent_tenant_id, parent_category_id, name)
    values (1, 3, 1, 1, 'Child B');

-- Удаление корневой строки автоматически удаляет всех её потомков:
delete from demo.good_tenant_categories where tenant_id = 1 and category_id = 1;

table demo.good_tenant_categories; -- возвращает 0 строк
```

## Как исправить

Добавьте явное действие `ON DELETE` к самоссылающемуся ограничению внешнего ключа.

Выберите действие в зависимости от нужной семантики:
- Используйте `ON DELETE CASCADE`, если удаление родителя должно автоматически удалять всех его потомков.
- Используйте `ON DELETE SET NULL`, если удаление родителя должно отвязывать его детей, делая их независимыми корневыми узлами.
  Для этого варианта FK-столбец должен допускать `NULL`.

Чтобы изменить существующее ограничение, удалите его и создайте заново:

```sql
alter table demo.bad_categories
    drop constraint bad_categories_parent_fk;

alter table demo.bad_categories
    add constraint bad_categories_parent_fk
        foreign key (parent_id) references demo.bad_categories (id)
        on delete cascade;
```
