package com.tailrocks.sqldiff.core.postgres.diff;

import com.tailrocks.sqldiff.core.postgres.model.PgColumn;
import com.tailrocks.sqldiff.core.postgres.model.PgConstraint;
import com.tailrocks.sqldiff.core.postgres.model.PgEnum;
import com.tailrocks.sqldiff.core.postgres.model.PgExtension;
import com.tailrocks.sqldiff.core.postgres.model.PgForeignKey;
import com.tailrocks.sqldiff.core.postgres.model.PgIndex;
import com.tailrocks.sqldiff.core.postgres.model.PgPrimaryKey;
import com.tailrocks.sqldiff.core.postgres.model.PgSchema;
import com.tailrocks.sqldiff.core.postgres.model.PgSequence;
import com.tailrocks.sqldiff.core.postgres.model.PgTable;
import com.tailrocks.sqldiff.core.postgres.model.PgUniqueConstraint;
import com.tailrocks.sqldiff.core.postgres.model.PgView;
import com.scentbird.krendel.model.config.KrendelDiffConfig.ForeignKeyCompareMethod;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class SchemaDiff {

    private static final Logger log = LoggerFactory.getLogger(SchemaDiff.class.getSimpleName());

    private final DiffOptions options;

    public SchemaDiff() {
        this(new DiffOptions());
    }

    public SchemaDiff(DiffOptions options) {
        this.options = options;
    }

    public Diff diff(PgSchema sourceSchema, PgSchema destinationSchema) {
        Diff diff = new Diff(sourceSchema, destinationSchema, options);

        compareExtensions(diff);
        compareEnums(diff);
        compareSequences(diff);
        compareTables(diff);
        if (options.getForeignKeyCompareMethod().equals(ForeignKeyCompareMethod.NAME)) {
            compareForeignKeysByName(diff);
        } else if (options.getForeignKeyCompareMethod().equals(ForeignKeyCompareMethod.REFERENCES)) {
            compareForeignKeysByReferences(diff);
        } else {
            log.error("Unsupported ForeignKeyCompareMethod: {}", options.getForeignKeyCompareMethod());
        }
        compareViews(diff);

        return diff;
    }

    private void compareExtensions(Diff diff) {
        Set<String> v1Extensions = diff.getSourceSchema().getExtensionsByName().keySet();
        Set<String> v2Extensions = diff.getDestinationSchema().getExtensionsByName().keySet();

        Set<String> allExtensions = new LinkedHashSet<>(v1Extensions);
        allExtensions.addAll(v2Extensions);

        for (String extensionName : allExtensions) {
            PgExtension v1Extension = diff.getSourceSchema().getExtensionsByName().get(extensionName);
            PgExtension v2Extension = diff.getDestinationSchema().getExtensionsByName().get(extensionName);

            if (options.isIgnoreExtension(extensionName)) {
                if (v2Extension != null) {
                    log.warn("Ignored extension {}, but exists in target schema", extensionName);
                } else {
                    log.debug("Ignored extension: {}", extensionName);
                }
            } else {
                if (v1Extension == null && v2Extension != null) {
                    // create new enum
                    diff.addDiffItem(DiffItem.ofInsert(v2Extension));
                } else if (v1Extension != null && v2Extension == null) {
                    // delete enum
                    diff.addDiffItem(DiffItem.ofRemove(v1Extension));
                } else if (v1Extension != null && v2Extension != null) {
                    // nothing to do, no implemented yet
                }
            }
        }
    }

    private void compareEnums(Diff diff) {
        Set<String> v1Enums = diff.getSourceSchema().getEnumsByName().keySet();
        Set<String> v2Enums = diff.getDestinationSchema().getEnumsByName().keySet();

        Set<String> allEnums = new LinkedHashSet<>(v1Enums);
        allEnums.addAll(v2Enums);

        for (String enumName : allEnums) {
            PgEnum v1Enum = diff.getSourceSchema().getEnum(enumName);
            PgEnum v2Enum = diff.getDestinationSchema().getEnum(enumName);

            if (options.isIgnoreEnum(enumName)) {
                if (v2Enum != null) {
                    log.warn("Ignored enum {}, but exists in target schema", enumName);
                } else {
                    log.debug("Ignored enum: {}", enumName);
                }
            } else {
                if (v1Enum == null && v2Enum != null) {
                    // create new enum
                    diff.addDiffItem(DiffItem.ofInsert(v2Enum));
                } else if (v1Enum != null && v2Enum == null) {
                    // delete enum
                    diff.addDiffItem(DiffItem.ofRemove(v1Enum));
                } else if (v1Enum != null && v2Enum != null) {
                    // check enum for changes
                    List<String> leftValue = new ArrayList<>(v1Enum.getValues());
                    List<String> rightValues = new ArrayList<>(v2Enum.getValues());

                    Collections.sort(leftValue);
                    Collections.sort(rightValues);

                    if (!leftValue.equals(rightValues)) {
                        diff.addDiffItem(DiffItem.ofChange(v1Enum, v2Enum));
                    }
                }
            }
        }
    }

    private void compareSequences(Diff diff) {
        Set<String> v1Sequences = diff.getSourceSchema().getSequencesByName().keySet();
        Set<String> v2Sequences = diff.getDestinationSchema().getSequencesByName().keySet();

        Set<String> allSequences = new LinkedHashSet<>(v1Sequences);
        allSequences.addAll(v2Sequences);

        for (String sequenceName : allSequences) {
            PgSequence v1Sequence = diff.getSourceSchema().getSequence(sequenceName);
            PgSequence v2Sequence = diff.getDestinationSchema().getSequence(sequenceName);

            if (options.isIgnoreSequence(sequenceName)) {
                if (v2Sequence != null) {
                    log.warn("Ignored sequence {}, but exists in target schema", sequenceName);
                } else {
                    log.debug("Ignored sequence: {}", sequenceName);
                }
            } else {
                if (v1Sequence == null && v2Sequence != null) {
                    // create new sequence
                    if (v2Sequence.getDepType() == null || !v2Sequence.getDepType().equalsIgnoreCase("i")) {
                        diff.addDiffItem(DiffItem.ofInsert(v2Sequence));
                    }
                } else if (v1Sequence != null && v2Sequence == null) {
                    // delete sequence
                    diff.addDiffItem(DiffItem.ofRemove(v1Sequence));
                } else if (v1Sequence != null && v2Sequence != null) {
                    // compare sequences
                    if (!Objects.equals(v1Sequence, v2Sequence)) {
                        diff.addDiffItem(DiffItem.ofChange(v1Sequence, v2Sequence));
                    }
                }
            }
        }
    }

    private void compareTables(Diff diff) {
        Set<String> v1Tables = diff.getSourceSchema().getTablesByName().keySet();
        Set<String> v2Tables = diff.getDestinationSchema().getTablesByName().keySet();

        Set<String> allTables = new LinkedHashSet<>(v1Tables);
        allTables.addAll(v2Tables);

        Set<String> children = new LinkedHashSet<>();

        // process parents first
        for (String tableName : allTables) {
            PgTable v1Table = diff.getSourceSchema().getTable(tableName);
            PgTable v2Table = diff.getDestinationSchema().getTable(tableName);

            if (options.isIgnoreTable(tableName)) {
                if (v2Table != null) {
                    log.warn("Ignored table {}, but exists in target schema", tableName);
                } else {
                    log.debug("Ignored table: {}", tableName);
                }
            } else {
                boolean v1IsChild = v1Table != null && v1Table.getParent() != null;
                boolean v2IsChild = v2Table != null && v2Table.getParent() != null;

                if (v1IsChild || v2IsChild) {
                    // process children later
                    children.add(tableName);
                } else {
                    compareTwoTables(diff, v1Table, v2Table);
                }
            }
        }

        // process children
        for (String tableName : children) {
            PgTable v1Table = diff.getSourceSchema().getTable(tableName);
            PgTable v2Table = diff.getDestinationSchema().getTable(tableName);

            compareTwoTables(diff, v1Table, v2Table);
        }
    }

    private void compareTwoTables(Diff diff, PgTable v1Table, PgTable v2Table) {
        boolean ignoreTableChanges = false;

        if (v1Table == null && v2Table != null) {
            // create new table
            diff.addDiffItem(DiffItem.ofInsert(v2Table));
        } else if (v1Table != null && v2Table == null) {
            // delete table
            diff.addDiffItem(DiffItem.ofRemove(v1Table));

            ignoreTableChanges = true;
        } else if (v1Table != null && v2Table != null) {
            compareTableChanges(diff, v1Table, v2Table);

            // check table columns for changes
            compareTableColumns(diff, v1Table, v2Table);
        }

        if (!ignoreTableChanges) {
            compareConstraints(diff, v1Table, v2Table);
            compareTableIndexes(diff, v1Table, v2Table);
        }
    }

    private void compareTableColumns(Diff diff, PgTable v1Table, PgTable v2Table) {
        Set<String> v1Columns = v1Table.getColumnsByName().keySet();
        Set<String> v2Columns = v2Table.getColumnsByName().keySet();

        Set<String> allColumns = new LinkedHashSet<>(v1Columns);
        allColumns.addAll(v2Columns);

        for (String columnName : allColumns) {
            PgColumn v1Column = v1Table.getColumn(columnName);
            PgColumn v2Column = v2Table.getColumn(columnName);

            if (options.isIgnoreColumn(v2Table.getName(), columnName)) {
                if (v2Column != null) {
                    log.warn("Ignored column {}.{}, but exists in target schema", v2Table.getName(), columnName);
                } else {
                    log.debug("Ignored column: {}.{}", v2Table.getName(), columnName);
                }
            } else {
                if (v1Column == null && v2Column != null) {
                    // create new column
                    diff.addDiffItem(DiffItem.ofInsert(v2Column));
                } else if (v1Column != null && v2Column == null) {
                    // delete column
                    diff.addDiffItem(DiffItem.ofRemove(v1Column));
                } else if (v1Column != null && v2Column != null) {
                    // check column for changes
                    compareColumnChanges(diff, v1Column, v2Column);
                }
            }
        }
    }

    private void compareTableIndexes(Diff diff, @Nullable PgTable v1Table, @Nullable PgTable v2Table) {
        Set<String> v1Indexes = v1Table != null ? v1Table.getIndicesByName().keySet() : new HashSet<>();
        Set<String> v2Indexes = v2Table != null ? v2Table.getIndicesByName().keySet() : new HashSet<>();

        Set<String> allIndexes = new LinkedHashSet<>(v1Indexes);
        allIndexes.addAll(v2Indexes);

        for (String indexName : allIndexes) {
            PgIndex v1Index = v1Table != null ? v1Table.getIndex(indexName) : null;
            PgIndex v2Index = v2Table != null ? v2Table.getIndex(indexName) : null;

            if (options.isIgnoreIndex(indexName)) {
                if (v2Index != null) {
                    log.warn("Ignored index {}, but exists in target schema", indexName);
                } else {
                    log.debug("Ignored index: {}", indexName);
                }
            } else {
                if (v1Index == null && v2Index != null) {
                    // create new column
                    diff.addDiffItem(DiffItem.ofInsert(v2Index));
                } else if (v1Index != null && v2Index == null) {
                    // delete column
                    diff.addDiffItem(DiffItem.ofRemove(v1Index));
                } else if (v1Index != null && v2Index != null) {
                    // check column for changes
                    compareIndexChanges(diff, v1Index, v2Index);
                }
            }
        }
    }

    private void compareViews(Diff diff) {
        Set<String> v1Views = diff.getSourceSchema().getViewsByName().keySet();
        Set<String> v2Views = diff.getDestinationSchema().getViewsByName().keySet();

        Set<String> allViews = new LinkedHashSet<>(v1Views);
        allViews.addAll(v2Views);

        for (String viewName : allViews) {
            PgView v1View = diff.getSourceSchema().getView(viewName);
            PgView v2View = diff.getDestinationSchema().getView(viewName);

            if (options.isIgnoreView(viewName)) {
                if (v2View != null) {
                    log.warn("Ignored view {}, but exists in target schema", viewName);
                } else {
                    log.debug("Ignored view: {}", viewName);
                }
            } else {
                if (v1View == null && v2View != null) {
                    if (viewName.equalsIgnoreCase("pg_stat_statements") &&
                            !diff.getSourceSchema().getExtensionsByName().containsKey("pg_stat_statements")) {
                        // ignore creating pg_stat_statements view when pg_stat_statements extensions is not added
                    } else {
                        // create new view
                        diff.addDiffItem(DiffItem.ofInsert(v2View));
                    }
                } else if (v1View != null && v2View == null) {
                    if (viewName.equalsIgnoreCase("pg_stat_statements") &&
                            !diff.getDestinationSchema().getExtensionsByName().containsKey("pg_stat_statements")) {
                        // ignore deleting pg_stat_statements view when pg_stat_statements extensions is added
                    } else {
                        // delete view
                        diff.addDiffItem(DiffItem.ofRemove(v1View));
                    }
                } else if (v1View != null && v2View != null) {
                    compareViewChanges(diff, v1View, v2View);
                }
            }
        }
    }

    private void compareViewChanges(Diff diff, PgView v1Index, PgView v2Index) {
        String v1Definition = v1Index.getDefinition().trim().toLowerCase();
        String v2Definition = v2Index.getDefinition().trim().toLowerCase();

        v1Definition = v1Definition.replaceAll("\\s+", "");
        v2Definition = v2Definition.replaceAll("\\s+", "");

        if (!v1Definition.equals(v2Definition)) {
            diff.addDiffItem(DiffItem.ofChange(v1Index, v2Index));
        }
    }

    private void compareTableChanges(Diff diff, PgTable v1Table, PgTable v2Table) {
        if (!Objects.equals(v1Table.getDescription(), v2Table.getDescription())) {
            diff.addDiffItem(DiffItem.ofChange(v1Table, v2Table));
        }
    }

    private void compareColumnChanges(Diff diff, PgColumn v1Column, PgColumn v2Column) {
        // TODO foreign keys
        boolean defaultValueChanged = !Objects.equals(v1Column.getDefaultValue(), v2Column.getDefaultValue());
        boolean sequenceChanged = !Objects.equals(v1Column.getSequence(), v2Column.getSequence());
        boolean descriptionChanged = !Objects.equals(v1Column.getDescription(), v2Column.getDescription());
        boolean typeChanged = !Objects.equals(v1Column.getColumnType(), v2Column.getColumnType());
        boolean nullableChanged = !Objects.equals(v1Column.isNullable(), v2Column.isNullable());

        if (defaultValueChanged || sequenceChanged || descriptionChanged || typeChanged || nullableChanged) {
            if (options.isIgnoreColumnDefaultValue(v2Column.getTable().getName(), v2Column.getName())) {
                log.debug("Ignored column default value: {}.{}", v2Column.getTable().getName(), v2Column.getName());
            } else {
                diff.addDiffItem(DiffItem.ofChange(v1Column, v2Column));
            }
        }
    }

    private void compareIndexChanges(Diff diff, PgIndex v1Index, PgIndex v2Index) {
        String v1Definition = v1Index.getDefinition().trim().toLowerCase();
        String v2Definition = v2Index.getDefinition().trim().toLowerCase();

        v1Definition = v1Definition.replaceAll("\\s+", "");
        v2Definition = v2Definition.replaceAll("\\s+", "");

        if (!v1Definition.equals(v2Definition)) {
            diff.addDiffItem(DiffItem.ofChange(v1Index, v2Index));
        }
    }

    private void compareConstraints(Diff diff, PgTable v1Table, PgTable v2Table) {
        comparePrimaryKeys(diff, v1Table, v2Table);
        compareUniqueConstraints(diff, v1Table, v2Table);
    }

    private void comparePrimaryKeys(Diff diff, PgTable v1Table, PgTable v2Table) {
        PgPrimaryKey v1PrimaryKey = v1Table != null ? v1Table.getPrimaryKey() : null;
        PgPrimaryKey v2PrimaryKey = v2Table != null ? v2Table.getPrimaryKey() : null;

        if (v2PrimaryKey != null && options.isIgnoreConstraint(v2PrimaryKey.getName())) {
            log.warn("Ignored constraint {}, but exists in target schema", v2PrimaryKey.getName());
            return;
        }

        if (v1PrimaryKey != null && options.isIgnoreConstraint(v1PrimaryKey.getName())) {
            log.debug("Ignored constraint: {}", v1PrimaryKey.getName());
            return;
        }

        if (isIgnoreConstraint(v1PrimaryKey) || isIgnoreConstraint(v2PrimaryKey)) {
            return;
        }

        if (v1PrimaryKey == null && v2PrimaryKey != null) {
            diff.addDiffItem(DiffItem.ofInsert(v2PrimaryKey));
        } else if (v1PrimaryKey != null && v2PrimaryKey == null) {
            diff.addDiffItem(DiffItem.ofRemove(v1PrimaryKey));
        } else if (v1PrimaryKey != null && v2PrimaryKey != null) {
            compareConstraintChanges(diff, v1PrimaryKey, v2PrimaryKey);
        }
    }

    private void compareUniqueConstraints(Diff diff, PgTable v1Table, PgTable v2Table) {
        Set<String> v1UniqueConstraints = v1Table != null ? v1Table.getUniqueConstraintsMap().keySet() : new HashSet<>();
        Set<String> v2UniqueConstraints = v2Table != null ? v2Table.getUniqueConstraintsMap().keySet() : new HashSet<>();

        Set<String> allUniqueConstraints = new LinkedHashSet<>(v1UniqueConstraints);
        allUniqueConstraints.addAll(v2UniqueConstraints);

        for (String uniqueConstraint : allUniqueConstraints) {
            PgUniqueConstraint v1UniqueConstraint = v1Table != null ? v1Table.getUniqueConstraint(uniqueConstraint) : null;
            PgUniqueConstraint v2UniqueConstraint = v2Table != null ? v2Table.getUniqueConstraint(uniqueConstraint) : null;

            if (options.isIgnoreConstraint(uniqueConstraint)) {
                if (v2UniqueConstraint != null) {
                    log.warn("Ignored constraint {}, but exists in target schema", uniqueConstraint);
                } else {
                    log.debug("Ignored constraint: {}", uniqueConstraint);
                }
            } else {
                if (isIgnoreConstraint(v1UniqueConstraint) || isIgnoreConstraint(v2UniqueConstraint)) {
                    continue;
                }

                if (v1UniqueConstraint == null && v2UniqueConstraint != null) {
                    diff.addDiffItem(DiffItem.ofInsert(v2UniqueConstraint));
                } else if (v1UniqueConstraint != null && v2UniqueConstraint == null) {
                    diff.addDiffItem(DiffItem.ofRemove(v1UniqueConstraint));
                } else if (v1UniqueConstraint != null && v2UniqueConstraint != null) {
                    compareConstraintChanges(diff, v1UniqueConstraint, v2UniqueConstraint);
                }
            }
        }
    }

    private void compareConstraintChanges(Diff diff, PgConstraint v1Constraint, PgConstraint v2Constraint) {
        List<String> v1Columns = v1Constraint.getColumns() != null ?
                v1Constraint.getColumns().stream()
                        .map(PgColumn::getName)
                        .collect(Collectors.toList())
                : null;
        List<String> v2Columns = v2Constraint.getColumns() != null ?
                v2Constraint.getColumns().stream()
                        .map(PgColumn::getName)
                        .collect(Collectors.toList())
                : null;

        if (!Objects.equals(v1Constraint.getName(), v2Constraint.getName()) ||
                !CollectionUtils.isEqualCollection(v1Columns, v2Columns)) {
            diff.addDiffItem(DiffItem.ofChange(v1Constraint, v2Constraint));
        }
    }

    private void compareForeignKeysByName(Diff diff) {
        Set<String> v1ForeignKeys = diff.getSourceSchema().getForeignKeysByName().keySet();
        Set<String> v2ForeignKeys = diff.getDestinationSchema().getForeignKeysByName().keySet();

        Set<String> allForeignKeys = new LinkedHashSet<>(v1ForeignKeys);
        allForeignKeys.addAll(v2ForeignKeys);

        for (String foreignKeyName : allForeignKeys) {
            PgForeignKey v1ForeignKey = diff.getSourceSchema().getForeignKeyByName(foreignKeyName);
            PgForeignKey v2ForeignKey = diff.getDestinationSchema().getForeignKeyByName(foreignKeyName);

            if (options.isIgnoreConstraint(foreignKeyName)) {
                if (v2ForeignKey != null) {
                    log.warn("Ignored constraint {}, but exists in target schema", foreignKeyName);
                } else {
                    log.debug("Ignored constraint: {}", foreignKeyName);
                }
            } else {
                if (isIgnoreForeignKey(v1ForeignKey) || isIgnoreForeignKey(v2ForeignKey)) {
                    continue;
                }

                if (v1ForeignKey == null && v2ForeignKey != null) {
                    // create new foreign key
                    diff.addDiffItem(DiffItem.ofInsert(v2ForeignKey));
                } else if (v1ForeignKey != null && v2ForeignKey == null) {
                    // delete foreign key
                    diff.addDiffItem(DiffItem.ofRemove(v1ForeignKey));
                } else if (v1ForeignKey != null && v2ForeignKey != null) {
                    if (!v1ForeignKey.equals(v2ForeignKey)) {
                        diff.addDiffItem(DiffItem.ofChange(v1ForeignKey, v2ForeignKey));
                    }
                }
            }
        }
    }

    private void compareForeignKeysByReferences(Diff diff) {
        Set<String> v1ForeignKeys = diff.getSourceSchema().getForeignKeysByHash().keySet();
        Set<String> v2ForeignKeys = diff.getDestinationSchema().getForeignKeysByHash().keySet();

        Set<String> allForeignKeys = new LinkedHashSet<>(v1ForeignKeys);
        allForeignKeys.addAll(v2ForeignKeys);

        for (String foreignKeyHash : allForeignKeys) {
            PgForeignKey v1ForeignKey = diff.getSourceSchema().getForeignKeyByHash(foreignKeyHash);
            PgForeignKey v2ForeignKey = diff.getDestinationSchema().getForeignKeyByHash(foreignKeyHash);

            if (v1ForeignKey != null && options.isIgnoreConstraint(v1ForeignKey.getName())) {
                if (v2ForeignKey != null) {
                    log.warn("Ignored constraint {}, but exists in target schema", v1ForeignKey.getName());
                } else {
                    log.debug("Ignored constraint: {}", v1ForeignKey.getName());
                }
            } else if (v2ForeignKey != null && options.isIgnoreConstraint(v2ForeignKey.getName())) {
                log.warn("Ignored constraint {}, but exists in target schema", v2ForeignKey.getName());
            } else {
                if (isIgnoreForeignKey(v1ForeignKey) || isIgnoreForeignKey(v2ForeignKey)) {
                    continue;
                }

                if (v1ForeignKey == null && v2ForeignKey != null) {
                    // create new foreign key
                    diff.addDiffItem(DiffItem.ofInsert(v2ForeignKey));
                } else if (v1ForeignKey != null && v2ForeignKey == null) {
                    // delete foreign key
                    diff.addDiffItem(DiffItem.ofRemove(v1ForeignKey));
                } else if (v1ForeignKey != null && v2ForeignKey != null) {
                    if (!v1ForeignKey.equalsIgnoreName(v2ForeignKey)) {
                        diff.addDiffItem(DiffItem.ofChange(v1ForeignKey, v2ForeignKey));
                    }
                }
            }
        }
    }

    private boolean isIgnoreConstraint(PgConstraint constraint) {
        if (constraint == null) {
            return false;
        }

        if (options.isIgnoreTable(constraint.getTable().getName())) {
            log.debug("Ignored constraint: {}", constraint.getName());
            return true;
        }

        for (PgColumn column : constraint.getColumns()) {
            if (options.isIgnoreColumn(column.getTable().getName(), column.getName())) {
                log.debug("Ignored constraint: {}", constraint.getName());
                return true;
            }
        }

        return false;
    }

    private boolean isIgnoreForeignKey(PgForeignKey foreignKey) {
        if (foreignKey == null) {
            return false;
        }

        if (options.isIgnoreTable(foreignKey.getColumn().getTable().getName())) {
            log.debug("Ignored constraint: {}", foreignKey.getName());
            return true;
        }

        if (options.isIgnoreTable(foreignKey.getReference().getTable().getName())) {
            log.debug("Ignored constraint: {}", foreignKey.getName());
            return true;
        }

        if (options.isIgnoreColumn(foreignKey.getColumn().getTable().getName(), foreignKey.getColumn().getName())) {
            log.debug("Ignored constraint: {}", foreignKey.getName());
            return true;
        }

        if (options.isIgnoreColumn(foreignKey.getReference().getTable().getName(), foreignKey.getReference().getName())) {
            log.debug("Ignored constraint: {}", foreignKey.getName());
            return true;
        }

        return false;
    }

}
