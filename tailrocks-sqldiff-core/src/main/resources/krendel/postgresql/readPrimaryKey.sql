SELECT COALESCE(
               (SELECT c.column_name
               FROM information_schema.key_column_usage AS c
               LEFT JOIN information_schema.table_constraints AS t
               ON t.constraint_name = c.constraint_name
               WHERE t.table_name = '%s' AND t.constraint_type = 'PRIMARY KEY'),
                'id'
) AS column_name
