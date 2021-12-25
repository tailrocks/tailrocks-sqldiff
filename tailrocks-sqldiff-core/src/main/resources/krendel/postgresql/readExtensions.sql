SELECT e.oid,
    extname AS extensionname,
    nspname,
    extversion AS version,
    extrelocatable,
    obj_description(e.oid, 'pg_extension') AS description
FROM pg_extension e
         LEFT JOIN pg_namespace n ON (e.extnamespace = n.oid)
WHERE n.nspname = '%s'
ORDER BY extname;