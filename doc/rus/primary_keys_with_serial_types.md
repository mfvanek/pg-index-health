# Проверка наличия первичных ключей типа serial

## Почему первичный ключ не стоит создавать с типом serial
Первичный ключ типа serial [создает проблемы](https://wiki.postgresql.org/wiki/Don%27t_Do_This#Don.27t_use_serial):
 - не соответствует стандарту SQL, а значит код нельзя переиспользовать
 - может вызывать ошибки, если манипуляции с таблицей включены в скрипты при деплое
 - трудно вносить изменения в ПК с таким типом
Есть [другой вариант создания первичного ключа](https://postgrespro.ru/docs/postgrespro/17/sql-createtable#SQL-CREATETABLE-PARMS-GENERATED-IDENTITY) для PostgreSQL старше 10 версии

## Как работает эта проверка
При вызове метода класса этой проверки doCheck выполняется sql-запрос [sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/primary_keys_with_serial_types.sql).
Работает для статического анализа.
Применима для секционированных таблиц.
