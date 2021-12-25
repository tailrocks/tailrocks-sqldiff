package com.tailrocks.sqldiff.core.postgres.model;

import org.jetbrains.annotations.Nullable;

public class PgColumn implements PgElement {

    private PgTable table;

    private String name;

    private boolean nullable;
    private String defaultValue;

    @Nullable
    private String description;

    @Nullable
    private PgSequence sequence;

    private PgColumnType columnType ;

    public PgColumn(String name, PgColumnType columnType) {
        this.name = name;
        this.columnType = columnType;
    }

    public PgTable getTable() {
        return table;
    }

    public void setTable(PgTable table) {
        this.table = table;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public PgSequence getSequence() {
        return sequence;
    }

    public void setSequence(PgSequence sequence) {
        this.sequence = sequence;
    }

    public PgColumnType getColumnType() {
        return columnType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
