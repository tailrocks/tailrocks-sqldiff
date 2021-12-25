package com.scentbird.krendel.core.postgres.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum PgConstraintType {

    PRIMARY("PRIMARY KEY"),
    UNIQUE("UNIQUE");

    private String name;

    PgConstraintType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static List<String> names() {
        return Arrays.stream(PgConstraintType.values())
                .map(PgConstraintType::getName)
                .collect(Collectors.toList());
    }

}
