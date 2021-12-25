package krendel.example.micronaut.advanced.data.hibernate;

import com.scentbird.hurma.hibernate.ExtraDatabaseObject;

public class ExtraBeforeQueries extends ExtraDatabaseObject {

    public ExtraBeforeQueries() {
        super("db/extra/before", true);
    }

}
