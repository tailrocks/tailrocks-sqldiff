package com.tailrocks.sqldiff.output;

import com.tailrocks.sqldiff.core.postgres.SchemaReader;
import com.tailrocks.sqldiff.core.postgres.diff.Diff;
import com.tailrocks.sqldiff.core.postgres.diff.DiffOptions;
import com.tailrocks.sqldiff.core.postgres.diff.SchemaDiff;
import com.tailrocks.sqldiff.core.postgres.migration.MigrationItem;
import com.tailrocks.sqldiff.core.postgres.migration.MigrationOptions;
import com.tailrocks.sqldiff.core.postgres.migration.MigrationReport;
import com.tailrocks.sqldiff.core.postgres.model.PgSchema;
import com.tailrocks.sqldiff.model.config.SqlDiffDiffConfig;
import com.tailrocks.sqldiff.core.postgres.migration.Migration;
import org.apache.commons.lang3.StringUtils;
import org.fusesource.jansi.AnsiConsole;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.postgresql.Driver;
import org.springframework.boot.ansi.AnsiBackground;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiStyle;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;

import static java.lang.System.out;
import static java.util.Objects.requireNonNull;
import static org.fusesource.jansi.Ansi.ansi;
import static org.springframework.boot.ansi.AnsiOutput.encode;

// TODO replace spring boot copy-pasted classes with jansi
public final class SqlDiffOutput {

    private final String sourceJdbc;
    private final String targetJdbc;
    private final String sourceUsername;
    private final String sourcePassword;
    private final String targetUsername;
    private final String targetPassword;
    private final String outputFile;
    private final boolean outputConsole;

    private int step = 0;

    public SqlDiffOutput(
            @NotNull String sourceJdbc,
            @NotNull String targetJdbc,
            @Nullable String sourceUsername,
            @Nullable String sourcePassword,
            @Nullable String targetUsername,
            @Nullable String targetPassword,
            @Nullable String outputFile,
            boolean outputConsole
    ) {
        requireNonNull(sourceJdbc, "sourceJdbc must not be null");
        requireNonNull(targetJdbc, "targetJdbc must not be null");

        this.sourceJdbc = sourceJdbc;
        this.targetJdbc = targetJdbc;
        this.sourceUsername = sourceUsername;
        this.sourcePassword = sourcePassword;
        this.targetUsername = targetUsername;
        this.targetPassword = targetPassword;
        this.outputFile = outputFile;
        this.outputConsole = outputConsole;
    }

    public static void printAppName() {
        System.setProperty("jansi.force", "true");
        AnsiConsole.systemInstall();

        System.out.println(ansi().bold().fgBrightMagenta().a("Krendel (by Scentbird)").reset());

        out.println();
    }

    public static void consolePrintln(String line) {
        out.println(line + reset());
    }

    private static String reset() {
        return encode(AnsiStyle.NORMAL) + encode(AnsiColor.DEFAULT) + encode(AnsiBackground.DEFAULT);
    }

    public void printStep(String title) {
        Objects.requireNonNull(title, "title can not be null");

        step++;

        consolePrintln(encode(AnsiStyle.BOLD) + "Step " + step + encode(AnsiStyle.NORMAL) + " > " + encode(AnsiStyle.BOLD) + title);
    }

