package com.scentbird.krendel.core.postgres.migration.statement;

/**
 * @author Efim Matytsin
 */
public interface Statement {

    static Statement DEFAULT = () -> "DEFAULT";

    String getQuery();
}
