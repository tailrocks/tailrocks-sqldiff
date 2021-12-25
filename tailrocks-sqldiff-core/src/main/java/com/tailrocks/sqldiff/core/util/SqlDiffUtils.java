package com.tailrocks.sqldiff.core.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class SqlDiffUtils {

    public static String readResource(String filename) {
        try (InputStream inputStream = SqlDiffUtils.class.getClassLoader().getResourceAsStream(filename)) {
            if (inputStream == null) {
                throw new FileNotFoundException(filename + " not found");
            }
            return new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getQuery(String filename) {
        return readResource("krendel/postgresql/" + filename);
    }

    public static String removeQuotes(String source) {
        return source.replace("\"", "");
    }
}
