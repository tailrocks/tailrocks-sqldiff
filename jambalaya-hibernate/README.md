# Hurma Hibernate

## Add comments to Hibernate entities (comments will be auto-added to the database schema DDL)

### Usage

#### 1. Add annotations to entity classes

Add import:

```java
import com.scentbird.hurma.spring.data.jpa.annotation.Comment;
```

Add annotation to the entity:

```java
    @Comment("I'm so happy, my comment which will be stored as a comment to the column in PostgreSQL")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
```

or

```java
    @Comment(performanceColumn = true, value = "Need for faster lookups by Scentbird Admin")
    @ManyToOne
    private User user;
```

or

```java
    @Comment(deprecated = true, value = "Rack number (need for WMS)")
    @Deprecated
    private Integer rack;
```

#### 2. Initialization

In `application.yml`, add custom `mapping-resources` and change dialect
to `com.scentbird.hurma.hibernate.PostgreSQL10HurmaDialect`:

```yaml
spring:
  jpa:
    mapping-resources: hibernate/custom.hbm.xml
    database-platform: com.scentbird.hurma.hibernate.PostgreSQL10HurmaDialect
    properties:
      hibernate:
        dialect: com.scentbird.hurma.hibernate.PostgreSQL10HurmaDialect
```

Put hibernate/custom.hbm.xml file into src/main/resources with content:

```xml
<hibernate-mapping xmlns="http://www.hibernate.org/xsd/orm/hbm">
    <database-object>
        <definition class="com.scentbird.hurma.hibernate.PostgreSQLCommentsDatabaseObject"/>
        <dialect-scope name="com.scentbird.hurma.hibernate.PostgreSQL10HurmaDialect"/>
    </database-object>
</hibernate-mapping>
```

Enjoy!
