package com.tailrocks.sqldiff.core.postgres.migration.statement.enums;

import com.tailrocks.sqldiff.core.postgres.migration.statement.Statement;
import com.tailrocks.sqldiff.core.postgres.model.PgEnum;

import java.util.stream.Collectors;

public class CreateEnum implements Statement {
    private final PgEnum pgEnum;

    public CreateEnum(PgEnum pgEnum) {
        this.pgEnum = pgEnum;
    }

    @Override
    public String getQuery() {
        String values = pgEnum.getValues().stream()
                .map(it -> "'" + it + "'")
                .collect(Collectors.joining(", "));

        return "CREATE TYPE \"" + pgEnum.getName() + "\" AS ENUM (" + values + ");";
    }
}
