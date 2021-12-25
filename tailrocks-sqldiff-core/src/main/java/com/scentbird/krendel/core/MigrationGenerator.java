package com.scentbird.krendel.core;

import com.scentbird.krendel.core.postgres.migration.MigrationReport;

public interface MigrationGenerator {

    void generateMigrations(MigrationReport report) throws Exception;

}
