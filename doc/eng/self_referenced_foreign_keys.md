# Check for self-referenced foreign keys without `ON DELETE CASCADE` or `ON DELETE SET NULL`

A self-referenced foreign key (a.k.a. recursive or self-join foreign key) is a constraint where
a table's foreign key column references the primary key of the same table.
This pattern is commonly used to model hierarchical or tree-structured data:
category trees, organizational charts, threaded comments, bill-of-materials, etc.

When the `ON DELETE` action is `NO ACTION` (the PostgreSQL default when no rule is specified) or `RESTRICT`,
deleting a parent row that is still referenced by child rows will fail with a foreign key violation error.
To remove a node from such a hierarchy the application must first recursively delete or re-parent
all descendants — which requires complex application logic and careful transaction ordering.
In high-concurrency scenarios this also significantly increases the risk of deadlocks.

Preferred alternatives:
- `ON DELETE CASCADE` — automatically removes all descendant rows when a parent is deleted;
  safe when the entire subtree should be removed together.
- `ON DELETE SET NULL` — sets the FK column to `NULL` in child rows, detaching them from the deleted parent
  and turning them into new root nodes; requires the FK column to be nullable.

See details in the [official documentation](https://www.postgresql.org/docs/current/ddl-constraints.html#DDL-CONSTRAINTS-FK).

## SQL query

- [self_referenced_foreign_keys.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/self_referenced_foreign_keys.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Supports partitioned tables.
The check is performed on the partitioned table itself (the parent one). Individual sections (descendants) are ignored.

## Reproduction script

```sql
create schema if not exists demo;

-- A category tree: each category may have a parent category in the same table.
-- The FK uses the default ON DELETE NO ACTION, which makes subtree deletion fragile.
create table demo.bad_categories
(
    id        bigint generated always as identity primary key,
    parent_id bigint,
    name      text not null,
    constraint bad_categories_parent_fk
        foreign key (parent_id) references demo.bad_categories (id)
        -- ON DELETE NO ACTION is the implicit default; deleting a parent with children fails
);

insert into demo.bad_categories (parent_id, name) values (null, 'Root');
insert into demo.bad_categories (parent_id, name) values (1, 'Child A');
insert into demo.bad_categories (parent_id, name) values (1, 'Child B');

-- Attempting to delete the root row fails because child rows still reference it:
-- delete from demo.bad_categories where id = 1; -- ERROR: update or delete on table "bad_categories"
--                                                -- violates foreign key constraint

-- A corrected version using ON DELETE CASCADE
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

-- Deleting the root row now automatically removes its children:
delete from demo.good_categories_cascade where id = 1;

table demo.good_categories_cascade; -- returns 0 rows

-- A corrected version using ON DELETE SET NULL
create table demo.good_categories_set_null
(
    id        bigint generated always as identity primary key,
    parent_id bigint, -- nullable: required for ON DELETE SET NULL
    name      text not null,
    constraint good_categories_set_null_parent_fk
        foreign key (parent_id) references demo.good_categories_set_null (id)
        on delete set null
);

insert into demo.good_categories_set_null (parent_id, name) values (null, 'Root');
insert into demo.good_categories_set_null (parent_id, name) values (1, 'Child A');
insert into demo.good_categories_set_null (parent_id, name) values (1, 'Child B');

-- Deleting the root row detaches its children (they become new roots):
delete from demo.good_categories_set_null where id = 1;

table demo.good_categories_set_null; -- returns 2 rows, both with parent_id = null
```

## How to fix

Add an explicit `ON DELETE` action to the self-referencing foreign key constraint.

Choose the action based on the desired semantics:
- Use `ON DELETE CASCADE` when removing a parent should automatically remove all its descendants.
- Use `ON DELETE SET NULL` when removing a parent should detach its children, making them independent root nodes.
  The FK column must be nullable for this option.

To alter an existing constraint, drop and recreate it:

```sql
alter table demo.bad_categories
    drop constraint bad_categories_parent_fk;

alter table demo.bad_categories
    add constraint bad_categories_parent_fk
        foreign key (parent_id) references demo.bad_categories (id)
        on delete cascade;
```
