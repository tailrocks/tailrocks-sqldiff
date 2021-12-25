SELECT c.constraint_name,
    origin.table_name,
    origin.column_name,
    reference.table_name AS foreign_table_name,
    reference.column_name AS foreign_column_name
FROM information_schema.referential_constraints AS c
         JOIN information_schema.key_column_usage origin
ON origin.constraint_name = c.constraint_name
    AND origin.table_schema = ''{0}''
                JOIN information_schema.key_column_usage reference
ON reference.ordinal_position = origin.position_in_unique_constraint
    AND reference.constraint_name = C.unique_constraint_name
    AND reference.table_schema = ''{0}''
GROUP BY
    C.constraint_name,
    origin.table_name,
    origin.column_name,
    reference.table_name,
    reference.column_name,
    origin.ordinal_position
ORDER BY
    C.constraint_name,
    origin.ordinal_position,
    origin.table_name;
