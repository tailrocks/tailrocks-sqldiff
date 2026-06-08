package com.scentbird.hurma.hibernate;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.hibernate.boot.model.relational.AbstractAuxiliaryDatabaseObject;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Auxiliary Database Object which create SQL DDL for {@link PostgreSQLEnum} implementations.
 */
public class PostgreSQLEnumsDatabaseObject extends AbstractAuxiliaryDatabaseObject {

    private static final Logger log = LoggerFactory.getLogger(PostgreSQLEnumsDatabaseObject.class);

    private final String basePackage;

    public PostgreSQLEnumsDatabaseObject(String basePackage) {
        super(true);

        this.basePackage = basePackage;
    }

    @Override
    public String[] sqlCreateStrings(SqlStringGenerationContext context) {
        List<String> result = new ArrayList<>();

        Map<String, String> usedTypes = new HashMap<>();

        for (String className : findImplementations()) {
            log.debug("Detected: {}", className);

            try {
                Class<PostgreSQLEnum> enumClass = (Class<PostgreSQLEnum>) Class.forName(className);

                result.add(generateEnumDef(enumClass, usedTypes));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Unable to load class: " + className, e);
            }
        }

        return result.toArray(new String[]{});
    }

    @Override
    public String[] sqlDropStrings(SqlStringGenerationContext context) {
        List<String> result = new ArrayList<>();

        for (String className : findImplementations()) {
            log.debug("Detected: {}", className);

            try {
                Class<PostgreSQLEnum> enumClass = (Class<PostgreSQLEnum>) Class.forName(className);

                result.add(generateDropQuery(enumClass));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Unable to load class: " + className, e);
            }
        }

        return result.toArray(new String[]{});
    }

    private Set<String> findImplementations() {
        log.debug("Init enums");

        Set<String> classes = new HashSet<>();

        try (
                ScanResult scanResult = new ClassGraph()
                        // scan classes only
                        .enableClassInfo()
                        .acceptPackages(basePackage)
                        .scan()
        ) {
            for (ClassInfo routeClassInfo : scanResult.getClassesImplementing(PostgreSQLEnum.class.getName())) {
                classes.add(routeClassInfo.getName());
            }
        }

        log.debug("Found {} implementations", classes.size());

        return classes;
    }

    private String generateDropQuery(Class<? extends PostgreSQLEnum> enumClass) {
        @SuppressWarnings("rawtypes")
        PostgreSQLEnum[] values = enumClass.getEnumConstants();

        if (values.length == 0) {
            throw new RuntimeException("Empty enum: " + enumClass.getName());
        }

        String typeName = values[0].columnDefinition();

        return "DROP TYPE IF EXISTS " + typeName + " CASCADE";
    }

    private String generateEnumDef(Class<? extends PostgreSQLEnum> enumClass, Map<String, String> usedTypes) {
        @SuppressWarnings("rawtypes")
        PostgreSQLEnum[] values = enumClass.getEnumConstants();

        if (values.length == 0) {
            throw new RuntimeException("Empty enum: " + enumClass.getName());
        }

        String typeName = values[0].columnDefinition();

        if (usedTypes.containsKey(typeName)) {
            throw new RuntimeException("Duplicated type: " + enumClass.getName() + " + with " + usedTypes.get(typeName));
        }

        usedTypes.put(typeName, enumClass.getName());

        String names = Arrays.stream(values)
                .map(PostgreSQLEnum::name)
                .map(it -> "'" + it + "'")
                .collect(Collectors.joining(", "));

        return "CREATE TYPE " + typeName + " AS ENUM (" + names + ")";
    }

}
