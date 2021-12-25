package com.scentbird.krendel.core.postgres.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PgTable implements PgElement {

    private PgSchema schema;

    private String name;

    private List<PgColumn> columns = new ArrayList<>();
    private Map<String, PgColumn> columnsByName = new LinkedHashMap<>();
    private Map<String, PgIndex> indicesByName = new LinkedHashMap<>();
    private Map<String, PgTable> childrenByName = new LinkedHashMap<>();
    private PgTable parent;

    private List<PgUniqueConstraint> uniqueConstraints = new ArrayList<>();
    private Map<String, PgUniqueConstraint> uniqueConstraintsMap = new LinkedHashMap<>();
    private Map<String, PgForeignKey> foreignKeysByName = new LinkedHashMap<>();

    @Nullable
    private PgPrimaryKey primaryKey;

    @Nullable
    private PgSequence sequence;

    @Nullable
    private String description;

    private String relKind;
    private boolean isPartition;
    private String partitionKeyDefinition;
    private String partitionBound;

    //todo triggers
    //todo rules

    public PgTable(String name) {
        this.name = name;
    }

    public void addColumn(PgColumn column) {
        Objects.requireNonNull(column);

        column.setTable(this);

        columns.add(column);
        columnsByName.put(column.getName(), column);
    }

    public PgIndex addIndex(String name, String definition) {
        PgIndex item = getOrCreateIndex(name);
        item.setDefinition(definition);

        indicesByName.put(item.getName(), item);

        return item;
    }

    public void addChild(PgTable child) {
        childrenByName.put(child.getName(), child);
        child.parent = this;
    }

    public PgPrimaryKey addPrimaryKey(@NotNull String name, @NotNull PgColumn column) {
        if (this.primaryKey != null) {
            this.primaryKey.addColumn(column);
            return this.primaryKey;
        }
        this.primaryKey = new PgPrimaryKey(this, name, column);
        return this.primaryKey;
    }

    public PgUniqueConstraint addUniqueConstraint(@NotNull String name, @NotNull PgColumn column) {
        PgUniqueConstraint uniqueConstraint = getOrCreateUniqueConstraint(name, column);

        uniqueConstraints.add(uniqueConstraint);
        uniqueConstraintsMap.put(name, uniqueConstraint);

        return uniqueConstraint;
    }

    public PgForeignKey addForeignKey(@NotNull String constraintName, @NotNull String columnName,
                                      @NotNull PgColumn reference) {
        PgColumn column = getColumn(columnName);
        if (column == null) {
            throw new IllegalStateException("Column " + columnName + " not found in table " + getName());
        }

        PgForeignKey foreignKey = new PgForeignKey(constraintName, column, reference);

        foreignKeysByName.put(foreignKey.getName(), foreignKey);
        schema.getForeignKeysByName().put(foreignKey.getName(), foreignKey);
        schema.getForeignKeysByHash().put(foreignKey.getHash(), foreignKey);

        return foreignKey;
    }

    private PgUniqueConstraint getOrCreateUniqueConstraint(@NotNull String name, @NotNull PgColumn column) {
        PgUniqueConstraint uniqueConstraint = uniqueConstraintsMap.getOrDefault(name, new PgUniqueConstraint(this, name));
        uniqueConstraint.addColumn(column);
        return uniqueConstraint;
    }

    public PgSchema getSchema() {
        return schema;
    }

    public void setSchema(PgSchema schema) {
        this.schema = schema;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PgColumn> getColumns() {
        return columns;
    }

    public Map<String, PgColumn> getColumnsByName() {
        return columnsByName;
    }

    public PgColumn getColumn(String name) {
        return columnsByName.get(name);
    }

    public Map<String, PgIndex> getIndicesByName() {
        return indicesByName;
    }

    public PgIndex getIndex(String name) {
        return indicesByName.get(name);
    }

    public void setColumns(List<PgColumn> columns) {
        this.columns = columns;
    }

    public PgPrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(PgPrimaryKey primaryKey) {
        this.primaryKey = primaryKey;
    }

    public PgSequence getSequence() {
        return sequence;
    }

    public void setSequence(PgSequence sequence) {
        this.sequence = sequence;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PgUniqueConstraint> getUniqueConstraints() {
        return uniqueConstraints;
    }

    public Map<String, PgUniqueConstraint> getUniqueConstraintsMap() {
        return uniqueConstraintsMap;
    }

    public PgUniqueConstraint getUniqueConstraint(String name) {
        return uniqueConstraintsMap.get(name);
    }

    private PgIndex getOrCreateIndex(String name) {
        return indicesByName.getOrDefault(name, new PgIndex(this, name));
    }

    public String getRelKind() {
        return relKind;
    }

    public void setRelKind(String relKind) {
        this.relKind = relKind;
    }

    public boolean isPartition() {
        return isPartition;
    }

    public void setPartition(boolean partition) {
        isPartition = partition;
    }

    public String getPartitionKeyDefinition() {
        return partitionKeyDefinition;
    }

    public void setPartitionKeyDefinition(String partitionKeyDefinition) {
        this.partitionKeyDefinition = partitionKeyDefinition;
    }

    public String getPartitionBound() {
        return partitionBound;
    }

    public void setPartitionBound(String partitionBound) {
        this.partitionBound = partitionBound;
    }

    public Map<String, PgTable> getChildrenByName() {
        return childrenByName;
    }

    public PgTable getParent() {
        return parent;
    }

}
