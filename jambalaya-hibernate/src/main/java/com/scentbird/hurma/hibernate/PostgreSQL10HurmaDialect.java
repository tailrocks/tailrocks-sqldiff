package com.scentbird.hurma.hibernate;

import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.tool.schema.spi.Exporter;

import java.util.List;

/**
 * Special PostgreSQL dialect, which add support {@link com.scentbird.hurma.hibernate.annotation.Comment} annotation.
 */
public class PostgreSQL10HurmaDialect extends PostgreSQLDialect {

    private final HurmaAuxiliaryDatabaseObjectExporter auxiliaryObjectExporter =
            new HurmaAuxiliaryDatabaseObjectExporter(this);

    @Override
    public Exporter<AuxiliaryDatabaseObject> getAuxiliaryDatabaseObjectExporter() {
        return auxiliaryObjectExporter;
    }

    @Override
    public void augmentRecognizedTableTypes(List<String> tableTypesList) {
        super.augmentRecognizedTableTypes(tableTypesList);
        tableTypesList.add("PARTITIONED TABLE");
    }

}
