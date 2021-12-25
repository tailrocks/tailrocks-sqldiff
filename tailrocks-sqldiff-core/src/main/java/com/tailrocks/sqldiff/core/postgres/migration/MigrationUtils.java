package com.tailrocks.sqldiff.core.postgres.migration;

import com.tailrocks.sqldiff.core.postgres.model.PgColumn;

/**
 * @author Efim Matytsin
 */
public class MigrationUtils {
    public static String getColumnType(PgColumn column) {
        // column type
        if (column.getColumnType().getUdtName().equalsIgnoreCase("varchar")) {
            return column.getColumnType().getDataType() + "(" + column.getColumnType().getCharacterMaximumLength() + ")";
        } else if (column.getColumnType().getDataType().equalsIgnoreCase("USER-DEFINED")) {
            return column.getColumnType().getUdtName();
        } else if (column.getColumnType().getDataType().equalsIgnoreCase("ARRAY")) {
            return column.getColumnType().getUdtName();
        } else if (column.getColumnType().getDataType().equalsIgnoreCase("numeric")) {
            return column.getColumnType().getDataType() + "(" + column.getColumnType().getNumericPrecision() + "," +
                    column.getColumnType().getNumericScale() + ")";
        } else {
            return column.getColumnType().getDataType();
        }
    }
}
