package com.tailrocks.sqldiff.output;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.tailrocks.sqldiff.core.MigrationGenerator;
import com.tailrocks.sqldiff.core.SqlClient;
import com.tailrocks.sqldiff.core.postgres.migration.MigrationItem;
import com.tailrocks.sqldiff.core.postgres.migration.MigrationItemGroup;
import com.tailrocks.sqldiff.core.postgres.migration.MigrationReport;
import com.tailrocks.sqldiff.model.config.KrendelMigrationMetadataConfig;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.commons.lang3.StringUtils;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.tailrocks.sqldiff.core.util.KrendelUtils.getQuery;
import static com.tailrocks.sqldiff.core.util.KrendelUtils.readResource;
import static com.tailrocks.sqldiff.core.util.KrendelUtils.removeQuotes;
import static com.tailrocks.sqldiff.output.KrendelOutput.consolePrintln;
import static java.lang.System.out;

public class FlywayMigrationGenerator implements MigrationGenerator {

    private static final Integer CHUNK_SIZE = 1000; // will be moved to config if needed

    private final Iterable<Flyway> flyways;
    private final String outputPath;
    private final boolean cleanOutputPath;
    private final Map<String, Long> tableCounts = new HashMap<>();

    private SqlClient sqlClient;

    private Long countThreshold = 100000L;

    public static final String UPDATE_MIGRATION_TEMPLATE = "db/migration/UpdateMigrationTemplate.java.template";

    public FlywayMigrationGenerator(String url,
                                    String username,
                                    String password,
                                    KrendelMigrationMetadataConfig metadata,
                                    String outputPath,
                                    boolean cleanOutputPath) throws IOException, CsvValidationException {
        this(
                url, username, password,
                Collections.singletonList(Flyway.configure().dataSource(url, username, password).load()),
                metadata,
                outputPath,
                cleanOutputPath
        );
    }

    public FlywayMigrationGenerator(String url,
                                    String username,
                                    String password,
                                    Iterable<Flyway> flyways,
                                    KrendelMigrationMetadataConfig metadata,
                                    String outputPath,
                                    boolean cleanOutputPath) throws IOException, CsvValidationException {
        this.flyways = flyways;
        this.outputPath = outputPath;
        this.cleanOutputPath = cleanOutputPath;

        if (metadata.getRowsCountThreshold() != null) {
            this.countThreshold = metadata.getRowsCountThreshold();
        }

        if (metadata.getFilePath() != null) {
            String[] row;

            CSVReader csvReader = new CSVReader(new FileReader(metadata.getFilePath()));
            while ((row = csvReader.readNext()) != null) {
                tableCounts.put(removeQuotes(row[0]), Long.valueOf(removeQuotes(row[1])));
            }

            sqlClient = new SqlClient(url, username, password);
        } else if (metadata.getUrl() != null) {
            new SqlClient(metadata.getUrl(), metadata.getUsername(), metadata.getPassword()).executeQuery(String.format(getQuery("readMetadata.sql"), "public"), (ResultSet rs) -> {
                tableCounts.put(rs.getString("table_name"), rs.getLong("approximate_row_count"));
            });
        }

        sqlClient = new SqlClient(url, username, password);
    }

