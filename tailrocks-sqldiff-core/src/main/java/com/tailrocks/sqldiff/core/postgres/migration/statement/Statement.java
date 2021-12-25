package com.tailrocks.sqldiff.core.postgres.migration.statement;

/**
 * @author Efim Matytsin
 */
public interface Statement {

    static Statement DEFAULT = () -> "DEFAULT";

    String getQuery();
}
