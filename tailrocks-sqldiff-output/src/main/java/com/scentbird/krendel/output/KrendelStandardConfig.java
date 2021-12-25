package com.scentbird.krendel.output;

import com.scentbird.krendel.model.config.KrendelDiffConfig;

public class KrendelStandardConfig {

    private Diff diff;

    public Diff getDiff() {
        return diff;
    }

    public void setDiff(Diff diff) {
        this.diff = diff;
    }

    public static class Diff extends KrendelDiffConfig {

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

        public static class Ignore extends KrendelDiffConfig.Ignore {
        }

        public static class Migration extends KrendelDiffConfig.Migration {

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

            public static class Tables extends KrendelDiffConfig.Migration.Tables {

                private Drop drop;

                @Override
                public Drop getDrop() {
                    return drop;
                }

                public void setDrop(Drop drop) {
                    this.drop = drop;
                }

                public static class Drop extends KrendelDiffConfig.Migration.Tables.Drop {
                }

            }

            public static class Columns extends KrendelDiffConfig.Migration.Columns {

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

                public static class Add extends KrendelDiffConfig.Migration.Columns.Add {
                }

                public static class Drop extends KrendelDiffConfig.Migration.Columns.Drop {
                }

            }

            public static class Indexes extends KrendelDiffConfig.Migration.Indexes {

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

                public static class Create extends KrendelDiffConfig.Migration.Indexes.Create {
                }

                public static class Drop extends KrendelDiffConfig.Migration.Indexes.Drop {
                }
            }

            public static class Sequences extends KrendelDiffConfig.Migration.Sequences {

                private Drop drop;

                @Override
                public Drop getDrop() {
                    return drop;
                }

                public void setDrop(Drop drop) {
                    this.drop = drop;
                }

                public static class Drop extends KrendelDiffConfig.Migration.Sequences.Drop {
                }

            }

            public static class Safe extends KrendelDiffConfig.Migration.Safe {

                private Add add;

                @Override
                public Add getAdd() {
                    return add;
                }

                public void setAdd(Add add) {
                    this.add = add;
                }

                public static class Add extends KrendelDiffConfig.Migration.Safe.Add {
                }

            }
        }

    }

}
