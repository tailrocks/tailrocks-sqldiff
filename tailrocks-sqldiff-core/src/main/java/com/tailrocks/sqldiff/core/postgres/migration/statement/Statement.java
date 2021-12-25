package com.tailrocks.sqldiff.core.postgres.migration.statement;

public interface Statement {

    Statement DEFAULT = () -> "DEFAULT";

    String getQuery();

}
