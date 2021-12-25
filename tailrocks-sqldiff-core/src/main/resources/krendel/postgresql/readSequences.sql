SELECT sequencename, data_type, start_value, min_value, max_value, increment_by, cache_size
FROM pg_catalog.pg_sequences
WHERE schemaname = '%s';