    public MigrationReport generateMigrationReport(DiffOptions diffOptions,
                                                   MigrationOptions migrationOptions) throws FileNotFoundException {
        PrintStream fileOut = null;

        try {
            printStep("Reading schemas");

            Diff diff = Mono.zip(readSourceSchema(), readTargetSchema())
                    .map(schemas -> {
                        SchemaDiff schemaDiff = new SchemaDiff(diffOptions);

                        out.println();
                        printStep("Comparing schemas");

                        return schemaDiff.diff(schemas.getT1(), schemas.getT2());
                    })
                    .block();

            if (!diff.getChanges().isEmpty()) {
                out.println();
                consolePrintln(encode(AnsiStyle.BOLD) + "Found " + encode(AnsiColor.BRIGHT_CYAN) + diff.getChanges().size() + encode(AnsiColor.DEFAULT) + " changes");
            } else {
                out.println();
                consolePrintln(encode(AnsiStyle.BOLD) + "No changes found");
                out.println();
                System.exit(0);
            }

            Migration migration = new Migration(diff, migrationOptions);

            out.println();
            printStep("Generating migrations");

            MigrationReport migrationReport = migration.generate();

            List<MigrationItem> migrationItems = migrationReport.getMigrations();

            if (!migrationItems.isEmpty()) {
                long newCount = migrationItems.stream().filter(MigrationItem::isOperationInsert).count();
                long removedCount = migrationItems.stream().filter(MigrationItem::isOperationRemove).count();
                long changedCount = migrationItems.stream().filter(MigrationItem::isOperationChange).count();

                out.println();

                consolePrintln(encode(AnsiColor.CYAN) + encode(AnsiStyle.BOLD) + "New items     " + newCount);
                consolePrintln(encode(AnsiColor.RED) + encode(AnsiStyle.BOLD) + "Removed items " + removedCount);
                consolePrintln(encode(AnsiColor.YELLOW) + encode(AnsiStyle.BOLD) + "Changed items " + changedCount);

                out.println();
                consolePrintln(encode(AnsiStyle.BOLD) + "Generated " + encode(AnsiColor.BRIGHT_CYAN) + migrationItems.size() + encode(AnsiColor.DEFAULT) + " migrations:");
            } else {
                out.println();
                consolePrintln(encode(AnsiStyle.BOLD) + "All changes are ignored");
            }

            out.println();

            if (!migrationItems.isEmpty()) {
                boolean isFileOut = outputFile != null;

                if (isFileOut) {
                    consolePrintln(encode(AnsiColor.YELLOW) + encode(AnsiStyle.BOLD) + "# Dump schema diff to " + outputFile);
                    out.println();
                }

                if (isFileOut) {
                    fileOut = new PrintStream(new File(this.outputFile));
                }

                for (MigrationItem migrationItem : migrationItems) {
                    AnsiColor color;

                    switch (migrationItem.getOperation()) {
                        case INSERT:
                            color = AnsiColor.CYAN;
                            break;
                        case REMOVE:
                            color = AnsiColor.RED;
                            break;
                        case CHANGE:
                            color = AnsiColor.YELLOW;
                            break;
                        default:
                            color = AnsiColor.DEFAULT;
                            break;
                    }

                    if (fileOut != null) {
                        writeWithoutColors(fileOut, migrationItem);
                    }

                    if (outputConsole) {
                        writeToConsoleWithColors(migrationItem, color);
                    }
                }

                if (isFileOut) {
                    fileOut.flush();
                }

                if (outputConsole) {
                    out.flush();
                }
            }

            if (migrationOptions.isIgnoreHint()) {
                printIgnoreConfig(migrationReport);
            }

            return migrationReport;
        } finally {
            if (fileOut != null) {
                fileOut.close();
            }
        }
    }

