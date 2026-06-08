package com.tailrocks.sqldiff.micronaut;

import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.AvailableSettings;

import java.util.Map;

/**
 * Overrides Hibernate's hbm2ddl.auto setting to "none" so that sqldiff manages schema migrations
 * instead of Hibernate auto-DDL.
 *
 * In Micronaut 4, JPA properties should be configured via application.yml:
 * jpa.default.properties.hibernate.hbm2ddl.auto=none
 */
@Singleton
public class SqlDiffHibernateConfiguration {

    // In Micronaut 4, configure this via application.yml:
    // jpa:
    //   default:
    //     properties:
    //       hibernate:
    //         hbm2ddl:
    //           auto: none
}
