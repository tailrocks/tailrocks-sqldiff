package com.scentbird.krendel.spring.boot.autoconfigure;

import com.tailrocks.sqldiff.model.config.KrendelDiffConfig;
import com.tailrocks.sqldiff.model.config.KrendelEmbeddedConfig;
import com.tailrocks.sqldiff.model.config.KrendelFlywayConfig;
import com.tailrocks.sqldiff.model.config.KrendelMigrationConfig;
import com.tailrocks.sqldiff.model.config.KrendelMigrationMetadataConfig;
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

        public static class Metadata extends KrendelMigrationMetadataConfig {
        }

    }

    public static class Diff extends KrendelDiffConfig {

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

        public static class Ignore extends KrendelDiffConfig.Ignore {
        }

        public static class Migration extends KrendelDiffConfig.Migration {

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

            public static class Tables extends KrendelDiffConfig.Migration.Tables {

                @NestedConfigurationProperty
                private final Drop drop = new Drop();

                @Override
                public Drop getDrop() {
                    return drop;
                }

                public static class Drop extends KrendelDiffConfig.Migration.Tables.Drop {
                }

            }

            public static class Columns extends KrendelDiffConfig.Migration.Columns {

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

                public static class Add extends KrendelDiffConfig.Migration.Columns.Add {
                }

                public static class Drop extends KrendelDiffConfig.Migration.Columns.Drop {
                }

            }

            public static class Indexes extends KrendelDiffConfig.Migration.Indexes {

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

                public static class Create extends KrendelDiffConfig.Migration.Indexes.Create {
                }

                public static class Drop extends KrendelDiffConfig.Migration.Indexes.Drop {
                }
            }

            public static class Sequences extends KrendelDiffConfig.Migration.Sequences {

                @NestedConfigurationProperty
                private final Drop drop = new Drop();

                @Override
                public Drop getDrop() {
                    return drop;
                }

                public static class Drop extends KrendelDiffConfig.Migration.Sequences.Drop {
                }

            }

            public static class Safe extends KrendelDiffConfig.Migration.Safe {

                @NestedConfigurationProperty
                private final Add add = new Add();

                @Override
                public Add getAdd() {
                    return add;
                }

                public static class Add extends KrendelDiffConfig.Migration.Safe.Add {
                }

            }
        }
    }

}
