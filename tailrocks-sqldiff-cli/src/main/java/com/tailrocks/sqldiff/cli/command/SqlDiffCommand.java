package com.tailrocks.sqldiff.cli.command;

import com.tailrocks.sqldiff.output.KrendelOutput;
import io.micronaut.configuration.picocli.PicocliRunner;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "krendel", mixinStandardHelpOptions = true, subcommands = {
        DiffCommand.class,
        AnalyzeSchemaCommand.class
})
public class KrendelCommand implements Callable<Object> {

    public static void main(String[] args) {
        int exitCode = PicocliRunner.execute(KrendelCommand.class, args);
        System.exit(exitCode);
    }

    @Override
    public Object call() {
        KrendelOutput.printAppName();
        return null;
    }

}
