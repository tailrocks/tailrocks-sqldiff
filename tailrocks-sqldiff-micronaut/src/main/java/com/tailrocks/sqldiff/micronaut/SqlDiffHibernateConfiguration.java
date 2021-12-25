package com.tailrocks.sqldiff.micronaut;

import io.micronaut.configuration.hibernate.jpa.JpaConfiguration;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import org.hibernate.cfg.AvailableSettings;

import javax.inject.Singleton;

@Singleton
public class KrendelHibernateConfiguration implements BeanCreatedEventListener<JpaConfiguration> {

    @Override
    public JpaConfiguration onCreated(BeanCreatedEvent<JpaConfiguration> event) {
        JpaConfiguration jpaConfiguration = event.getBean();
        // override hbm2ddl parameter, always skip validation or attempt to update schema
        jpaConfiguration.getProperties().put(AvailableSettings.HBM2DDL_AUTO, "none");
        return jpaConfiguration;
    }

}
