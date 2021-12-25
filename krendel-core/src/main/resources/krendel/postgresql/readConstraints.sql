SELECT tc.table_name, tc.constraint_name, tc.constraint_type, c.column_name
FROM information_schema.table_constraints tc
         JOIN information_schema.constraint_column_usage AS ccu
USING (constraint_schema, constraint_name)
         JOIN information_schema.columns AS c
ON c.table_schema = tc.constraint_schema
    AND tc.table_name = c.table_name
    AND ccu.column_name = c.column_name
WHERE tc.table_schema = ''{0}''
                    AND tc.constraint_schema = ''{0}''
                    AND tc.constraint_type IN (''{1}'');
