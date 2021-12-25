package com.tailrocks.sqldiff.core;

import com.tailrocks.sqldiff.core.postgres.migration.MigrationReport;

public interface MigrationGenerator {

    void generateMigrations(MigrationReport report) throws Exception;

}
