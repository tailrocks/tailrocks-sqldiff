package com.tailrocks.sqldiff.cli.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.scentbird.krendel.core.postgres.diff.DiffOptions;
import com.scentbird.krendel.core.postgres.migration.MigrationOptions;
import com.scentbird.krendel.core.postgres.migration.MigrationReport;
import com.scentbird.krendel.model.config.KrendelDiffConfig;
import com.scentbird.krendel.output.KrendelStandardConfig;
import com.scentbird.krendel.model.config.KrendelMigrationMetadataConfig;
import com.scentbird.krendel.output.FlywayMigrationGenerator;
import com.scentbird.krendel.output.KrendelOutput;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiStyle;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.Callable;

import static com.scentbird.krendel.output.KrendelOutput.consolePrintln;
import static org.springframework.boot.ansi.AnsiOutput.encode;

@Command(name = "diff", mixinStandardHelpOptions = true, sortOptions = false)
public class DiffCommand implements Callable<Void> {

    @SuppressWarnings({"UnusedDeclaration"})
    @Parameters(index = "0", description = "Source jdbc url (including username/password)")
    private String sourceJdbc;

    @SuppressWarnings({"UnusedDeclaration"})
    @Parameters(index = "1", description = "Target jdbc url (including username/password)")
    private String targetJdbc;

    @SuppressWarnings({"UnusedDeclaration"})
    @Option(names = "--source-username", description = "Source database username")
    private String sourceUsername;

    @SuppressWarnings({"UnusedDeclaration"})
    @Option(names = "--source-password", description = "Source database password")
    private String sourcePassword;

    @SuppressWarnings({"UnusedDeclaration"})
    @Option(names = "--target-username", description = "Target database username")
    private String targetUsername;

    @SuppressWarnings({"UnusedDeclaration"})
    @Option(names = "--target-password", description = "Target database password")
    private String targetPassword;

    @SuppressWarnings({"UnusedDeclaration"})
    @Option(names = "--metadata-url", description = "JDBC url of production database to get metadata (table sizes)")
    private String metadataJdbcUrl;

    @SuppressWarnings({"UnusedDeclaration"})
    @Option(names = "--metadata-username", description = "Username of production database to get metadata")
    private String metadataUsername;

    @SuppressWarnings({"UnusedDeclaration"})
    @Option(names = "--metadata-password", description = "Password of production database to get metadata")
    private String metadataPassword;

    @SuppressWarnings({"UnusedDeclaration"})
    @Option(names = "--metadata-file-path", description = "Path to CSV file with metadata from production")
    private String metadataFilePath;

    @SuppressWarnings({"UnusedDeclaration"})
    @Option(names = "--config", description = "Path to configuration file (yml format)")
    private String configFile;

    @SuppressWarnings({"UnusedDeclaration"})
    @Option(names = "--output-file", description = "Path to output file")
    private String outputFile;

    @SuppressWarnings({"UnusedDeclaration"})
    @Option(names = "--output-console", defaultValue = "true", description = "Output to console true/false")
    private boolean outputConsole;

    @SuppressWarnings({"UnusedDeclaration"})
    @Option(names = "--migration-output-path", description = "Path where migration files should be generated")
    private String migrationOutputPath;

    @SuppressWarnings({"UnusedDeclaration"})
    @Option(names = "--clean-output-path", defaultValue = "false",
            description = "Clean output path before saving migrations")
    private boolean cleanOutputPath;

    @SuppressWarnings({"UnusedDeclaration"})
    @Option(names = "--rows-count-threshold", description =
            "Minimum number of rows in table that will cause to generate java migrations for unsafe sql statements")
    private Long rowsCountThreshold;

