WITH sequences_list AS (
    SELECT pg_class.oid, pg_class.relname
    FROM pg_class
             INNER JOIN pg_namespace ON pg_namespace.oid = pg_class.relnamespace
    WHERE pg_namespace.nspname = 'public' AND pg_class.relkind = 'S'
),
    tables_list AS (
        SELECT pg_class.oid, pg_class.relname
        FROM pg_class
                 INNER JOIN pg_namespace ON pg_namespace.oid = pg_class.relnamespace
        WHERE pg_namespace.nspname = 'public' AND pg_class.relkind = 'r'
    )
SELECT sequence_data.relname AS sequencename,
    table_data.relname AS tablename,
    attrib.attname AS columnname,
    pg_depend.deptype AS type
FROM pg_depend
         INNER JOIN sequences_list AS sequence_data
ON sequence_data.oid = pg_depend.objid
         INNER JOIN tables_list AS table_data
ON table_data.oid = pg_depend.refobjid
         INNER JOIN pg_attribute AS attrib
ON attrib.attnum = pg_depend.refobjsubid
    AND attrib.attrelid = pg_depend.refobjid
WHERE pg_depend.deptype IN ('a', 'i');