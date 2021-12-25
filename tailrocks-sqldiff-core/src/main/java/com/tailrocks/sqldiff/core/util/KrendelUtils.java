package com.scentbird.krendel.core.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class KrendelUtils {

    public static String readResource(String filename) {
        String resourceName = filename;
        try (InputStream inputStream = KrendelUtils.class.getClassLoader().getResourceAsStream(resourceName)) {
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
