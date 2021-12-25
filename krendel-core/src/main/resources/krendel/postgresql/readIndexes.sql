SELECT tablename, indexname, indexdef
FROM pg_catalog.pg_indexes
WHERE schemaname = ''{0}''
                    AND NOT EXISTS (
                        SELECT 1
                        FROM information_schema.table_constraints tc
                        WHERE
                        tc.table_schema = ''{0}''
                        AND tc.constraint_schema = ''{0}''
                        AND tc.constraint_name = indexname
                        AND tc.constraint_type IN (''{1}'')
                    );