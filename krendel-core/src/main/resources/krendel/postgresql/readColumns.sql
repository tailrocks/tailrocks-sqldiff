SELECT table_name,
    column_name,
    ordinal_position,
    column_default,
    is_nullable,
    data_type,
    udt_name,
    character_maximum_length,
    numeric_precision,
    numeric_scale,
    col_description((SELECT table_name::REGCLASS::OID), ordinal_position::INT) AS column_description
FROM information_schema.columns
WHERE table_schema = '%s'
ORDER BY table_schema ASC,
    table_name ASC,
    ordinal_position ASC;