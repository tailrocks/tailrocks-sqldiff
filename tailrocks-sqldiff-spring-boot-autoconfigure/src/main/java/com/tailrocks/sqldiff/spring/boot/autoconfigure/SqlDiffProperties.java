package com.tailrocks.sqldiff.spring.boot.autoconfigure;

import com.tailrocks.sqldiff.model.config.SqlDiffDiffConfig;
import com.tailrocks.sqldiff.model.config.KrendelEmbeddedConfig;
import com.tailrocks.sqldiff.model.config.KrendelFlywayConfig;
import com.tailrocks.sqldiff.model.config.KrendelMigrationConfig;
import com.tailrocks.sqldiff.model.config.SqlDiffMigrationMetadataConfig;
import com.tailrocks.sqldiff.model.config.KrendelTargetConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "krendel")
public class KrendelProperties extends KrendelEmbeddedConfig {

    @NestedConfigurationProperty
    private final Target target = new Target();

    @NestedConfigurationProperty
    private final Flyway flyway = new Flyway();

    @NestedConfigurationProperty
    private final Migration migration = new Migration();

    @NestedConfigurationProperty
    private final Diff diff = new Diff();

    @Override
    public Target getTarget() {
        return target;
    }

    @Override
    public Flyway getFlyway() {
        return flyway;
    }

    @Override
    public Migration getMigration() {
        return migration;
    }

    @Override
    public Diff getDiff() {
        return diff;
    }

    public static class Target extends KrendelTargetConfig {
    }

    public static class Flyway extends KrendelFlywayConfig {
    }

    public static class Migration extends KrendelMigrationConfig {

        @NestedConfigurationProperty
        private final Metadata metadata = new Metadata();

        @Override
        public Metadata getMetadata() {
            return metadata;
        }

        public static class Metadata extends SqlDiffMigrationMetadataConfig {
        }

    }

    public static class Diff extends SqlDiffDiffConfig {

        @NestedConfigurationProperty
        private final Ignore ignore = new Ignore();

        @NestedConfigurationProperty
        private final Migration migration = new Migration();

        @Override
        public Ignore getIgnore() {
            return ignore;
        }

        @Override
        public Migration getMigration() {
            return migration;
        }

        public static class Ignore extends SqlDiffDiffConfig.Ignore {
        }

        public static class Migration extends SqlDiffDiffConfig.Migration {

            @NestedConfigurationProperty
            private final Tables tables = new Tables();

            @NestedConfigurationProperty
            private final Columns columns = new Columns();

            @NestedConfigurationProperty
            private final Indexes indexes = new Indexes();

            @NestedConfigurationProperty
            private final Sequences sequences = new Sequences();

            @NestedConfigurationProperty
            private final Safe safe = new Safe();

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

            public static class Tables extends SqlDiffDiffConfig.Migration.Tables {

                @NestedConfigurationProperty
                private final Drop drop = new Drop();

                @Override
                public Drop getDrop() {
                    return drop;
                }

                public static class Drop extends SqlDiffDiffConfig.Migration.Tables.Drop {
                }

            }

            public static class Columns extends SqlDiffDiffConfig.Migration.Columns {

                @NestedConfigurationProperty
                private final Add add = new Add();

                @NestedConfigurationProperty
                private final Drop drop = new Drop();

                @Override
                public Add getAdd() {
                    return add;
                }

                @Override
                public Drop getDrop() {
                    return drop;
                }

                public static class Add extends SqlDiffDiffConfig.Migration.Columns.Add {
                }

                public static class Drop extends SqlDiffDiffConfig.Migration.Columns.Drop {
                }

            }

            public static class Indexes extends SqlDiffDiffConfig.Migration.Indexes {

                @NestedConfigurationProperty
                private final Create create = new Create();

                @NestedConfigurationProperty
                private final Drop drop = new Drop();

                @Override
                public Create getCreate() {
                    return create;
                }

                @Override
                public Drop getDrop() {
                    return drop;
                }

                public static class Create extends SqlDiffDiffConfig.Migration.Indexes.Create {
                }

                public static class Drop extends SqlDiffDiffConfig.Migration.Indexes.Drop {
                }
            }

            public static class Sequences extends SqlDiffDiffConfig.Migration.Sequences {

                @NestedConfigurationProperty
                private final Drop drop = new Drop();

                @Override
                public Drop getDrop() {
                    return drop;
                }

                public static class Drop extends SqlDiffDiffConfig.Migration.Sequences.Drop {
                }

            }

            public static class Safe extends SqlDiffDiffConfig.Migration.Safe {

                @NestedConfigurationProperty
                private final Add add = new Add();

                @Override
                public Add getAdd() {
                    return add;
                }

                public static class Add extends SqlDiffDiffConfig.Migration.Safe.Add {
                }

            }
        }
    }

}
