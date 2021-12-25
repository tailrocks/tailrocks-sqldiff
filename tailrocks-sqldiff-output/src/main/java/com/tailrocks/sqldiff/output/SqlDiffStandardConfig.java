package com.tailrocks.sqldiff.output;

import com.tailrocks.sqldiff.model.config.SqlDiffDiffConfig;

public class SqlDiffStandardConfig {

    private Diff diff;

    public Diff getDiff() {
        return diff;
    }

    public void setDiff(Diff diff) {
        this.diff = diff;
    }

    public static class Diff extends SqlDiffDiffConfig {

        private Ignore ignore;
        private Migration migration;

        @Override
        public Ignore getIgnore() {
            return ignore;
        }

        @Override
        public Migration getMigration() {
            return migration;
        }

        public void setIgnore(Ignore ignore) {
            this.ignore = ignore;
        }

        public void setMigration(Migration migration) {
            this.migration = migration;
        }

        public static class Ignore extends SqlDiffDiffConfig.Ignore {
        }

        public static class Migration extends SqlDiffDiffConfig.Migration {

            private Tables tables;
            private Columns columns;
            private Indexes indexes;
            private Sequences sequences;
            private Safe safe;

            @Override
            public Tables getTables() {
                return tables;
            }

            @Override
            public Columns getColumns() {
                return columns;
            }

            @Override
            public Indexes getIndexes() {
                return indexes;
            }

            @Override
            public Sequences getSequences() {
                return sequences;
            }

            @Override
            public Safe getSafe() {
                return safe;
            }

            public void setTables(Tables tables) {
                this.tables = tables;
            }

            public void setColumns(Columns columns) {
                this.columns = columns;
            }

            public void setIndexes(Indexes indexes) {
                this.indexes = indexes;
            }

            public void setSequences(Sequences sequences) {
                this.sequences = sequences;
            }

            public void setSafe(Safe safe) {
                this.safe = safe;
            }

            public static class Tables extends SqlDiffDiffConfig.Migration.Tables {

                private Drop drop;

                @Override
                public Drop getDrop() {
                    return drop;
                }

                public void setDrop(Drop drop) {
                    this.drop = drop;
                }

                public static class Drop extends SqlDiffDiffConfig.Migration.Tables.Drop {
                }

            }

            public static class Columns extends SqlDiffDiffConfig.Migration.Columns {

                private Add add;
                private Drop drop;

                @Override
                public Add getAdd() {
                    return add;
                }

                @Override
                public Drop getDrop() {
                    return drop;
                }

                public void setAdd(Add add) {
                    this.add = add;
                }

                public void setDrop(Drop drop) {
                    this.drop = drop;
                }

                public static class Add extends SqlDiffDiffConfig.Migration.Columns.Add {
                }

                public static class Drop extends SqlDiffDiffConfig.Migration.Columns.Drop {
                }

            }

            public static class Indexes extends SqlDiffDiffConfig.Migration.Indexes {

                private Create create;
                private Drop drop;

                @Override
                public Create getCreate() {
                    return create;
                }

                @Override
                public Drop getDrop() {
                    return drop;
                }

                public void setCreate(Create create) {
                    this.create = create;
                }

                public void setDrop(Drop drop) {
                    this.drop = drop;
                }

                public static class Create extends SqlDiffDiffConfig.Migration.Indexes.Create {
                }

                public static class Drop extends SqlDiffDiffConfig.Migration.Indexes.Drop {
                }
            }

            public static class Sequences extends SqlDiffDiffConfig.Migration.Sequences {

                private Drop drop;

                @Override
                public Drop getDrop() {
                    return drop;
                }

                public void setDrop(Drop drop) {
                    this.drop = drop;
                }

                public static class Drop extends SqlDiffDiffConfig.Migration.Sequences.Drop {
                }

            }

            public static class Safe extends SqlDiffDiffConfig.Migration.Safe {

                private Add add;

                @Override
                public Add getAdd() {
                    return add;
                }

                public void setAdd(Add add) {
                    this.add = add;
                }

                public static class Add extends SqlDiffDiffConfig.Migration.Safe.Add {
                }

            }
        }

    }

}
