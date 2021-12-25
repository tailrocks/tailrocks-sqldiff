package com.scentbird.krendel.core.postgres

import com.scentbird.krendel.AbstractContainerBaseTest
import com.scentbird.krendel.TestCase
import com.scentbird.krendel.core.postgres.diff.DiffOperation
import com.scentbird.krendel.core.postgres.model.PgColumn
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ColumnsMigrationTests : AbstractContainerBaseTest() {

    @Test
    fun `column - change type`() {
        // given
        val testCase = TestCase("column_change_type", POSTGRES_CONTAINER)
        testCase.readSchema()

        // when
        val diff = testCase.generateDiff()

        // then
        assertNotNull(diff).apply {
            changes.apply {
                assertEquals(1, size)

                get(0).apply {
                    assertEquals(DiffOperation.CHANGE, operation)
                    assertNotNull(before).let {
                        assertTrue(it is PgColumn).apply {
                            assertEquals("content", it.name)
                            assertNotNull(it.columnType).apply {
                                assertEquals("character varying", dataType)
                                assertEquals("varchar", udtName)
                                assertEquals(255, characterMaximumLength)
                            }
                        }
                    }
                    assertNotNull(after).let {
                        assertTrue(it is PgColumn).apply {
                            assertEquals("content", it.name)
                            assertNotNull(it.columnType).apply {
                                assertEquals("text", dataType)
                                assertEquals("text", udtName)
                                assertEquals(0, characterMaximumLength)
                            }
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
                assertEquals(DiffOperation.CHANGE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER TABLE \"posts\"", get(0))
                    assertEquals("    ALTER COLUMN \"content\" SET DATA TYPE text;", get(1))
                }
                assertEquals("-- Current Type: character varying(255)", comment)
            }
        }
        //when
        val migrationReport = testCase.generateMigrationReport()
        //then
        assertNotNull(migrationReport.migrationItemGroupSet).apply {
            assertEquals(1, size)
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
