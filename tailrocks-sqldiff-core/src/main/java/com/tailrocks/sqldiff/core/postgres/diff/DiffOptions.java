package com.tailrocks.sqldiff.core.postgres.diff;

import com.tailrocks.sqldiff.model.config.KrendelDiffConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DiffOptions {

    private final Set<String> ignoreExtensions = new HashSet<>();
    private final Set<String> ignoreEnums = new HashSet<>();
    private final Set<String> ignoreSequences = new HashSet<>();
    private final Set<String> ignoreTables = new HashSet<>();
    private final Set<String> ignoreIndexes = new HashSet<>();
    private final Map<String, Set<String>> ignoreColumns = new HashMap<>();
    private final Map<String, Set<String>> ignoreColumnsDefaultValue = new HashMap<>();
    private final Set<String> ignoreViews = new HashSet<>();
    private final Set<String> ignoreConstraints = new HashSet<>();
    private KrendelDiffConfig.ForeignKeyCompareMethod foreignKeyCompareMethod = KrendelDiffConfig.ForeignKeyCompareMethod.NAME;

    public DiffOptions ignoreExtension(String name) {
        name = requireNotEmpty(name, "name");

        ignoreExtensions.add(name);

        return this;
    }

    public DiffOptions ignoreExtensions(@Nullable Collection<String> names) {
        if (names != null) {
            for (String enumName : names) {
                ignoreExtension(enumName);
            }
        }

        return this;
    }

    public DiffOptions ignoreEnum(String name) {
        name = requireNotEmpty(name, "name");

        ignoreEnums.add(name);

        return this;
    }

    public DiffOptions ignoreEnums(@Nullable Collection<String> names) {
        if (names != null) {
            for (String enumName : names) {
                ignoreEnum(enumName);
            }
        }

        return this;
    }

    public DiffOptions ignoreSequence(String name) {
        name = requireNotEmpty(name, "name");

        ignoreSequences.add(name);

        return this;
    }

    public DiffOptions ignoreSequences(@Nullable Collection<String> names) {
        if (names != null) {
            for (String sequenceName : names) {
                ignoreSequence(sequenceName);
            }
        }

        return this;
    }

    public boolean isIgnoreExtension(String name) {
        name = requireNotEmpty(name, "name");

        return ignoreExtensions.contains(name);
    }

    public boolean isIgnoreEnum(String name) {
        name = requireNotEmpty(name, "name");

        return ignoreEnums.contains(name);
    }

    public boolean isIgnoreSequence(String name) {
        name = requireNotEmpty(name, "name");

        return ignoreSequences.contains(name);
    }

    public DiffOptions ignoreTable(String name) {
        name = requireNotEmpty(name, "name");

        ignoreTables.add(name);

        return this;
    }

    public DiffOptions ignoreTables(@Nullable Collection<String> names) {
        if (names != null) {
            for (String tableName : names) {
                ignoreTable(tableName);
            }
        }

        return this;
    }

    public boolean isIgnoreTable(String name) {
        name = requireNotEmpty(name, "name");

        return ignoreTables.contains(name);
    }

    public DiffOptions ignoreIndex(String name) {
        name = requireNotEmpty(name, "name");

        ignoreIndexes.add(name);

        return this;
    }

    public DiffOptions ignoreIndexes(@Nullable Collection<String> names) {
        if (names != null) {
            for (String tableName : names) {
                ignoreIndex(tableName);
            }
        }

        return this;
    }

    public boolean isIgnoreIndex(String name) {
        name = requireNotEmpty(name, "name");

        return ignoreIndexes.contains(name);
    }

    public DiffOptions ignoreConstraint(String name) {
        name = requireNotEmpty(name, "name");

        ignoreConstraints.add(name);

        return this;
    }

    public DiffOptions ignoreConstraints(@Nullable Collection<String> names) {
        if (names != null) {
            for (String viewName : names) {
                ignoreConstraint(viewName);
            }
        }

        return this;
    }

    public boolean isIgnoreConstraint(String name) {
        name = requireNotEmpty(name, "name");

        return ignoreConstraints.contains(name);
    }

    public DiffOptions ignoreColumn(String tableName, String columnName) {
        tableName = requireNotEmpty(tableName, "tableName");
        columnName = requireNotEmpty(columnName, "columnName");

        ignoreColumns.putIfAbsent(tableName, new HashSet<>());
        ignoreColumns.get(tableName).add(columnName);

        return this;
    }

    public DiffOptions ignoreColumns(@Nullable Map<String, List<String>> tableColumns) {
        if (tableColumns != null) {
            Set<String> tables = tableColumns.keySet();
            for (String tableName : tables) {
                Collection<String> columns = tableColumns.get(tableName);

                for (String column : columns) {
                    ignoreColumn(tableName, column);
                }
            }
        }

        return this;
    }

    public boolean isIgnoreColumn(String tableName, String columnName) {
        tableName = requireNotEmpty(tableName, "tableName");
        columnName = requireNotEmpty(columnName, "columnName");

        if (!ignoreColumns.containsKey(tableName)) {
            return false;
        }

        return ignoreColumns.get(tableName).contains(columnName);
    }

    public DiffOptions ignoreColumnDefaultValue(String tableName, String columnName) {
        tableName = requireNotEmpty(tableName, "tableName");
        columnName = requireNotEmpty(columnName, "columnName");

        ignoreColumnsDefaultValue.putIfAbsent(tableName, new HashSet<>());
        ignoreColumnsDefaultValue.get(tableName).add(columnName);

        return this;
    }

    public DiffOptions ignoreColumnsDefaultValue(@Nullable Map<String, List<String>> tableColumns) {
        if (tableColumns != null) {
            Set<String> tables = tableColumns.keySet();
            for (String tableName : tables) {
                Collection<String> columns = tableColumns.get(tableName);

                for (String column : columns) {
                    ignoreColumnDefaultValue(tableName, column);
                }
            }
        }

        return this;
    }

    public boolean isIgnoreColumnDefaultValue(String tableName, String columnName) {
        tableName = requireNotEmpty(tableName, "tableName");
        columnName = requireNotEmpty(columnName, "columnName");

        if (!ignoreColumnsDefaultValue.containsKey(tableName)) {
            return false;
        }

        return ignoreColumnsDefaultValue.get(tableName).contains(columnName);
    }

    public DiffOptions ignoreView(String name) {
        name = requireNotEmpty(name, "name");

        ignoreViews.add(name);

        return this;
    }

    public DiffOptions ignoreViews(@Nullable Collection<String> names) {
        if (names != null) {
            for (String viewName : names) {
                ignoreView(viewName);
            }
        }

        return this;
    }

    public boolean isIgnoreView(String name) {
        name = requireNotEmpty(name, "name");

        return ignoreViews.contains(name);
    }

    private String requireNotEmpty(@Nullable String name, @NotNull String fieldName) {
        if (name == null) {
            throw new IllegalArgumentException("`" + fieldName + "` can not be null");
        }

        name = name.trim().toLowerCase();

        if (name.equals("")) {
            throw new IllegalArgumentException("`" + fieldName + "` can not be empty string");
        }

        return name;
    }

    public KrendelDiffConfig.ForeignKeyCompareMethod getForeignKeyCompareMethod() {
        return foreignKeyCompareMethod;
    }

    public void setForeignKeyCompareMethod(KrendelDiffConfig.ForeignKeyCompareMethod foreignKeyCompareMethod) {
        Objects.requireNonNull(foreignKeyCompareMethod, "`foreignKeyCompareMethod` can not be null");

        this.foreignKeyCompareMethod = foreignKeyCompareMethod;
    }

}
