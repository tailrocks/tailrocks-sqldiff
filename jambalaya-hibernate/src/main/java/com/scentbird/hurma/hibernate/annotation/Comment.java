package com.scentbird.hurma.hibernate.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This will add the comment to the column in the database table.
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Comment {

    /**
     * (Required) The comment which will be stored in database on column.
     *
     * @return text comment to store
     */
    String value();

    /**
     * (Optional) Whether the column is deprecated or not. It will add "DEPRECATED" prefix in the comment.
     *
     * @return {@literal true} if this column is deprecated
     */
    boolean deprecated() default false;

    /**
     * (Optional) Indicated this field is representing column in database, which mostly need only for performance
     * reasons. It's not a business logic column, it can be some duplicated value moved from another table to avoid
     * slow join queries.
     *
     * @return {@literal true} if this column is only for performance needs
     */
    boolean performanceColumn() default false;

}
