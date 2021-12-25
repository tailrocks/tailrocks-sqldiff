package krendel.spring.boot.advanced.sample.data.hibernate;

import com.scentbird.hurma.hibernate.ExtraDatabaseObject;

public class ExtraAfterQueries extends ExtraDatabaseObject {

    public ExtraAfterQueries() {
        super("db/extra/after", false);
    }

}
