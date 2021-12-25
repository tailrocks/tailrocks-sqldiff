package com.scentbird.krendel.core.postgres.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PgSchema {

    private String name;

    private final Map<String, PgExtension> extensionsByName = new LinkedHashMap<>();
    private final Map<String, PgEnum> enumsByName = new LinkedHashMap<>();

    private final List<PgTable> tables = new ArrayList<>();
    private final Map<String, PgTable> tablesByName = new LinkedHashMap<>();
    private final Map<String, PgSequence> sequencesByName = new LinkedHashMap<>();
    private final Map<String, PgView> viewsByName = new LinkedHashMap<>();
    private final Map<String, PgForeignKey> foreignKeysByName = new LinkedHashMap<>();
    private final Map<String, PgForeignKey> foreignKeysByHash = new LinkedHashMap<>();

    public PgSchema(String name) {
        this.name = name;
    }

    public void addExtension(PgExtension extension) {
        extensionsByName.put(extension.getName(), extension);
    }

    public void addEnums(Collection<PgEnum> values) {
        for (PgEnum item : values) {
            addEnum(item);
        }
    }

    public void addEnum(PgEnum item) {
        enumsByName.put(item.getName(), item);
    }

    public PgTable addTable(String name) {
        PgTable table = getOrCreateTableByName(name);
        table.setSchema(this);

        tables.add(table);
        tablesByName.put(name, table);

        return table;
    }

    public void addSequence(PgSequence sequence) {
        sequencesByName.put(sequence.getName(), sequence);
    }

    public void addView(PgView view) {
        viewsByName.put(view.getName(), view);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PgTable> getTables() {
        return tables;
    }

    public Map<String, PgExtension> getExtensionsByName() {
        return extensionsByName;
    }

    public Map<String, PgEnum> getEnumsByName() {
        return enumsByName;
    }

    public PgEnum getEnum(String name) {
        return enumsByName.get(name);
    }

    public Map<String, PgTable> getTablesByName() {
        return tablesByName;
    }

    public PgTable getTable(String name) {
        return tablesByName.get(name);
    }

    public Map<String, PgSequence> getSequencesByName() {
        return sequencesByName;
    }

    public PgSequence getSequence(String name) {
        return sequencesByName.get(name);
    }

    public Map<String, PgView> getViewsByName() {
        return viewsByName;
    }

    public PgView getView(String name) {
        return viewsByName.get(name);
    }

    private PgTable getOrCreateTableByName(String name) {
        return tablesByName.getOrDefault(name, new PgTable(name));
    }

    public Map<String, PgForeignKey> getForeignKeysByName() {
        return foreignKeysByName;
    }

    public Map<String, PgForeignKey> getForeignKeysByHash() {
        return foreignKeysByHash;
    }

    public PgForeignKey getForeignKeyByName(String name) {
        return foreignKeysByName.get(name);
    }

    public PgForeignKey getForeignKeyByHash(String hash) {
        return foreignKeysByHash.get(hash);
    }

}
