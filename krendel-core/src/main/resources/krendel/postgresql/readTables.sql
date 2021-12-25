SELECT
    c.oid,
    n.nspname AS schemaname,
    c.relname AS tablename,
    c.relkind,
    obj_description(c.oid, 'pg_class') AS table_description,
    c.relispartition,
    pg_get_partkeydef (c.oid) AS partitionkeydef,
    pg_get_expr(c.relpartbound, c.oid) AS partitionbound
FROM pg_catalog.pg_class c
         INNER JOIN pg_catalog.pg_namespace n
ON (c.relnamespace = n.oid)
         LEFT JOIN pg_catalog.pg_tablespace t
ON (c.reltablespace = t.oid)
         LEFT JOIN (pg_catalog.pg_type y INNER JOIN pg_catalog.pg_namespace o ON (y.typnamespace = o.oid))
ON (c.reloftype = y.oid)
WHERE
        relkind IN ('r', 'p')
        AND n.nspname !~ '^pg_'
        AND n.nspname <> 'information_schema'
        AND n.nspname = '%s'
        AND NOT EXISTS (
        SELECT 1
        FROM pg_depend d
        WHERE
                t.oid = d.objid
                AND d.deptype = 'e'
    )
ORDER BY
    n.nspname,
    c.relname;