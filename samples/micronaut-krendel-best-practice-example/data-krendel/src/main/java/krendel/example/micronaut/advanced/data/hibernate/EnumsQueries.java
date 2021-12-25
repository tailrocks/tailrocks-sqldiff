package krendel.example.micronaut.advanced.data.hibernate;

import com.scentbird.hurma.hibernate.PostgreSQLEnumsDatabaseObject;

public class EnumsQueries extends PostgreSQLEnumsDatabaseObject {

    public EnumsQueries() {
        super("krendel.example.micronaut.advanced.data.model");
    }

}
