package com.tailrocks.sqldiff.cli.command;

import com.tailrocks.sqldiff.output.SqlDiffOutput;
import io.micronaut.configuration.picocli.PicocliRunner;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "tailrocks-sqldiff", mixinStandardHelpOptions = true, subcommands = {
        DiffCommand.class,
        AnalyzeSchemaCommand.class
})
public class SqlDiffCommand implements Callable<Object> {

    public static void main(String[] args) {
        int exitCode = PicocliRunner.execute(SqlDiffCommand.class, args);
        System.exit(exitCode);
    }

    @Override
    public Object call() {
        SqlDiffOutput.printAppName();
        return null;
    }

}
