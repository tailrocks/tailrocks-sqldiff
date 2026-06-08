package com.scentbird.hurma.hibernate;

import com.scentbird.hurma.hibernate.annotation.Comment;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.AbstractAuxiliaryDatabaseObject;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Auxiliary Database Object which add comments to columns in PostgreSQL database by processing @Comment annotations.
 */
public class PostgreSQLCommentsDatabaseObject extends AbstractAuxiliaryDatabaseObject {

    private static final Logger log = LoggerFactory.getLogger(PostgreSQLCommentsDatabaseObject.class);

    public static String escapeSql(String str) {
        return str.replace("'", "''");
    }

    public String[] sqlCreateStrings(Metadata metadata) {
        log.debug("Init comments");

        List<String> result = new ArrayList<>();

        for (PersistentClass persistentClass : metadata.getEntityBindings()) {
            String entityClassName = persistentClass.getClassName();

            String tableName = persistentClass.getTable().getName();

            try {
                Class<?> entityClass = Class.forName(entityClassName);

                List<Field> fieldsWithComments = FieldUtils.getFieldsListWithAnnotation(entityClass, Comment.class);

                for (Field field : fieldsWithComments) {
                    Property property = persistentClass.getProperty(field.getName());

                    String columnName = getColumnName(property);

                    Comment commentAnnotation = field.getAnnotation(Comment.class);

                    String comment = (commentAnnotation.deprecated() ? "DEPRECATED. " : "") +
                            (commentAnnotation.performanceColumn() ? "PERFORMANCE. " : "") +
                            commentAnnotation.value().trim();

                    result.add(generateCommentQuery(tableName, columnName, comment));
                }
            } catch (ClassNotFoundException e) {
                log.error(e.getMessage(), e);
            }
        }

        log.debug("Found {} comments", result.size());

        return result.toArray(new String[]{});
    }

    @Override
    public String[] sqlCreateStrings(SqlStringGenerationContext context) {
        throw new RuntimeException("This method is not supported, use `sqlCreateStrings(Metadata metadata)` instead");
    }

    @Override
    public String[] sqlDropStrings(SqlStringGenerationContext context) {
        return new String[0];
    }

    private String generateCommentQuery(String tableName, String columnName, String comment) {
        return "COMMENT ON COLUMN " + tableName + "." + columnName + " IS '" + escapeSql(comment) + "'";
    }

    private String getColumnName(Property property) {
        Value value = property.getValue();

        if (value.getColumnSpan() == 0) {
            String source = property.getPersistentClass().getClassName() + "." + property.getName();

            throw new RuntimeException("Zero columns: " + value.getColumnSpan() + ", " + source);
        }
        if (value.getColumnSpan() > 1) {
            String source = property.getPersistentClass().getClassName() + "." + property.getName();

            throw new RuntimeException("Columns more than one: " + value.getColumnSpan() + ", " + source);
        }

        return value.getColumns().get(0).getText();
    }

}
