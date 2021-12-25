package com.scentbird.hurma.hibernate;

/**
 * This interface need to be implemented in enum type, which will be used in JPA entity:
 *
 * <pre><code>
 * public enum UserStatus implements PostgreSQLEnum {
 *
 *     ACTIVE,
 *     INACTIVE,
 *     DELETED;
 *
 *     public static final String COLUMN_DEFINITION = "user_status";
 *
 *     public String columnDefinition() {
 *         return COLUMN_DEFINITION;
 *     }
 *
 * }
 * </code></pre>
 */
public interface PostgreSQLEnum {

    /**
     * @return unique value inside enum
     * @see Enum#name()
     */
    String name();

    /**
     * Suggested to use lower case value here.
     *
     * @return unique enum name which will be used in the create query (CREATE TYPE :columnDefinition AS ENUM ...)
     */
    String columnDefinition();

}
