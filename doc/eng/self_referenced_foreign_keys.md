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
create table if not exists demo.bad_categories
(
    id        bigint generated always as identity primary key,
    parent_id bigint,
    name      text not null,
    constraint bad_categories_parent_fk
        foreign key (parent_id) references demo.bad_categories (id)
        -- ON DELETE NO ACTION is the implicit default; deleting a parent with children fails
);

-- A corrected version using ON DELETE CASCADE
create table if not exists demo.good_categories_cascade
(
    id        bigint generated always as identity primary key,
    parent_id bigint,
    name      text not null,
    constraint good_categories_cascade_parent_fk
        foreign key (parent_id) references demo.good_categories_cascade (id)
        on delete cascade
);

-- A corrected version using ON DELETE SET NULL
create table if not exists demo.good_categories_set_null
(
    id        bigint generated always as identity primary key,
    parent_id bigint, -- nullable: required for ON DELETE SET NULL
    name      text not null,
    constraint good_categories_set_null_parent_fk
        foreign key (parent_id) references demo.good_categories_set_null (id)
        on delete set null
);

-- Composite self-referenced FK example:
-- A multi-tenant category tree where (tenant_id, category_id) is the composite primary key.
-- A category's parent must belong to the same tenant, so the FK spans both columns.
create table if not exists demo.bad_tenant_categories
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
    -- ON DELETE NO ACTION is the implicit default; deleting a parent with children fails
);

-- A corrected version using ON DELETE CASCADE
create table if not exists demo.good_tenant_categories
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

-- Partitioned table example:
-- Using hash partitioning on id so that id alone can serve as the primary key,
-- allowing the self-referencing FK to reference id without including the partition key.
create table if not exists demo.bad_categories_partitioned
(
    id        bigint not null,
    parent_id bigint,
    name      text not null,
    primary key (id),
    constraint bad_categories_partitioned_parent_fk
        foreign key (parent_id) references demo.bad_categories_partitioned (id)
        -- ON DELETE NO ACTION is the implicit default; deleting a parent with children fails
) partition by hash (id);

create table if not exists demo.bad_categories_partitioned_0
    partition of demo.bad_categories_partitioned for values with (modulus 2, remainder 0);
create table if not exists demo.bad_categories_partitioned_1
    partition of demo.bad_categories_partitioned for values with (modulus 2, remainder 1);

-- A corrected version using ON DELETE CASCADE
create table if not exists demo.good_categories_partitioned
(
    id        bigint not null,
    parent_id bigint,
    name      text not null,
    primary key (id),
    constraint good_categories_partitioned_parent_fk
        foreign key (parent_id) references demo.good_categories_partitioned (id)
            on delete cascade
) partition by hash (id);

create table if not exists demo.good_categories_partitioned_0
    partition of demo.good_categories_partitioned for values with (modulus 2, remainder 0);
create table if not exists demo.good_categories_partitioned_1
    partition of demo.good_categories_partitioned for values with (modulus 2, remainder 1);
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