    @Override
    public Void call() {
        KrendelOutput krendelOutput = new KrendelOutput(
                sourceJdbc,
                targetJdbc,
                sourceUsername,
                sourcePassword,
                targetUsername,
                targetPassword,
                outputFile,
                outputConsole
        );

        try {
            KrendelStandardConfig krendelCliConfig = fetchConfig();
            DiffOptions diffOptions = constructDiffOptions(krendelCliConfig);
            MigrationOptions migrationOptions = constructMigrationOptions(krendelCliConfig);

            MigrationReport migrationReport = krendelOutput.generateMigrationReport(diffOptions, migrationOptions);

            if (migrationOutputPath != null) {
                consolePrintln(encode(AnsiStyle.BOLD) + "Step 5" + encode(AnsiStyle.NORMAL) + " > " + encode(AnsiStyle.BOLD) + "Generating flyway migrations");

                FlywayMigrationGenerator flywayMigrationGenerator = new FlywayMigrationGenerator(
                        sourceJdbc,
                        sourceUsername,
                        sourcePassword,
                        metadataFilePath != null ? new KrendelMigrationMetadataConfig(metadataFilePath, rowsCountThreshold) : new KrendelMigrationMetadataConfig(metadataJdbcUrl, metadataUsername, metadataPassword, rowsCountThreshold),
                        migrationOutputPath,
                        cleanOutputPath
                );
                flywayMigrationGenerator.generateMigrations(migrationReport);

                consolePrintln("Migrations were exported to " + encode(AnsiStyle.BOLD) + migrationOutputPath);
            }
        } catch (Exception ex) {
            System.err.println(encode(AnsiStyle.BOLD) + encode(AnsiColor.BRIGHT_RED) + "ERROR: " + encode(AnsiColor.DEFAULT) + encode(AnsiStyle.NORMAL) + ex.getMessage());
            System.err.println();
            ex.printStackTrace();
        }

        return null;
    }

    private KrendelStandardConfig fetchConfig() throws IOException {
        if (configFile == null) {
            return null;
        }

        try (FileInputStream config = new FileInputStream(new File(configFile))) {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            return mapper.readValue(config, KrendelStandardConfig.class);
        }
    }

    private DiffOptions constructDiffOptions(KrendelStandardConfig krendelCliConfig) {
        DiffOptions diffOptions = new DiffOptions();

        if (krendelCliConfig == null || krendelCliConfig.getDiff() == null) {
            return diffOptions;
        }

        if (krendelCliConfig.getDiff().getForeignKeyCompareMethod() != null) {
            diffOptions.setForeignKeyCompareMethod(krendelCliConfig.getDiff().getForeignKeyCompareMethod());
        }

        if (krendelCliConfig.getDiff().getIgnore() == null) {
            return diffOptions;
        }

        KrendelDiffConfig.Ignore ignoreConfig = krendelCliConfig.getDiff().getIgnore();

        diffOptions.ignoreExtensions(ignoreConfig.getExtensions());
        diffOptions.ignoreEnums(ignoreConfig.getEnums());
        diffOptions.ignoreSequences(ignoreConfig.getSequences());
        diffOptions.ignoreTables(ignoreConfig.getTables());
        diffOptions.ignoreIndexes(ignoreConfig.getIndexes());
        diffOptions.ignoreConstraints(ignoreConfig.getConstraints());
        diffOptions.ignoreColumns(ignoreConfig.getColumns());
        diffOptions.ignoreColumnsDefaultValue(ignoreConfig.getColumnsDefaultValue());
        diffOptions.ignoreViews(ignoreConfig.getViews());

        return diffOptions;
    }

    private MigrationOptions constructMigrationOptions(KrendelStandardConfig krendelCliConfig) {
        MigrationOptions migrationOptions = new MigrationOptions();

        if (krendelCliConfig == null || krendelCliConfig.getDiff() == null || krendelCliConfig.getDiff().getMigration() == null) {
            return migrationOptions;
        }

        if (krendelCliConfig.getDiff().getMigration().isIgnoreHint()) {
            migrationOptions.setIgnoreHint(krendelCliConfig.getDiff().getMigration().isIgnoreHint());
        }

        fillTablesConfig(krendelCliConfig, migrationOptions);
        fillColumnsConfig(krendelCliConfig, migrationOptions);
        fillIndexesConfig(krendelCliConfig, migrationOptions);
        fillSequencesConfig(krendelCliConfig, migrationOptions);
        fillSafeConfig(krendelCliConfig, migrationOptions);

        return migrationOptions;
    }

