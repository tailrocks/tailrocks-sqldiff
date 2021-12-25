package com.tailrocks.sqldiff.micronaut;

import com.tailrocks.sqldiff.model.config.SqlDiffDiffConfig;
import com.tailrocks.sqldiff.model.config.KrendelEmbeddedConfig;
import com.tailrocks.sqldiff.model.config.KrendelFlywayConfig;
import com.tailrocks.sqldiff.model.config.KrendelMigrationConfig;
import com.tailrocks.sqldiff.model.config.SqlDiffMigrationMetadataConfig;
import com.tailrocks.sqldiff.model.config.KrendelTargetConfig;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.util.Toggleable;

@ConfigurationProperties(KrendelConfiguration.PREFIX)
public class KrendelConfiguration extends KrendelEmbeddedConfig implements Toggleable {

    public static final String PREFIX = "krendel";

    private final TargetConfiguration target;
    private final FlywayConfiguration flyway;
    private final MigrationConfiguration migration;
    private final DiffConfiguration diff;

    public KrendelConfiguration(TargetConfiguration target,
                                FlywayConfiguration flyway,
                                MigrationConfiguration migration,
                                DiffConfiguration diff) {
        this.target = target;
        this.flyway = flyway;
        this.migration = migration;
        this.diff = diff;
    }

    @Override
    public TargetConfiguration getTarget() {
        return target;
    }

    @Override
    public FlywayConfiguration getFlyway() {
        return flyway;
    }

    @Override
    public MigrationConfiguration getMigration() {
        return migration;
    }

    @Override
    public DiffConfiguration getDiff() {
        return diff;
    }

    @ConfigurationProperties("target")
    public static class TargetConfiguration extends KrendelTargetConfig {
    }

    @ConfigurationProperties("flyway")
    public static class FlywayConfiguration extends KrendelFlywayConfig {
    }

    @ConfigurationProperties("migration")
    public static class MigrationConfiguration extends KrendelMigrationConfig {

        private final MetadataConfiguration metadata;

        public MigrationConfiguration(MetadataConfiguration metadataConfiguration) {
            metadata = metadataConfiguration;
        }

        @Override
        public SqlDiffMigrationMetadataConfig getMetadata() {
            return metadata;
        }

        @ConfigurationProperties("metadata")
        public static class MetadataConfiguration extends SqlDiffMigrationMetadataConfig {
        }

    }

    @ConfigurationProperties("diff")
    public static class DiffConfiguration extends SqlDiffDiffConfig {

        private final IgnoreConfiguration ignore;
        private final MigrationConfiguration migration;

        public DiffConfiguration(IgnoreConfiguration ignore, MigrationConfiguration migration) {
            this.ignore = ignore;
            this.migration = migration;
        }

        @Override
        public IgnoreConfiguration getIgnore() {
            return ignore;
        }

        @Override
        public MigrationConfiguration getMigration() {
            return migration;
        }


        @ConfigurationProperties("ignore")
        public static class IgnoreConfiguration extends SqlDiffDiffConfig.Ignore {
        }

        @ConfigurationProperties("migration")
        public static class MigrationConfiguration extends SqlDiffDiffConfig.Migration {

            private final TablesConfiguration tables;
            private final ColumnsConfiguration columns;
            private final IndexesConfiguration indexes;
            private final SequencesConfiguration sequences;
            private final SafeConfiguration safe;

            public MigrationConfiguration(TablesConfiguration tables, ColumnsConfiguration columns,
                                          IndexesConfiguration indexes, SequencesConfiguration sequences,
                                          SafeConfiguration safe) {
                this.tables = tables;
                this.columns = columns;
                this.indexes = indexes;
                this.sequences = sequences;
                this.safe = safe;
            }

            @Override
            public TablesConfiguration getTables() {
                return tables;
            }

            @Override
            public ColumnsConfiguration getColumns() {
                return columns;
            }

            @Override
            public IndexesConfiguration getIndexes() {
                return indexes;
            }

            @Override
            public SequencesConfiguration getSequences() {
                return sequences;
            }

            @Override
            public SafeConfiguration getSafe() {
                return safe;
            }

            @ConfigurationProperties("tables")
            public static class TablesConfiguration extends SqlDiffDiffConfig.Migration.Tables {

                private final DropConfiguration drop;

                public TablesConfiguration(DropConfiguration drop) {
                    this.drop = drop;
                }

                @Override
                public DropConfiguration getDrop() {
                    return drop;
                }

                @ConfigurationProperties("drop")
                public static class DropConfiguration extends SqlDiffDiffConfig.Migration.Tables.Drop {
                }

            }

            @ConfigurationProperties("columns")
            public static class ColumnsConfiguration extends SqlDiffDiffConfig.Migration.Columns {

                private final AddConfiguration add;
                private final DropConfiguration drop;

                public ColumnsConfiguration(AddConfiguration add, DropConfiguration drop) {
                    this.add = add;
                    this.drop = drop;
                }

                @Override
                public AddConfiguration getAdd() {
                    return add;
                }

                @Override
                public DropConfiguration getDrop() {
                    return drop;
                }

                @ConfigurationProperties("add")
                public static class AddConfiguration extends SqlDiffDiffConfig.Migration.Columns.Add {
                }

                @ConfigurationProperties("drop")
                public static class DropConfiguration extends SqlDiffDiffConfig.Migration.Columns.Drop {
                }

            }

            @ConfigurationProperties("indexes")
            public static class IndexesConfiguration extends SqlDiffDiffConfig.Migration.Indexes {

                private final CreateConfiguration create;
                private final DropConfiguration drop;

                public IndexesConfiguration(CreateConfiguration create, DropConfiguration drop) {
                    this.create = create;
                    this.drop = drop;
                }

                @Override
                public CreateConfiguration getCreate() {
                    return create;
                }

                @Override
                public DropConfiguration getDrop() {
                    return drop;
                }

                @ConfigurationProperties("create")
                public static class CreateConfiguration extends SqlDiffDiffConfig.Migration.Indexes.Create {
                }

                @ConfigurationProperties("drop")
                public static class DropConfiguration extends SqlDiffDiffConfig.Migration.Indexes.Drop {
                }
            }

            @ConfigurationProperties("sequences")
            public static class SequencesConfiguration extends SqlDiffDiffConfig.Migration.Sequences {

                private final DropConfiguration drop;

                public SequencesConfiguration(DropConfiguration drop) {
                    this.drop = drop;
                }

                @Override
                public DropConfiguration getDrop() {
                    return drop;
                }

                @ConfigurationProperties("drop")
                public static class DropConfiguration extends SqlDiffDiffConfig.Migration.Sequences.Drop {
                }

            }

            @ConfigurationProperties("safe")
            public static class SafeConfiguration extends SqlDiffDiffConfig.Migration.Safe {

                private final AddConfiguration add;

                public SafeConfiguration(AddConfiguration add) {
                    this.add = add;
                }

                @Override
                public AddConfiguration getAdd() {
                    return add;
                }

                @ConfigurationProperties("add")
                public static class AddConfiguration extends SqlDiffDiffConfig.Migration.Safe.Add {
                }

            }
        }
    }

}
