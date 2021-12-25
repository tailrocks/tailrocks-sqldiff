package com.scentbird.hurma.hibernate;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.schema.internal.StandardAuxiliaryDatabaseObjectExporter;

/**
 * Special Auxiliary Database Object Exporter only used by {@link PostgreSQL10HurmaDialect} to properly initialize
 * {@link PostgreSQLCommentsDatabaseObject}.
 */
public class HurmaAuxiliaryDatabaseObjectExporter extends StandardAuxiliaryDatabaseObjectExporter {

    public HurmaAuxiliaryDatabaseObjectExporter(Dialect dialect) {
        super(dialect);
    }

    @Override
    public String[] getSqlCreateStrings(AuxiliaryDatabaseObject object, Metadata metadata) {
        if (object instanceof PostgreSQLCommentsDatabaseObject) {
            return ((PostgreSQLCommentsDatabaseObject) object).sqlCreateStrings(metadata);
        }
        return super.getSqlCreateStrings(object, metadata);
    }

}
