SELECT nmsp_parent.nspname AS parent_schema,
    parent.relname AS parent,
    nmsp_child.nspname AS child_schema,
    child.relname AS child
FROM pg_inherits
         INNER JOIN pg_class parent
ON pg_inherits.inhparent = parent.oid
    AND parent.relkind = 'p'
         INNER JOIN pg_class child
ON pg_inherits.inhrelid = child.oid
         INNER JOIN pg_namespace nmsp_parent
ON nmsp_parent.oid = parent.relnamespace
         INNER JOIN pg_namespace nmsp_child
ON nmsp_child.oid = child.relnamespace
ORDER BY parent.relname, child.relname;