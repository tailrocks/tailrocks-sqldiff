package com.scentbird.krendel.model.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class KrendelDiffConfig {

    private ForeignKeyCompareMethod foreignKeyCompareMethod = ForeignKeyCompareMethod.NAME;

    public ForeignKeyCompareMethod getForeignKeyCompareMethod() {
        return foreignKeyCompareMethod;
    }

    public void setForeignKeyCompareMethod(ForeignKeyCompareMethod foreignKeyCompareMethod) {
        this.foreignKeyCompareMethod = foreignKeyCompareMethod;
    }

    public abstract Ignore getIgnore();

    public abstract Migration getMigration();

    public static abstract class Ignore {

        /**
         * The list of extension names to ignore.
         */
        private List<String> extensions = new ArrayList<>();

        /**
         * The list of enum names to ignore.
         */
        private List<String> enums = new ArrayList<>();

        /**
         * The list of table names to ignore.
         */
        private List<String> tables = new ArrayList<>();

        /**
         * The list of sequence names to ignore.
         */
        private List<String> sequences = new ArrayList<>();

        /**
         * The list of index names to ignore.
         */
        private List<String> indexes = new ArrayList<>();

        /**
         * The list of constraint names to ignore.
         */
        private List<String> constraints = new ArrayList<>();

        /**
         * The list of view names to ignore.
         */
        private List<String> views = new ArrayList<>();

        /**
         * The list of tables with columns to ignore. All related operations will be ignored, for example creating
         * a new foreign key references column from this list also will be ignored.
         */
        private Map<String, List<String>> columns = new HashMap<>();

        /**
         * The list of columns in some tables to ignore default value changes. Ignore only default value changes.
         * The new column will still created with default value if it set.
         */
        private Map<String, List<String>> columnsDefaultValue = new HashMap<>();

        public List<String> getExtensions() {
            return extensions;
        }

        public void setExtensions(List<String> extensions) {
            this.extensions = extensions;
        }

        public List<String> getEnums() {
            return enums;
        }

        public void setEnums(List<String> enums) {
            this.enums = enums;
        }

        public List<String> getTables() {
            return tables;
        }

        public void setTables(List<String> tables) {
            this.tables = tables;
        }

        public List<String> getSequences() {
            return sequences;
        }

        public void setSequences(List<String> sequences) {
            this.sequences = sequences;
        }

        public List<String> getIndexes() {
            return indexes;
        }

        public void setIndexes(List<String> indexes) {
            this.indexes = indexes;
        }

        public List<String> getConstraints() {
            return constraints;
        }

        public void setConstraints(List<String> constraints) {
            this.constraints = constraints;
        }

        public List<String> getViews() {
            return views;
        }

        public void setViews(List<String> views) {
            this.views = views;
        }

        public Map<String, List<String>> getColumns() {
            return columns;
        }

        public void setColumns(Map<String, List<String>> columns) {
            this.columns = columns;
        }

        public Map<String, List<String>> getColumnsDefaultValue() {
            return columnsDefaultValue;
        }

        public void setColumnsDefaultValue(Map<String, List<String>> columnsDefaultValue) {
            this.columnsDefaultValue = columnsDefaultValue;
        }

        public boolean isEmpty() {
            return extensions.isEmpty() &&
                    enums.isEmpty() &&
                    tables.isEmpty() &&
                    sequences.isEmpty() &&
                    indexes.isEmpty() &&
                    constraints.isEmpty() &&
                    views.isEmpty() &&
                    columns.isEmpty() &&
                    columnsDefaultValue.isEmpty();
        }

    }

    public static abstract class Migration {

        private boolean ignoreHint;

        public boolean isIgnoreHint() {
            return ignoreHint;
        }

        public void setIgnoreHint(boolean ignoreHint) {
            this.ignoreHint = ignoreHint;
        }

        public abstract Tables getTables();

        public abstract Columns getColumns();

        public abstract Indexes getIndexes();

        public abstract Sequences getSequences();

        public abstract Safe getSafe();

        public static abstract class Tables {

            public abstract Drop getDrop();

            public static abstract class Drop {

                /**
                 * Adding "IF EXISTS" condition to the "DROP TABLE" statement.
                 */
                private boolean ifExists;

                public boolean isIfExists() {
                    return ifExists;
                }

                public void setIfExists(boolean ifExists) {
                    this.ifExists = ifExists;
                }

            }
        }

        public static abstract class Columns {

            public abstract Add getAdd();

            public abstract Drop getDrop();

            public static abstract class Add {

                /**
                 * Adding "IF NOT EXISTS" condition to the "ADD COLUMN" statement.
                 */
                private boolean ifNotExists;

                public boolean isIfNotExists() {
                    return ifNotExists;
                }

                public void setIfNotExists(boolean ifNotExists) {
                    this.ifNotExists = ifNotExists;
                }

            }

            public static abstract class Drop {

                /**
                 * Adding "IF EXISTS" condition to the "DROP COLUMN" statement.
                 */
                private boolean ifExists;

                public boolean isIfExists() {
                    return ifExists;
                }

                public void setIfExists(boolean ifExists) {
                    this.ifExists = ifExists;
                }

            }

        }

        public static abstract class Indexes {

            public abstract Create getCreate();

            public abstract Drop getDrop();

            public static abstract class Create {

                /**
                 * Adding "IF NOT EXISTS" condition to the "CREATE INDEX" statement.
                 */
                private boolean ifNotExists;

                /**
                 * Adding "CONCURRENTLY" condition to the "CREATE INDEX" statement.
                 */
                private boolean concurrently;

                public boolean isIfNotExists() {
                    return ifNotExists;
                }

                public void setIfNotExists(boolean ifNotExists) {
                    this.ifNotExists = ifNotExists;
                }

                public boolean isConcurrently() {
                    return concurrently;
                }

                public void setConcurrently(boolean concurrently) {
                    this.concurrently = concurrently;
                }

            }

            public static abstract class Drop {

                /**
                 * Adding "IF EXISTS" condition to the "DROP INDEX" statement.
                 */
                private boolean ifExists;

                /**
                 * Adding "CONCURRENTLY" condition to the "DROP INDEX" statement.
                 */
                private boolean concurrently;

                public boolean isIfExists() {
                    return ifExists;
                }

                public void setIfExists(boolean ifExists) {
                    this.ifExists = ifExists;
                }

                public boolean isConcurrently() {
                    return concurrently;
                }

                public void setConcurrently(boolean concurrently) {
                    this.concurrently = concurrently;
                }

            }

        }

        public static abstract class Sequences {

            public abstract Drop getDrop();

            public static abstract class Drop {

                /**
                 * Adding "IF EXISTS" condition to the "DROP SEQUENCE" statement.
                 */
                private boolean ifExists;

                public boolean isIfExists() {
                    return ifExists;
                }

                public void setIfExists(boolean ifExists) {
                    this.ifExists = ifExists;
                }

            }
        }

        public static abstract class Safe {

            public abstract Add getAdd();

            public static abstract class Add {

                /**
                 * Processed adding column with default value in two steps:
                 * 1) add a new column without default value
                 * 2) set default value to the new column.
                 */
                private boolean defaultColumns;

                /**
                 * Process adding column with not null condition in two or three steps:
                 * 1) add a new column with nullable condition
                 * 2) (optional) fill table records with null values with default value (if default value is set on
                 * the column)
                 * 3) process "ALTER COLUMN ... SET NOT NULL" statement.
                 */
                private boolean notNullColumns;

                /**
                 * Generate "ALTER TABLE ... ADD CONSTRAINT ... FOREIGN KEY" query with "NOT VALID" option. It will
                 * also generate create an "VALIDATE CONSTRAINT" query. Especially useful when need to add Foreign
                 * Keys with zero downtime.
                 */
                private boolean foreignKeys;

                public boolean isDefaultColumns() {
                    return defaultColumns;
                }

                public void setDefaultColumns(boolean defaultColumns) {
                    this.defaultColumns = defaultColumns;
                }

                public boolean isNotNullColumns() {
                    return notNullColumns;
                }

                public void setNotNullColumns(boolean notNullColumns) {
                    this.notNullColumns = notNullColumns;
                }

                public boolean isForeignKeys() {
                    return foreignKeys;
                }

                public void setForeignKeys(boolean foreignKeys) {
                    this.foreignKeys = foreignKeys;
                }

            }

        }

    }

    public enum ForeignKeyCompareMethod {

        /**
         * Compare foreign keys by name. That means we will compare foreign key from source schema and target
         * schema use their naming, for example `orders` table in source schema have FK linked to `users` table,
         * and target schema also have same foreign key but with different name, in this case Krendel will generate
         * migration queries which contains two queries, one delete FK from source schema and one create FK based
         * on naming from target schema. This is default value.
         */
        NAME,

        /**
         * Compare foreign keys by references. That means we will ignore naming, if source and target schema both
         * have foreign keys with same linking and difference name, Krendel will not generate any migration
         * queries, even if name of these FK links are different.
         */
        REFERENCES

    }

}
