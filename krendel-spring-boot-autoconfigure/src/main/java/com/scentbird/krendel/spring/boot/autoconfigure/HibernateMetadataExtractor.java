package com.scentbird.krendel.spring.boot.autoconfigure;

import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class HibernateMetadataExtractor implements org.hibernate.integrator.spi.Integrator {

    public static final HibernateMetadataExtractor INSTANCE = new HibernateMetadataExtractor();

    private HibernateMetadataExtractor() {
    }

    // This countDownLatch hack is used in some situations, when getMetadata() is called before integrate method was
    // called, this is actual for the cases when Hibernate initializing in separate thread.
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private Metadata metadata;

    public Metadata getMetadata() throws InterruptedException {
        countDownLatch.await(5L, TimeUnit.SECONDS);

        if (metadata == null) {
            throw new HibernateMetadataExtractorNotInitializedException("Metadata is null, integrator not initialized yet");
        }
        return metadata;
    }

    @Override
    public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory,
                          SessionFactoryServiceRegistry serviceRegistry) {
        this.metadata = metadata;

        countDownLatch.countDown();
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
    }

}
