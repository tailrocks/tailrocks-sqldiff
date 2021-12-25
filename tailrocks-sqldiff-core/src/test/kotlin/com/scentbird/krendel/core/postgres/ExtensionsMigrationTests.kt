package com.scentbird.krendel.core.postgres

import com.scentbird.krendel.AbstractContainerBaseTest
import com.scentbird.krendel.TestCase
import com.scentbird.krendel.core.postgres.diff.DiffOperation
import com.scentbird.krendel.core.postgres.model.PgExtension
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ExtensionsMigrationTests : AbstractContainerBaseTest() {

    @Test
    fun `extension - create`() {
        // given
        val testCase = TestCase("extension_create", AbstractContainerBaseTest.POSTGRES_CONTAINER)
        testCase.readSchema()

        // when
        val diff = testCase.generateDiff()

        // then
        assertNotNull(diff).apply {
            assertNotNull(changes).apply {
                assertEquals(1, size)

                get(0).apply {
                    assertEquals(DiffOperation.INSERT, operation)
                    assertNull(before)
                    assertNotNull(after).let {
                        assertTrue(it is PgExtension).apply {
                            assertEquals("pg_stat_statements", it.name)
                        }
                    }
                }
            }
        }

        // when
        val migrationItems = testCase.generateMigrations()

        // then
        assertNotNull(migrationItems).apply {
            assertEquals(1, size)

            get(0).apply {
                assertEquals(DiffOperation.INSERT, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("CREATE EXTENSION \"pg_stat_statements\";", get(0))
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
    fun `extension - drop`() {
        // given
        val testCase = TestCase("extension_drop", AbstractContainerBaseTest.POSTGRES_CONTAINER)
        testCase.readSchema()

        // when
        val diff = testCase.generateDiff()

        // then
        assertNotNull(diff).apply {
            assertNotNull(changes).apply {
                assertEquals(1, size)

                get(0).apply {
                    assertEquals(DiffOperation.REMOVE, operation)
                    assertNull(after)
                    assertNotNull(before).let {
                        assertTrue(it is PgExtension).apply {
                            assertEquals("pg_stat_statements", it.name)
                        }
                    }
                }
            }
        }

        // when
        val migrationItems = testCase.generateMigrations()

        // then
        assertNotNull(migrationItems).apply {
            assertEquals(1, size)

            get(0).apply {
                assertEquals(DiffOperation.REMOVE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("DROP EXTENSION \"pg_stat_statements\";", get(0))
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

}
