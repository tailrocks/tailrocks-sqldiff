package com.tailrocks.sqldiff.spring.boot.autoconfigure;

import com.tailrocks.sqldiff.core.postgres.SchemaReader;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.jpa.boot.spi.IntegratorProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(SchemaReader.class)
@ConditionalOnProperty(prefix = "krendel", name = "enabled", matchIfMissing = true)
public class KrendelHibernateConfig implements HibernatePropertiesCustomizer {

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        // override hbm2ddl parameter, always skip validation or attempt to update schema
        hibernateProperties.put(AvailableSettings.HBM2DDL_AUTO, "none");

        // inject HibernateMetadataExtractor, need to catch the Metadata instance
        hibernateProperties.put("hibernate.integrator_provider",
                (IntegratorProvider) () -> Collections.singletonList(HibernateMetadataExtractor.INSTANCE));
    }

}
