package com.scentbird.krendel.core.postgres

import com.scentbird.krendel.AbstractContainerBaseTest
import com.scentbird.krendel.TestCase
import com.scentbird.krendel.core.postgres.diff.DiffOperation
import com.scentbird.krendel.core.postgres.diff.DiffOptions
import com.scentbird.krendel.core.postgres.migration.MigrationOptions
import com.scentbird.krendel.core.postgres.model.PgIndex
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class IndexesMigrationTests : AbstractContainerBaseTest() {

    @Test
    fun `indexes - basic test`() {
        // given
        val testCase = TestCase("indexes", POSTGRES_CONTAINER)
        testCase.readSchema()

        // when
        val diff = testCase.generateDiff()

        // then
        assertNotNull(diff).apply {
            changes.apply {
                assertEquals(3, size)
                get(0).apply {
                    assertEquals(DiffOperation.CHANGE, operation)
                    assertNotNull(before).let {
                        assertTrue(it is PgIndex).apply {
                            assertEquals("ix_account_status_new", it.name)
                        }
                    }
                    assertNotNull(after).let {
                        assertTrue(it is PgIndex).apply {
                            assertEquals("ix_account_status_new", it.name)
                        }
                    }
                }
                get(1).apply {
                    assertEquals(DiffOperation.REMOVE, operation)
                    assertNull(after)
                    assertNotNull(before).let {
                        assertTrue(it is PgIndex).apply {
                            assertEquals("ix_account_email", it.name)
                        }
                    }
                }
                get(2).apply {
                    assertEquals(DiffOperation.INSERT, operation)
                    assertNull(before)
                    assertNotNull(after).let {
                        assertTrue(it is PgIndex).apply {
                            assertEquals("ix_account_email_index", it.name)
                        }
                    }
                }
            }
        }

        // when
        val migrationItems = testCase.generateMigrations()

        // then
        assertNotNull(migrationItems).apply {
            assertEquals(4, size)
            get(0).apply {
                assertEquals(DiffOperation.REMOVE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("DROP INDEX \"ix_account_status_new\";", get(0))
                }
            }
            get(1).apply {
                assertEquals(DiffOperation.INSERT, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("CREATE INDEX ix_account_status_new ON account USING btree (id) WHERE (status = 'NEW'::account_status);", get(0))
                }
            }
            get(2).apply {
                assertEquals(DiffOperation.REMOVE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("DROP INDEX \"ix_account_email\";", get(0))
                }
            }
            get(3).apply {
                assertEquals(DiffOperation.INSERT, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("CREATE UNIQUE INDEX ix_account_email_index ON account USING btree (email, index);", get(0))
                }
            }
        }

        // when
        testCase.migrateAndRefreshSchema()
        val diffAfterMigration = testCase.generateDiff()

        // then
        assertNotNull(diffAfterMigration).apply {
            assertEquals(0, changes.size)
        }
    }

    @Test
    fun `indexes - concurrently`() {
        // given
        val testCase = TestCase("indexes", POSTGRES_CONTAINER)
        testCase.readSchema()

        val migrationOptions = MigrationOptions().apply {
            isCreateIndexConcurrently = true
            isDropIndexConcurrently = true
        }

        // when
        val diff = testCase.generateDiff()

        // then
        assertNotNull(diff).apply {
            changes.apply {
                assertEquals(3, size)
            }
        }

        // when
        val migrationReport = testCase.generateMigrationReport(migrationOptions)

        // then
        assertNotNull(migrationReport).apply {
            assertNotNull(migrations).apply {
                assertEquals(4, size)
                get(0).apply {
                    assertEquals(DiffOperation.REMOVE, operation)
                    assertNotNull(query).lines().apply {
                        assertEquals("DROP INDEX CONCURRENTLY \"ix_account_status_new\";", get(0))
                    }
                }
                get(1).apply {
                    assertEquals(DiffOperation.INSERT, operation)
                    assertNotNull(query).lines().apply {
                        assertEquals("CREATE INDEX CONCURRENTLY ix_account_status_new ON account USING btree (id) WHERE (status = 'NEW'::account_status);", get(0))
                    }
                }
                get(2).apply {
                    assertEquals(DiffOperation.REMOVE, operation)
                    assertNotNull(query).lines().apply {
                        assertEquals("DROP INDEX CONCURRENTLY \"ix_account_email\";", get(0))
                    }
                }
                get(3).apply {
                    assertEquals(DiffOperation.INSERT, operation)
                    assertNotNull(query).lines().apply {
                        assertEquals("CREATE UNIQUE INDEX CONCURRENTLY ix_account_email_index ON account USING btree (email, index);", get(0))
                    }
                }
            }
        }

        // when
        testCase.migrateAndRefreshSchema()
        val diffAfterMigration = testCase.generateDiff()

        // then
        assertNotNull(diffAfterMigration).apply {
            assertEquals(0, changes.size)
        }
    }

    @Test
    fun `indexes - if (not) exists`() {
        // given
        val testCase = TestCase("indexes", POSTGRES_CONTAINER)
        testCase.readSchema()

        val migrationOptions = MigrationOptions().apply {
            isCreateIndexIfNotExists = true
            isDropIndexIfExists = true
        }

        // when
        val diff = testCase.generateDiff()

        // then
        assertNotNull(diff).apply {
            changes.apply {
                assertEquals(3, size)
            }
        }

        // when
        val migrationReport = testCase.generateMigrationReport(migrationOptions)

        // then
        assertNotNull(migrationReport).apply {
            assertNotNull(migrations).apply {
                assertEquals(4, size)
                get(0).apply {
                    assertEquals(DiffOperation.REMOVE, operation)
                    assertNotNull(query).lines().apply {
                        assertEquals("DROP INDEX IF EXISTS \"ix_account_status_new\";", get(0))
                    }
                }
                get(1).apply {
                    assertEquals(DiffOperation.INSERT, operation)
                    assertNotNull(query).lines().apply {
                        assertEquals("CREATE INDEX IF NOT EXISTS ix_account_status_new ON account USING btree (id) WHERE (status = 'NEW'::account_status);", get(0))
                    }
                }
                get(2).apply {
                    assertEquals(DiffOperation.REMOVE, operation)
                    assertNotNull(query).lines().apply {
                        assertEquals("DROP INDEX IF EXISTS \"ix_account_email\";", get(0))
                    }
                }
                get(3).apply {
                    assertEquals(DiffOperation.INSERT, operation)
                    assertNotNull(query).lines().apply {
                        assertEquals("CREATE UNIQUE INDEX IF NOT EXISTS ix_account_email_index ON account USING btree (email, index);", get(0))
                    }
                }
            }
        }

        // when
        testCase.migrateAndRefreshSchema()
        val diffAfterMigration = testCase.generateDiff()

        // then
        assertNotNull(diffAfterMigration).apply {
            assertEquals(0, changes.size)
        }
    }

    @Test
    fun `indexes - concurrently if (not) exists`() {
        // given
        val testCase = TestCase("indexes", POSTGRES_CONTAINER)
        testCase.readSchema()

        val migrationOptions = MigrationOptions().apply {
            isCreateIndexConcurrently = true
            isDropIndexConcurrently = true
            isCreateIndexIfNotExists = true
            isDropIndexIfExists = true
        }

        // when
        val diff = testCase.generateDiff()

        // then
        assertNotNull(diff).apply {
            changes.apply {
                assertEquals(3, size)
            }
        }

        // when
        val migrationReport = testCase.generateMigrationReport(migrationOptions)

        // then
        assertNotNull(migrationReport).apply {
            assertNotNull(migrations).apply {
                assertEquals(4, size)
                get(0).apply {
                    assertEquals(DiffOperation.REMOVE, operation)
                    assertNotNull(query).lines().apply {
                        assertEquals("DROP INDEX CONCURRENTLY IF EXISTS \"ix_account_status_new\";", get(0))
                    }
                }
                get(1).apply {
                    assertEquals(DiffOperation.INSERT, operation)
                    assertNotNull(query).lines().apply {
                        assertEquals("CREATE INDEX CONCURRENTLY IF NOT EXISTS ix_account_status_new ON account USING btree (id) WHERE (status = 'NEW'::account_status);", get(0))
                    }
                }
                get(2).apply {
                    assertEquals(DiffOperation.REMOVE, operation)
                    assertNotNull(query).lines().apply {
                        assertEquals("DROP INDEX CONCURRENTLY IF EXISTS \"ix_account_email\";", get(0))
                    }
                }
                get(3).apply {
                    assertEquals(DiffOperation.INSERT, operation)
                    assertNotNull(query).lines().apply {
                        assertEquals("CREATE UNIQUE INDEX CONCURRENTLY IF NOT EXISTS ix_account_email_index ON account USING btree (email, index);", get(0))
                    }
                }
            }
        }

        // when
        testCase.migrateAndRefreshSchema()
        val diffAfterMigration = testCase.generateDiff()

        // then
        assertNotNull(diffAfterMigration).apply {
            assertEquals(0, changes.size)
        }
    }

    @Test
    fun `indexes - ignore`() {
        // given
        val testCase = TestCase("indexes", POSTGRES_CONTAINER)
        testCase.readSchema()

        val options = DiffOptions().apply {
            ignoreIndex("ix_account_status_new")
            ignoreIndex("ix_account_email")
            ignoreIndex("ix_account_email_index")
        }

        // when
        val diff = testCase.generateDiff(options)

        // then
        assertNotNull(diff).apply {
            changes.apply {
                assertEquals(0, size)
            }
        }

        // when
        val migrationItems = testCase.generateMigrations()

        // then
        assertNotNull(migrationItems).apply {
            assertEquals(0, size)
        }
    }

}
