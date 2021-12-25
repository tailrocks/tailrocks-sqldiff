package com.scentbird.krendel.core.postgres.migration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class MigrationReport {

    private final List<MigrationItem> migrations = new ArrayList<>();

    private final Set<String> ignoreExtensions = new LinkedHashSet<>();
    private final Set<String> ignoreEnums = new LinkedHashSet<>();
    private final Set<String> ignoreSequences = new LinkedHashSet<>();
    private final Set<String> ignoreTables = new LinkedHashSet<>();
    private final Set<String> ignoreIndexes = new LinkedHashSet<>();
    private final Map<String, Set<String>> ignoreColumns = new HashMap<>();
    private final Map<String, Set<String>> ignoreColumnsDefaultValue = new HashMap<>();
    private final Set<String> ignoreViews = new LinkedHashSet<>();
    private final Set<String> ignoreConstraints = new LinkedHashSet<>();

    public List<MigrationItem> getMigrations() {
        return migrations;
    }

    public void addMigration(MigrationItem migrationItem) {
        requireNonNull(migrationItem, "`migrationItem` can not be null");

        migrations.add(migrationItem);
    }

    public void addMigrations(Collection<MigrationItem> migrationItems) {
        requireNonNull(migrationItems, "`migrationItems` can not be null");

        migrations.addAll(migrationItems);
    }

    public Set<String> getIgnoreExtensions() {
        return ignoreExtensions;
    }

    public Set<String> getIgnoreEnums() {
        return ignoreEnums;
    }

    public Set<String> getIgnoreSequences() {
        return ignoreSequences;
    }

    public Set<String> getIgnoreTables() {
        return ignoreTables;
    }

    public Set<String> getIgnoreIndexes() {
        return ignoreIndexes;
    }

    public Map<String, Set<String>> getIgnoreColumns() {
        return ignoreColumns;
    }

    public Map<String, Set<String>> getIgnoreColumnsDefaultValue() {
        return ignoreColumnsDefaultValue;
    }

    public Set<String> getIgnoreViews() {
        return ignoreViews;
    }

    public Set<String> getIgnoreConstraints() {
        return ignoreConstraints;
    }

    public List<MigrationItemGroup> getMigrationItemGroupSet() {
        List<MigrationItemGroup> groups = new ArrayList<>();

        MigrationItemGroup currentGroup = new MigrationItemGroup();

        for (MigrationItem item : migrations) {

            if(currentGroup.hasConflicts(item)) {
                groups.add(currentGroup);
                currentGroup = new MigrationItemGroup();
            }

            currentGroup.addMigration(item);
        }

        if (!currentGroup.isEmpty()) {
            groups.add(currentGroup);
        }

        return groups;
    }

    public boolean isEmptyIgnoreList() {
        return ignoreEnums.isEmpty()
                && ignoreSequences.isEmpty()
                && ignoreTables.isEmpty()
                && ignoreIndexes.isEmpty()
                && ignoreColumns.isEmpty()
                && ignoreColumnsDefaultValue.isEmpty()
                && ignoreViews.isEmpty()
                && ignoreConstraints.isEmpty();
    }

}