    @Override
    public void generateMigrations(MigrationReport report) throws Exception {
        MigrationInfoService migrationInfoService = flyways.iterator().next().info();
        MigrationInfo[] appliedMigrations = migrationInfoService.applied();

        Integer version = 1;

        if (appliedMigrations.length > 0) {
            MigrationInfo lastMigration = appliedMigrations[appliedMigrations.length - 1];
            String lastVersion = lastMigration.getVersion().getVersion();

            version = Integer.parseInt(lastVersion) + 1;
        }

        // TODO remove after moving to object-oriented approach
        CCJSqlParserManager sqlParser = new CCJSqlParserManager();

        out.println();

        File outputDirectory = new File(outputPath);

        if (cleanOutputPath && outputDirectory.exists()) {
            Files.walk(outputDirectory.toPath())
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }

        outputDirectory.mkdirs();

        outputDirectory = outputDirectory.getCanonicalFile();

        for (MigrationItemGroup migrationItemGroup : report.getMigrationItemGroupSet()) {
            if (migrationItemGroup.isEmpty()) {
                continue;
            }

            List<MigrationItem> updateMigrationItems = migrationItemGroup.getMigrations()
                    .stream()
                    .filter(mi -> mi.isOperationChange() && mi.getQuery().contains("UPDATE"))
                    .collect(Collectors.toList());

            List<MigrationItem> processedMigrationItems = new ArrayList<>();

            for (MigrationItem migrationItem : updateMigrationItems) {
                Statement statement = sqlParser.parse(new StringReader(migrationItem.getQuery()));
                if (statement instanceof Update) {
                    String tableName = removeQuotes(((Update) statement).getTable().getName());
                    Long rowsCount = tableCounts.get(tableName);
                    if (rowsCount != null && rowsCount > countThreshold) {
                        String optimizedQuery = buildQueryForJavaMigration((Update) statement);
                        String fileName = String.format("V%s__migration.java", version);

                        int fileVersion = version;
                        saveMigrationToFile(outputDirectory, fileName, out -> {
                            out.println(String.format(readResource(UPDATE_MIGRATION_TEMPLATE),
                                    String.format("V%s__migration", fileVersion), CHUNK_SIZE, optimizedQuery));
                            out.flush();
                        });

                        processedMigrationItems.add(migrationItem);
                        ++version;
                    }
                }
            }

            List<MigrationItem> unprocessedMigrations = new ArrayList<>(migrationItemGroup.getMigrations());
            unprocessedMigrations.removeAll(processedMigrationItems);

            if (unprocessedMigrations.isEmpty()) {
                continue;
            }

            String fileName = String.format("V%s__migration.sql", version);

            saveMigrationToFile(outputDirectory, fileName, out -> {
                for (MigrationItem migrationItem : unprocessedMigrations) {
                    out.println(migrationItem.getQuery());
                    out.println();
                }
                out.flush();
            });

            ++version;
        }

        out.println();
    }

    private void saveMigrationToFile(File outputDirectory, String fileName, Consumer<PrintStream> consumer) throws FileNotFoundException {
        File migrationFile = new File(outputDirectory, fileName);

        if (migrationFile.exists()) {
            throw new RuntimeException("Migration file " + migrationFile.getName() + " already exists");
        }

        try (PrintStream out = new PrintStream(migrationFile)) {
            consumer.accept(out);
        }

        consolePrintln(migrationFile.getAbsolutePath());
    }

    private String buildQueryForJavaMigration(Update statement) {
        String tableName = removeQuotes(statement.getTable().getName());
        StringBuilder setQuery = new StringBuilder()
                .append("UPDATE ")
                .append(tableName)
                .append(" SET ");

        for (int i = 0; i < statement.getColumns().size(); i++) {
            Column column = statement.getColumns().get(i);
            Expression expression = statement.getExpressions().get(i);
            setQuery.append(removeQuotes(column.getColumnName()))
                    .append(" = ")
                    .append(((Column) expression).getColumnName())
                    .append(",");
        }

        setQuery.deleteCharAt(setQuery.length() - 1);

        List<String> primaryKeys = new ArrayList<>();

        sqlClient.executeQuery(String.format(getQuery("readPrimaryKey.sql"), tableName), (ResultSet rs) -> {
            primaryKeys.add(rs.getString("column_name"));
        });

        String primaryKeysJoined = removeQuotes(StringUtils.join(primaryKeys, ","));

        setQuery.append(" WHERE (")
                .append(primaryKeysJoined)
                .append(") IN (SELECT ")
                .append(primaryKeysJoined)
                .append(" FROM ")
                .append(tableName)
                .append(" WHERE ")
                .append(removeQuotes(statement.getWhere().toString()))
                .append(" LIMIT ?")
                .append(")");

        return setQuery.toString();
    }
}
