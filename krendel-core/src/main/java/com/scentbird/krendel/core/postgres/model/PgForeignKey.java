package com.scentbird.krendel.core.postgres.model;

import java.util.Objects;

public class PgForeignKey implements PgElement {

    private String name;
    private PgColumn column;
    private PgColumn reference;

    private String hash;

    public PgForeignKey(String name, PgColumn column, PgColumn reference) {
        Objects.requireNonNull(name, "`name` can not be null");
        Objects.requireNonNull(column, "`column` can not be null");
        Objects.requireNonNull(reference, "`reference` can not be null");

        this.name = name;
        this.column = column;
        this.reference = reference;

        hash = column.getTable().getName() + "." +
                column.getName() + "<->" +
                reference.getTable().getName() + "." +
                reference.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PgColumn getColumn() {
        return column;
    }

    public PgColumn getReference() {
        return reference;
    }

    public String getHash() {
        return hash;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, column.getTable().getName(), column.getName(), reference.getTable().getName(), reference.getName());
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof PgForeignKey) {
            PgForeignKey other = (PgForeignKey) obj;
            if (column == null) {
                return false;
            }
            if (reference == null) {
                return false;
            }
            if (other.getColumn() == null) {
                return false;
            }
            if (other.getReference() == null) {
                return false;
            }
            return Objects.equals(name, other.name)
                    && Objects.equals(column.getName(), other.column.getName())
                    && Objects.equals(column.getTable().getName(), other.column.getTable().getName())
                    && Objects.equals(reference.getName(), other.reference.getName())
                    && Objects.equals(reference.getTable().getName(), other.reference.getTable().getName());
        } else {
            return false;
        }
    }

    public boolean equalsIgnoreName(PgForeignKey other) {
        if (this == other) {
            return true;
        }
        if (column == null) {
            return false;
        }
        if (reference == null) {
            return false;
        }
        if (other.getColumn() == null) {
            return false;
        }
        if (other.getReference() == null) {
            return false;
        }
        return Objects.equals(column.getName(), other.column.getName())
                && Objects.equals(column.getTable().getName(), other.column.getTable().getName())
                && Objects.equals(reference.getName(), other.reference.getName())
                && Objects.equals(reference.getTable().getName(), other.reference.getTable().getName());
    }

}
