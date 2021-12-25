package com.scentbird.hurma.hibernate;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.Resource;
import io.github.classgraph.ResourceList;
import io.github.classgraph.ScanResult;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.boot.model.relational.AbstractAuxiliaryDatabaseObject;
import org.hibernate.dialect.Dialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.strip;

/**
 * Auxiliary database object to initialize extra queries from files by specific location.
 * Quite useful when need to add some indexes with condition to Hibernate schema, which is not possible to do with
 * JPA annotations.
 */
public class ExtraDatabaseObject extends AbstractAuxiliaryDatabaseObject {

    private static final Logger log = LoggerFactory.getLogger(ExtraDatabaseObject.class);

    private final String location;

    /**
     * @param location     path to extra queries
     * @param beforeTables whether to initialize these extra queries before hibernate tables was created
     */
    public ExtraDatabaseObject(String location, boolean beforeTables) {
        super(beforeTables);

        this.location = location;
    }

    @Override
    public String[] sqlCreateStrings(Dialect dialect) {
        log.debug("Init extra queries {}", this.beforeTablesOnCreation() ? "before tables" : "after tables");

        List<String> lines = new ArrayList<>();

        try (ScanResult scanResult = new ClassGraph().acceptPathsNonRecursive(location).scan()) {
            try (ResourceList resourceList = scanResult.getResourcesWithExtension("sql")) {
                List<Resource> resources = resourceList.stream()
                        .sorted(Comparator.comparing(it -> it.getURI().toString()))
                        .collect(Collectors.toList());

                for (Resource stateFile : resources) {
                    try {
                        String migration = new String(stateFile.read().array());

                        String[] migrations = migration.split(";");

                        for (String line : migrations) {
                            line = strip(line.trim());

                            if (!StringUtils.isEmpty(line)) {
                                lines.add(line);
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("Can not read " + stateFile.getPath(), e);
                    } finally {
                        stateFile.close();
                    }
                }
            }
        }

        log.debug("Found {} extra queries", lines.size());

        return lines.toArray(new String[]{});
    }

    @Override
    public String[] sqlDropStrings(Dialect dialect) {
        return new String[0];
    }

}