    private void fillTablesConfig(KrendelStandardConfig krendelCliConfig, MigrationOptions migrationOptions) {
        KrendelDiffConfig.Migration.Tables tablesConfig = krendelCliConfig.getDiff().getMigration().getTables();

        if (tablesConfig == null) {
            return;
        }

        KrendelDiffConfig.Migration.Tables.Drop tablesDropConfig = tablesConfig.getDrop();
        if (tablesDropConfig != null) {
            migrationOptions.setDropTableIfExists(tablesDropConfig.isIfExists());
        }
    }

    private void fillColumnsConfig(KrendelStandardConfig krendelCliConfig, MigrationOptions migrationOptions) {
        KrendelDiffConfig.Migration.Columns columnsConfig = krendelCliConfig.getDiff().getMigration().getColumns();

        if (columnsConfig == null) {
            return;
        }

        KrendelDiffConfig.Migration.Columns.Add columnsAddConfig = columnsConfig.getAdd();
        if (columnsAddConfig != null) {
            migrationOptions.setAddColumnIfNotExists(columnsAddConfig.isIfNotExists());
        }

        KrendelDiffConfig.Migration.Columns.Drop columnsDropConfig = columnsConfig.getDrop();
        if (columnsDropConfig != null) {
            migrationOptions.setDropColumnIfExists(columnsDropConfig.isIfExists());
        }
    }

    private void fillIndexesConfig(KrendelStandardConfig krendelCliConfig, MigrationOptions migrationOptions) {
        KrendelDiffConfig.Migration.Indexes indexesConfig = krendelCliConfig.getDiff().getMigration().getIndexes();

        if (indexesConfig == null) {
            return;
        }

        KrendelDiffConfig.Migration.Indexes.Create indexesCreateConfig = indexesConfig.getCreate();
        if (indexesCreateConfig != null) {
            migrationOptions.setCreateIndexIfNotExists(indexesCreateConfig.isIfNotExists());
            migrationOptions.setCreateIndexConcurrently(indexesCreateConfig.isConcurrently());
        }

        KrendelDiffConfig.Migration.Indexes.Drop indexesDropConfig = indexesConfig.getDrop();
        if (indexesDropConfig != null) {
            migrationOptions.setDropIndexIfExists(indexesDropConfig.isIfExists());
            migrationOptions.setDropIndexConcurrently(indexesDropConfig.isConcurrently());
        }
    }

    private void fillSequencesConfig(KrendelStandardConfig krendelCliConfig, MigrationOptions migrationOptions) {
        KrendelDiffConfig.Migration.Sequences sequencesConfig = krendelCliConfig.getDiff().getMigration().getSequences();

        if (sequencesConfig == null) {
            return;
        }

        KrendelDiffConfig.Migration.Sequences.Drop sequencesDropConfig = sequencesConfig.getDrop();
        if (sequencesDropConfig != null) {
            migrationOptions.setDropSequenceIfExists(sequencesDropConfig.isIfExists());
        }
    }

    private void fillSafeConfig(KrendelStandardConfig krendelCliConfig, MigrationOptions migrationOptions) {
        KrendelDiffConfig.Migration.Safe safeConfig = krendelCliConfig.getDiff().getMigration().getSafe();

        if (safeConfig == null) {
            return;
        }

        KrendelDiffConfig.Migration.Safe.Add safeAddConfig = safeConfig.getAdd();
        if (safeAddConfig != null) {
            migrationOptions.setSafeAddDefaultColumn(safeAddConfig.isDefaultColumns());
            migrationOptions.setSafeAddNotNullColumn(safeAddConfig.isNotNullColumns());
            migrationOptions.setSafeCreateForeignKey(safeAddConfig.isForeignKeys());
        }
    }

}
