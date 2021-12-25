select table_name, (SELECT reltuples AS approximate_row_count FROM pg_class WHERE relname = table_name)
from information_schema.tables
where table_schema = '%s'
order by table_name
