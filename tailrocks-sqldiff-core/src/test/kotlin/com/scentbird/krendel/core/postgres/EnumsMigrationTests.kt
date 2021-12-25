package com.scentbird.krendel.core.postgres

import com.scentbird.krendel.AbstractContainerBaseTest
import com.scentbird.krendel.TestCase
import com.scentbird.krendel.core.postgres.diff.DiffOperation
import com.scentbird.krendel.core.postgres.model.PgEnum
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EnumsMigrationTests : AbstractContainerBaseTest() {

    @Test
    fun `enum - create new`() {
        // given
        val testCase = TestCase("new_enum", POSTGRES_CONTAINER)
        testCase.readSchema()

        // when
        val diff = testCase.generateDiff()

        // then
        assertNotNull(diff).apply {
            changes.apply {
                assertEquals(1, size)
                get(0).apply {
                    assertEquals(DiffOperation.INSERT, operation)
                    assertNull(before)
                    assertNotNull(after).let {
                        assertTrue(it is PgEnum).apply {
                            assertEquals("line_item_type", it.name)
                            assertEquals(3, it.values.size)
                            assertEquals("UNKNOWN", it.values[0])
                            assertEquals("CHARGE", it.values[1])
                            assertEquals("CREDIT", it.values[2])
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
            assertEquals("CREATE TYPE \"line_item_type\" AS ENUM ('UNKNOWN', 'CHARGE', 'CREDIT');", get(0).query)
            assertEquals(DiffOperation.INSERT, get(0).operation)
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
    fun `enum - drop existed`() {
        // given
        val testCase = TestCase("delete_enum", POSTGRES_CONTAINER)
        testCase.readSchema()

        // when
        val diff = testCase.generateDiff()

        // then
        assertNotNull(diff).apply {
            changes.apply {
                assertEquals(1, size)
                get(0).apply {
                    assertEquals(DiffOperation.REMOVE, operation)
                    assertNull(after)
                    assertNotNull(before).let {
                        assertTrue(it is PgEnum).apply {
                            assertEquals("line_item_type", it.name)
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
            assertEquals("DROP TYPE \"line_item_type\";", get(0).query)
            assertEquals(DiffOperation.REMOVE, get(0).operation)
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
    fun `enum - add value`() {
        // given
        val testCase = TestCase("enum_add_value", POSTGRES_CONTAINER)
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
                        assertTrue(it is PgEnum).apply {
                            assertEquals("line_item_type", it.name)
                            assertEquals(3, it.values.size)
                            assertEquals("UNKNOWN", it.values[0])
                            assertEquals("CHARGE", it.values[1])
                            assertEquals("CREDIT", it.values[2])
                        }
                    }
                    assertNotNull(after).let {
                        assertTrue(it is PgEnum).apply {
                            assertEquals("line_item_type", it.name)
                            assertEquals(5, it.values.size)
                            assertEquals("UNKNOWN", it.values[0])
                            assertEquals("XXX", it.values[1])
                            assertEquals("CHARGE", it.values[2])
                            assertEquals("CREDIT", it.values[3])
                            assertEquals("ZZZ", it.values[4])
                        }
                    }
                }
            }
        }

        // when
        val migrationItems = testCase.generateMigrations()

        // then
        assertNotNull(migrationItems).apply {
            assertEquals(2, size)

            assertEquals("ALTER TYPE \"line_item_type\" ADD VALUE 'XXX';", get(0).query)
            assertEquals(DiffOperation.CHANGE, get(0).operation)

            assertEquals("ALTER TYPE \"line_item_type\" ADD VALUE 'ZZZ';", get(1).query)
            assertEquals(DiffOperation.CHANGE, get(1).operation)
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