    private void printIgnoreConfig(MigrationReport migrationReport) {
        if (migrationReport.isEmptyIgnoreList()) {
            return;
        }

        SqlDiffStandardConfig.Diff.Ignore ignoreConfig = new SqlDiffStandardConfig.Diff.Ignore();

        Map<String, List<String>> ignoreColumns = new HashMap<>();
        for (String tableName : migrationReport.getIgnoreColumns().keySet()) {
            ignoreColumns.put(tableName, new ArrayList<>(migrationReport.getIgnoreColumns().get(tableName)));
        }

        Map<String, List<String>> ignoreColumnsDefaultValue = new HashMap<>();
        for (String tableName : migrationReport.getIgnoreColumnsDefaultValue().keySet()) {
            ignoreColumnsDefaultValue.put(tableName, new ArrayList<>(migrationReport.getIgnoreColumnsDefaultValue().get(tableName)));
        }

        ignoreConfig.setExtensions(new ArrayList<>(migrationReport.getIgnoreExtensions()));
        ignoreConfig.setSequences(new ArrayList<>(migrationReport.getIgnoreSequences()));
        ignoreConfig.setTables(new ArrayList<>(migrationReport.getIgnoreTables()));
        ignoreConfig.setIndexes(new ArrayList<>(migrationReport.getIgnoreIndexes()));
        ignoreConfig.setViews(new ArrayList<>(migrationReport.getIgnoreViews()));
        ignoreConfig.setConstraints(new ArrayList<>(migrationReport.getIgnoreConstraints()));
        ignoreConfig.setColumns(ignoreColumns);
        ignoreConfig.setColumnsDefaultValue(ignoreColumnsDefaultValue);

        if (!ignoreConfig.isEmpty()) {
            out.println();
            printStep("Generating ignore config changes");
            out.println();

            DumperOptions dumperOptions = new DumperOptions();
            dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
            dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

            Representer representer = new Representer() {
                @Override
                protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue,
                                                              Tag customTag) {
                    // if value of property is null, ignore it.
                    if (propertyValue == null) {
                        return null;
                    } else if (propertyValue instanceof Collection && ((Collection) propertyValue).isEmpty()) {
                        return null;
                    } else if (propertyValue instanceof Map && ((Map) propertyValue).isEmpty()) {
                        return null;
                    } else if (propertyValue instanceof SqlDiffDiffConfig.Migration) {
                        return null;
                    } else if (propertyValue instanceof SqlDiffDiffConfig.ForeignKeyCompareMethod) {
                        return null;
                    } else {
                        return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
                    }
                }
            };
            representer.addClassTag(SqlDiffStandardConfig.class, Tag.MAP);
            representer.addClassTag(SqlDiffStandardConfig.Diff.class, Tag.MAP);
            representer.addClassTag(SqlDiffStandardConfig.Diff.Ignore.class, Tag.MAP);

            Yaml yaml = new Yaml(representer, dumperOptions);

            SqlDiffStandardConfig.Diff diffConfig = new SqlDiffStandardConfig.Diff();
            diffConfig.setIgnore(ignoreConfig);

            SqlDiffStandardConfig krendelConfig = new SqlDiffStandardConfig();
            krendelConfig.setDiff(diffConfig);

            String krendelConfigYaml = yaml.dump(krendelConfig);

            out.println(krendelConfigYaml);
        }
    }

    private Mono<PgSchema> readSourceSchema() {
        consolePrintln("Source: " + encode(AnsiStyle.BOLD) + maskJdbcUrl(sourceJdbc));
        SchemaReader reader = new SchemaReader(sourceJdbc, sourceUsername, sourcePassword);

        return Mono
                .fromCallable(() -> reader.read("public"))
                .publishOn(Schedulers.newSingle("source-reader"));
    }

    private Mono<PgSchema> readTargetSchema() {
        consolePrintln("Target: " + encode(AnsiStyle.BOLD) + maskJdbcUrl(targetJdbc));
        SchemaReader reader = new SchemaReader(targetJdbc, targetUsername, targetPassword);

        return Mono
                .fromCallable(() -> reader.read("public"))
                .publishOn(Schedulers.newSingle("target-reader"));
    }

    public static String maskJdbcUrl(String jdbcUrl) {
        if (jdbcUrl.startsWith("jdbc:postgresql:")) {
            Properties properties = Driver.parseURL(jdbcUrl, new Properties());
            String password = properties.getProperty("password");
            if (StringUtils.isEmpty(password)) {
                return jdbcUrl;
            }

            return jdbcUrl.replace("password=" + password, "password=" + StringUtils.repeat("*", password.length()));
        } else {
            return jdbcUrl;
        }
    }

    private void writeWithoutColors(PrintStream outputFile, MigrationItem migrationItem) {
        outputFile.println(migrationItem.getQuery());
        outputFile.println();
    }

    private void writeToConsoleWithColors(MigrationItem migrationItem, AnsiColor color) {
        if (migrationItem.getComment() != null) {
            consolePrintln(encode(AnsiColor.DEFAULT) + migrationItem.getComment());
        }
        try (Scanner scanner = new Scanner(migrationItem.getQuery())) {
            while (scanner.hasNextLine()) {
                consolePrintln(encode(color) + scanner.nextLine());
            }
        }
        out.println();
    }

}
