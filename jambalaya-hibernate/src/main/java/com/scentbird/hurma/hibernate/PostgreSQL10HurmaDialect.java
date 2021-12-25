package com.scentbird.hurma.hibernate;

import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
import org.hibernate.dialect.PostgreSQL10Dialect;
import org.hibernate.tool.schema.spi.Exporter;

import java.util.List;

/**
 * Special PostgreSQL dialect, which add support {@link com.scentbird.hurma.hibernate.annotation.Comment} annotation.
 */
public class PostgreSQL10HurmaDialect extends PostgreSQL10Dialect {

    private final HurmaAuxiliaryDatabaseObjectExporter auxiliaryObjectExporter =
            new HurmaAuxiliaryDatabaseObjectExporter(this);

    public Exporter<AuxiliaryDatabaseObject> getAuxiliaryDatabaseObjectExporter() {
        return auxiliaryObjectExporter;
    }

    // TODO temp fix, need to remove after https://github.com/hibernate/hibernate-orm/pull/3352 will be merged

    /**
     * An SQL Dialect for PostgreSQL 10 and later. Adds support for Partition table.
     *
     * @param tableTypesList
     */
    @Override
    public void augmentRecognizedTableTypes(List<String> tableTypesList) {
        super.augmentRecognizedTableTypes(tableTypesList);
        tableTypesList.add("PARTITIONED TABLE");
    }

}
