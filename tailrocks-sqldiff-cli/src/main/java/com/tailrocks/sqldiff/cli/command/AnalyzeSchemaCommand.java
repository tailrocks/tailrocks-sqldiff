package com.tailrocks.sqldiff.cli.command;

import com.opencsv.CSVWriter;
import com.scentbird.krendel.core.SqlClient;
import org.springframework.boot.ansi.AnsiStyle;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.concurrent.Callable;

import static com.scentbird.krendel.core.util.KrendelUtils.getQuery;
import static com.scentbird.krendel.output.KrendelOutput.consolePrintln;
import static com.scentbird.krendel.output.KrendelOutput.maskJdbcUrl;
import static org.springframework.boot.ansi.AnsiOutput.encode;

@Command(name = "analyze-schema", mixinStandardHelpOptions = true, sortOptions = false)
public class AnalyzeSchemaCommand implements Callable<Void> {

    @SuppressWarnings({"UnusedDeclaration"})
    @Parameters(index = "0", description = "JDBC url of production database to get metadata (including username/password)")
    private String url;

    @SuppressWarnings({"UnusedDeclaration"})
    @Option(names = "--username", description = "Username of production database to get metadata")
    private String username;

    @SuppressWarnings({"UnusedDeclaration"})
    @Option(names = "--password", description = "Password of production database to get metadata")
    private String password;

    @SuppressWarnings({"UnusedDeclaration"})
    @Option(names = "--output-file", description = "Path to output file")
    private String outputFile;

    @Override
    public Void call() throws IOException {
        consolePrintln("Analyzing schema: " + encode(AnsiStyle.BOLD) + maskJdbcUrl(url));

        CSVWriter csvWriter = new CSVWriter(new FileWriter(outputFile));

        try {
            new SqlClient(url, username, password).executeQuery(String.format(getQuery("readMetadata.sql"), "public"), (ResultSet rs) -> {
                csvWriter.writeNext(new String[]{rs.getString("table_name"), String.valueOf(rs.getLong("approximate_row_count"))});
            });
        } finally {
            csvWriter.close();
        }

        consolePrintln("Output file was generated to " + encode(AnsiStyle.BOLD) + outputFile);

        return null;
    }
}
