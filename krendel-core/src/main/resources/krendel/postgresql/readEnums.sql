SELECT DISTINCT e.enumtypid AS enumtypid, t.typname AS enum_name
FROM pg_type t
         JOIN pg_enum e ON t.oid = e.enumtypid
         JOIN pg_namespace n ON n.oid = t.typnamespace
WHERE n.nspname = '%s'
ORDER BY t.typname ASC;