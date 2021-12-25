package com.scentbird.krendel.core.postgres

import com.scentbird.krendel.AbstractContainerBaseTest
import com.scentbird.krendel.TestCase
import com.scentbird.krendel.core.postgres.diff.DiffOperation
import com.scentbird.krendel.core.postgres.diff.DiffOptions
import com.scentbird.krendel.core.postgres.migration.MigrationOptions
import com.scentbird.krendel.core.postgres.model.PgColumn
import com.scentbird.krendel.core.postgres.model.PgForeignKey
import com.scentbird.krendel.core.postgres.model.PgPrimaryKey
import com.scentbird.krendel.core.postgres.model.PgSequence
import com.scentbird.krendel.core.postgres.model.PgTable
import com.scentbird.krendel.core.postgres.model.PgUniqueConstraint
import com.scentbird.krendel.core.postgres.model.PgView
import com.scentbird.krendel.model.config.KrendelDiffConfig
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SchemaExtractorTest : AbstractContainerBaseTest() {

    @Test
    fun `table - add new columns`() {
        // given
        val testCase = TestCase("table_new_column", POSTGRES_CONTAINER)
        testCase.readSchema()

        // when
        val diff = testCase.generateDiff()

        // then
        assertNotNull(diff).apply {
            changes.apply {
                assertEquals(3, size)
                get(0).apply {
                    assertEquals(DiffOperation.INSERT, operation)
                    assertNull(before)
                    assertNotNull(after).let {
                        assertTrue(it is PgColumn).apply {
                            assertEquals("column2", it.name)
                        }
                    }
                }
                get(1).apply {
                    assertEquals(DiffOperation.INSERT, operation)
                    assertNull(before)
                    assertNotNull(after).let {
                        assertTrue(it is PgColumn).apply {
                            assertEquals("expired", it.name)
                        }
                    }
                }
                get(2).apply {
                    assertEquals(DiffOperation.INSERT, operation)
                    assertNull(before)
                    assertNotNull(after).let {
                        assertTrue(it is PgColumn).apply {
                            assertEquals("version", it.name)
                        }
                    }
                }
            }
        }

        // when
        val migrationItems = testCase.generateMigrations()

        // then
        assertNotNull(migrationItems).apply {
            assertEquals(3, size)
            get(0).apply {
                assertEquals(DiffOperation.INSERT, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER TABLE \"a\"", get(0))
                    assertEquals("    ADD COLUMN \"column2\" bigint;", get(1))
                }
            }
            get(1).apply {
                assertEquals(DiffOperation.INSERT, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER TABLE \"a\"", get(0))
                    assertEquals("    ADD COLUMN \"expired\" boolean;", get(1))
                }
            }
            get(2).apply {
                assertEquals(DiffOperation.INSERT, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER TABLE \"a\"", get(0))
                    assertEquals("    ADD COLUMN \"version\" bigint DEFAULT 0 NOT NULL;", get(1))
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
    fun `table - add new column with comment`() {
        // given
        val testCase = TestCase("table_new_column_with_comment", POSTGRES_CONTAINER)
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
                        assertTrue(it is PgColumn).apply {
                            assertEquals("column2", it.name)
                            assertEquals("test column", it.description)
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
            get(0).apply {
                assertEquals(DiffOperation.INSERT, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER TABLE \"a\"", get(0))
                    assertEquals("    ADD COLUMN \"column2\" bigint;", get(1))
                }
            }
            get(1).apply {
                assertEquals(DiffOperation.CHANGE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("COMMENT ON COLUMN \"a\".\"column2\" IS 'test column';", get(0))
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
    fun `table - drop columns`() {
        // given
        val testCase = TestCase("table_drop_columns", POSTGRES_CONTAINER)
        testCase.readSchema()

        // when
        val diff = testCase.generateDiff()

        // then
        assertNotNull(diff).apply {
            changes.apply {
                assertEquals(3, size)
                get(0).apply {
                    assertEquals(DiffOperation.REMOVE, operation)
                    assertNull(after)
                    assertNotNull(before).let {
                        assertTrue(it is PgColumn).apply {
                            assertEquals("column2", it.name)
                        }
                    }
                }
                get(1).apply {
                    assertEquals(DiffOperation.REMOVE, operation)
                    assertNull(after)
                    assertNotNull(before).let {
                        assertTrue(it is PgColumn).apply {
                            assertEquals("expired", it.name)
                        }
                    }
                }
                get(2).apply {
                    assertEquals(DiffOperation.REMOVE, operation)
                    assertNull(after)
                    assertNotNull(before).let {
                        assertTrue(it is PgColumn).apply {
                            assertEquals("version", it.name)
                        }
                    }
                }
            }
        }

        // when
        val migrationItems = testCase.generateMigrations()

        // then
        assertNotNull(migrationItems).apply {
            assertEquals(3, size)
            get(0).apply {
                assertEquals(DiffOperation.REMOVE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER TABLE \"a\"", get(0))
                    assertEquals("    DROP COLUMN \"column2\";", get(1))
                }
            }
            get(1).apply {
                assertEquals(DiffOperation.REMOVE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER TABLE \"a\"", get(0))
                    assertEquals("    DROP COLUMN \"expired\";", get(1))
                }
            }
            get(2).apply {
                assertEquals(DiffOperation.REMOVE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER TABLE \"a\"", get(0))
                    assertEquals("    DROP COLUMN \"version\";", get(1))
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
    fun `table - add default sequence`() {
        // given
        val testCase = TestCase("table_add_default_sequence", POSTGRES_CONTAINER)
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
                            assertNotNull(it.table).apply {
                                assertEquals("accounts", it.table.name)
                            }
                            assertEquals("id", it.name)
                            assertNull(it.defaultValue)
                        }
                    }
                    assertNotNull(after).let {
                        assertTrue(it is PgColumn).apply {
                            assertNotNull(it.table).apply {
                                assertEquals("accounts", it.table.name)
                            }
                            assertEquals("id", it.name)
                            assertEquals("nextval('accounts_id_seq'::regclass)", it.defaultValue)
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
            get(0).apply {
                assertEquals(DiffOperation.CHANGE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER TABLE \"accounts\"", get(0))
                    assertEquals("    ALTER COLUMN \"id\" SET DEFAULT nextval('accounts_id_seq'::regclass);", get(1))
                }
            }
            get(1).apply {
                assertEquals(DiffOperation.CHANGE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("SELECT setval('accounts_id_seq', (SELECT MAX(\"id\") FROM \"accounts\") + 1);", get(0))
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
    fun `table - add sequence`() {
        // given
        val testCase = TestCase("table_add_sequence", POSTGRES_CONTAINER)
        testCase.readSchema()

        // when
        val diff = testCase.generateDiff()

        // then
        assertNotNull(diff).apply {
            changes.apply {
                assertEquals(2, size)
                get(0).apply {
                    assertEquals(DiffOperation.CHANGE, operation)
                    assertNotNull(before).let {
                        assertTrue(it is PgSequence).apply {
                            assertEquals("accounts_id_seq", it.name)
                        }
                    }
                    assertNotNull(after).let {
                        assertTrue(it is PgSequence).apply {
                            assertEquals("accounts_id_seq", it.name)
                        }
                    }
                }
                get(1).apply {
                    assertEquals(DiffOperation.CHANGE, operation)
                    assertNotNull(before).let {
                        assertTrue(it is PgColumn).apply {
                            assertNotNull(it.table).apply {
                                assertEquals("accounts", it.table.name)
                            }
                            assertEquals("id", it.name)
                            assertEquals("nextval('accounts_id_seq'::regclass)", it.defaultValue)
                            assertNotNull(it.sequence).apply {
                                assertEquals("accounts_id_seq", name)
                                assertNull(depType)
                            }
                        }
                    }
                    assertNotNull(after).let {
                        assertTrue(it is PgColumn).apply {
                            assertNotNull(it.table).apply {
                                assertEquals("accounts", it.table.name)
                            }
                            assertEquals("id", it.name)
                            assertEquals("nextval('accounts_id_seq'::regclass)", it.defaultValue)
                            assertNotNull(it.sequence).apply {
                                assertEquals("accounts_id_seq", name)
                                assertEquals("a", depType)
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
            assertEquals(2, size)
            get(0).apply {
                assertEquals(DiffOperation.CHANGE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER SEQUENCE \"accounts_id_seq\" OWNED BY \"accounts\".\"id\";", get(0))
                }
            }
            get(1).apply {
                assertEquals(DiffOperation.CHANGE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("SELECT setval('accounts_id_seq', (SELECT MAX(\"id\") FROM \"accounts\") + 1);", get(0))
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
    fun `table - remove sequence`() {
        // given
        val testCase = TestCase("table_remove_sequence", POSTGRES_CONTAINER)
        testCase.readSchema()

        // when
        val diff = testCase.generateDiff()

        // then
        assertNotNull(diff).apply {
            changes.apply {
                assertEquals(2, size)
                get(0).apply {
                    assertEquals(DiffOperation.CHANGE, operation)
                    assertNotNull(before).let {
                        assertTrue(it is PgSequence).apply {
                            assertEquals("accounts_id_seq", it.name)
                        }
                    }
                    assertNotNull(after).let {
                        assertTrue(it is PgSequence).apply {
                            assertEquals("accounts_id_seq", it.name)
                        }
                    }
                }
                get(1).apply {
                    assertEquals(DiffOperation.CHANGE, operation)
                    assertNotNull(before).let {
                        assertTrue(it is PgColumn).apply {
                            assertNotNull(it.table).apply {
                                assertEquals("accounts", it.table.name)
                            }
                            assertEquals("id", it.name)
                            assertEquals("nextval('accounts_id_seq'::regclass)", it.defaultValue)
                            assertNotNull(it.sequence).apply {
                                assertEquals("accounts_id_seq", name)
                                assertEquals("a", depType)
                            }
                        }
                    }
                    assertNotNull(after).let {
                        assertTrue(it is PgColumn).apply {
                            assertNotNull(it.table).apply {
                                assertEquals("accounts", it.table.name)
                            }
                            assertEquals("id", it.name)
                            assertNull(it.defaultValue)
                            assertNull(it.sequence)
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

            get(0).apply {
                assertEquals(DiffOperation.CHANGE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER TABLE \"accounts\"", get(0))
                    assertEquals("    ALTER COLUMN \"id\" DROP DEFAULT;", get(1))
                }
            }

            get(1).apply {
                assertEquals(DiffOperation.CHANGE, operation)
                assertEquals("ALTER SEQUENCE \"accounts_id_seq\" OWNED BY NONE;", query)
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
    fun `table - add identity`() {
        // given
        val testCase = TestCase("table_add_identity", POSTGRES_CONTAINER)
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
                            assertNotNull(it.table).apply {
                                assertEquals("accounts", it.table.name)
                            }
                            assertEquals("id", it.name)
                            assertNull(it.defaultValue)
                            assertNull(it.sequence)
                        }
                    }
                    assertNotNull(after).let {
                        assertTrue(it is PgColumn).apply {
                            assertNotNull(it.table).apply {
                                assertEquals("accounts", it.table.name)
                            }
                            assertEquals("id", it.name)
                            assertNull(it.defaultValue)
                            assertNotNull(it.sequence).apply {
                                assertEquals("accounts_id_seq", name)
                                assertEquals("i", depType)
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
            assertEquals(2, size)
            get(0).apply {
                assertEquals(DiffOperation.INSERT, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER TABLE \"accounts\" ALTER COLUMN \"id\" ADD GENERATED BY DEFAULT AS IDENTITY (", get(0))
                    assertEquals("    SEQUENCE NAME \"accounts_id_seq\"", get(1))
                    assertEquals("    START WITH 1", get(2))
                    assertEquals("    INCREMENT BY 1", get(3))
                    assertEquals("    NO MINVALUE", get(4))
                    assertEquals("    NO MAXVALUE", get(5))
                    assertEquals("    CACHE 1", get(6))
                    assertEquals(");", get(7))
                }
            }
            get(1).apply {
                assertEquals(DiffOperation.CHANGE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("SELECT setval('accounts_id_seq', (SELECT MAX(\"id\") FROM \"accounts\") + 1);", get(0))
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
    fun `table - convert sequence to identity`() {
        // given
        val testCase = TestCase("table_sequence_to_identity", POSTGRES_CONTAINER)
        testCase.readSchema()

        // when
        val diff = testCase.generateDiff()

        // then
        assertNotNull(diff).apply {
            assertNotNull(changes).apply {
                assertEquals(2, size)
                get(0).apply {
                    assertEquals(DiffOperation.CHANGE, operation)
                    assertNotNull(before).let {
                        assertTrue(it is PgSequence).apply {
                            assertEquals("accounts_id_seq", it.name)
                        }
                    }
                    assertNotNull(after).let {
                        assertTrue(it is PgSequence).apply {
                            assertEquals("accounts_id_seq", it.name)
                        }
                    }
                }
                get(1).apply {
                    assertEquals(DiffOperation.CHANGE, operation)
                    assertNotNull(before).let {
                        assertTrue(it is PgColumn).apply {
                            assertNotNull(it.table).apply {
                                assertEquals("accounts", it.table.name)
                            }
                            assertEquals("id", it.name)
                            assertEquals("nextval('accounts_id_seq'::regclass)", it.defaultValue)
                            assertNotNull(it.sequence).apply {
                                assertEquals("accounts_id_seq", name)
                                assertEquals("a", depType)
                                assertNotNull(owner)
                            }
                        }
                    }
                    assertNotNull(after).let {
                        assertTrue(it is PgColumn).apply {
                            assertNotNull(it.table).apply {
                                assertEquals("accounts", it.table.name)
                            }
                            assertEquals("id", it.name)
                            assertNull(it.defaultValue)
                            assertNotNull(it.sequence).apply {
                                assertEquals("accounts_id_seq", name)
                                assertEquals("i", depType)
                                assertNotNull(owner)
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
            assertEquals(5, size)
            get(0).apply {
                assertEquals(DiffOperation.CHANGE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER TABLE \"accounts\"", get(0))
                    assertEquals("    ALTER COLUMN \"id\" DROP DEFAULT;", get(1))
                }
            }
            get(1).apply {
                assertEquals(DiffOperation.CHANGE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER SEQUENCE \"accounts_id_seq\" OWNED BY NONE;", get(0))
                }
            }
            get(2).apply {
                assertEquals(DiffOperation.REMOVE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("DROP SEQUENCE \"accounts_id_seq\";", get(0))
                }
            }
            get(3).apply {
                assertEquals(DiffOperation.INSERT, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER TABLE \"accounts\" ALTER COLUMN \"id\" ADD GENERATED BY DEFAULT AS IDENTITY (", get(0))
                    assertEquals("    SEQUENCE NAME \"accounts_id_seq\"", get(1))
                    assertEquals("    START WITH 1", get(2))
                    assertEquals("    INCREMENT BY 1", get(3))
                    assertEquals("    NO MINVALUE", get(4))
                    assertEquals("    NO MAXVALUE", get(5))
                    assertEquals("    CACHE 1", get(6))
                    assertEquals(");", get(7))
                }
            }
            get(4).apply {
                assertEquals(DiffOperation.CHANGE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("SELECT setval('accounts_id_seq', (SELECT MAX(\"id\") FROM \"accounts\") + 1);", get(0))
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
    fun `table - convert sequence without owner to identity`() {
        // given
        val testCase = TestCase("table_sequence_without_owner_to_identity", POSTGRES_CONTAINER)
        testCase.readSchema()

        // when
        val diff = testCase.generateDiff()

        // then
        assertNotNull(diff).apply {
            assertNotNull(changes).apply {
                assertEquals(2, size)
                get(0).apply {
                    assertEquals(DiffOperation.CHANGE, operation)
                    assertNotNull(before).let {
                        assertTrue(it is PgSequence).apply {
                            assertEquals("accounts_id_seq", it.name)
                            assertNull(it.depType)
                            assertNull(it.owner)
                        }
                    }
                    assertNotNull(after).let {
                        assertTrue(it is PgSequence).apply {
                            assertEquals("accounts_id_seq", it.name)
                            assertEquals("i", it.depType)
                            assertNotNull(it.owner).apply {
                                assertEquals("id", name)
                                assertNotNull(table).apply {
                                    assertEquals("accounts", name)
                                }
                            }
                        }
                    }
                }
                get(1).apply {
                    assertEquals(DiffOperation.CHANGE, operation)
                    assertNotNull(before).let {
                        assertTrue(it is PgColumn).apply {
                            assertNotNull(it.table).apply {
                                assertEquals("accounts", it.table.name)
                            }
                            assertEquals("id", it.name)
                            assertEquals("nextval('accounts_id_seq'::regclass)", it.defaultValue)
                            assertNotNull(it.sequence).apply {
                                assertEquals("accounts_id_seq", name)
                                assertNull(depType)
                                assertNull(owner)
                            }
                        }
                    }
                    assertNotNull(after).let {
                        assertTrue(it is PgColumn).apply {
                            assertNotNull(it.table).apply {
                                assertEquals("accounts", it.table.name)
                            }
                            assertEquals("id", it.name)
                            assertNull(it.defaultValue)
                            assertNotNull(it.sequence).apply {
                                assertEquals("accounts_id_seq", name)
                                assertEquals("i", depType)
                                assertNotNull(owner)
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
            assertEquals(5, size)
            get(0).apply {
                assertEquals(DiffOperation.CHANGE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER TABLE \"accounts\"", get(0))
                    assertEquals("    ALTER COLUMN \"id\" DROP DEFAULT;", get(1))
                }
            }
            get(1).apply {
                assertEquals(DiffOperation.CHANGE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER SEQUENCE \"accounts_id_seq\" OWNED BY NONE;", get(0))
                }
            }
            get(2).apply {
                assertEquals(DiffOperation.REMOVE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("DROP SEQUENCE \"accounts_id_seq\";", get(0))
                }
            }
            get(3).apply {
                assertEquals(DiffOperation.INSERT, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER TABLE \"accounts\" ALTER COLUMN \"id\" ADD GENERATED BY DEFAULT AS IDENTITY (", get(0))
                    assertEquals("    SEQUENCE NAME \"accounts_id_seq\"", get(1))
                    assertEquals("    START WITH 1", get(2))
                    assertEquals("    INCREMENT BY 1", get(3))
                    assertEquals("    NO MINVALUE", get(4))
                    assertEquals("    NO MAXVALUE", get(5))
                    assertEquals("    CACHE 1", get(6))
                    assertEquals(");", get(7))
                }
            }
            get(4).apply {
                assertEquals(DiffOperation.CHANGE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("SELECT setval('accounts_id_seq', (SELECT MAX(\"id\") FROM \"accounts\") + 1);", get(0))
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
    fun `table - add default`() {
        // given
        val testCase = TestCase("table_add_default", POSTGRES_CONTAINER)
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
                            assertNotNull(it.table).apply {
                                assertEquals("test_table", it.table.name)
                            }
                            assertNull(it.defaultValue)
                        }
                    }
                    assertNotNull(after).let {
                        assertTrue(it is PgColumn).apply {
                            assertNotNull(it.table).apply {
                                assertEquals("test_table", it.table.name)
                            }
                            assertEquals("0", it.defaultValue)
                        }
                    }
                }
            }
        }

        // when
        val migrationOptions = MigrationOptions().apply {
            isIgnoreHint = true
        }
        val migrationReport = testCase.generateMigrationReport(migrationOptions)

        // then
        assertNotNull(migrationReport).apply {
            assertNotNull(migrations).apply {
                assertEquals(1, size)

                get(0).apply {
                    assertEquals(DiffOperation.CHANGE, operation)
                    assertNotNull(query).lines().apply {
                        assertEquals("ALTER TABLE \"test_table\"", get(0))
                        assertEquals("    ALTER COLUMN \"version\" SET DEFAULT 0;", get(1))
                    }
                }
            }
            assertFalse(isEmptyIgnoreList)
            assertNotNull(ignoreColumnsDefaultValue).apply {
                assertEquals(1, size)
                assertNotNull(get("test_table")).apply {
                    assertEquals(1, size)
                    assertEquals("version", first())
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
    fun `table - drop default`() {
        // given
        val testCase = TestCase("table_drop_default", POSTGRES_CONTAINER)
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
                            assertNotNull(it.table).apply {
                                assertEquals("test_table", it.table.name)
                            }
                            assertEquals("now()", it.defaultValue)
                        }
                    }
                    assertNotNull(after).let {
                        assertTrue(it is PgColumn).apply {
                            assertNotNull(it.table).apply {
                                assertEquals("test_table", it.table.name)
                            }
                            assertNull(it.defaultValue)
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
                    assertEquals("ALTER TABLE \"test_table\"", get(0))
                    assertEquals("    ALTER COLUMN \"modified_date\" DROP DEFAULT;", get(1))
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
    fun `table - ignore add default`() {
        // given
        val testCase = TestCase("table_ignore_add_default", POSTGRES_CONTAINER)
        testCase.readSchema()

        val diffOptions = DiffOptions().apply {
            ignoreColumnDefaultValue("test_table", "version")
        }

        // when
        val diff = testCase.generateDiff(diffOptions)

        // then
        assertNotNull(diff).apply {
            assertNotNull(changes).apply {
                assertEquals(0, size)
            }
        }

        // when
        val migrationReport = testCase.generateMigrationReport()

        // then
        assertNotNull(migrationReport).apply {
            assertNotNull(migrations).apply {
                assertEquals(0, size)
            }
            assertTrue(isEmptyIgnoreList)
        }
    }

    @Test
    fun `table - set and drop not null`() {
        // given
        val testCase = TestCase("table_set_drop_not_null", POSTGRES_CONTAINER)
        testCase.readSchema()

        // when
        val diff = testCase.generateDiff()

        // then
        assertNotNull(diff).apply {
            changes.apply {
                assertEquals(2, size)
                get(0).apply {
                    assertEquals(DiffOperation.CHANGE, operation)
                    assertNotNull(before).let {
                        assertTrue(it is PgColumn).apply {
                            assertEquals("id", it.name)
                            assertNotNull(it.table).apply {
                                assertEquals("test_table", it.table.name)
                            }
                            assertEquals(true, it.isNullable)
                        }
                    }
                    assertNotNull(after).let {
                        assertTrue(it is PgColumn).apply {
                            assertEquals("id", it.name)
                            assertNotNull(it.table).apply {
                                assertEquals("test_table", it.table.name)
                            }
                            assertEquals(false, it.isNullable)
                        }
                    }
                }
                get(1).apply {
                    assertEquals(DiffOperation.CHANGE, operation)
                    assertNotNull(before).let {
                        assertTrue(it is PgColumn).apply {
                            assertEquals("modified_date", it.name)
                            assertNotNull(it.table).apply {
                                assertEquals("test_table", it.table.name)
                            }
                            assertEquals(false, it.isNullable)
                        }
                    }
                    assertNotNull(after).let {
                        assertTrue(it is PgColumn).apply {
                            assertEquals("modified_date", it.name)
                            assertNotNull(it.table).apply {
                                assertEquals("test_table", it.table.name)
                            }
                            assertEquals(true, it.isNullable)
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

            get(0).apply {
                assertEquals(DiffOperation.CHANGE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER TABLE \"test_table\"", get(0))
                    assertEquals("    ALTER COLUMN \"id\" SET NOT NULL;", get(1))
                }
            }
            get(1).apply {
                assertEquals(DiffOperation.CHANGE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER TABLE \"test_table\"", get(0))
                    assertEquals("    ALTER COLUMN \"modified_date\" DROP NOT NULL;", get(1))
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
    fun `sequence - create new`() {
        // given
        val testCase = TestCase("new_sequence", POSTGRES_CONTAINER)
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
                        assertTrue(it is PgSequence).apply {
                            assertEquals("accounts_id_seq", it.name)
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
            assertEquals(DiffOperation.INSERT, get(0).operation)
            assertNotNull(get(0).query).lines().apply {
                assertEquals("CREATE SEQUENCE accounts_id_seq", get(0))
                assertEquals("START WITH 1", get(1))
                assertEquals("INCREMENT BY 1", get(2))
                assertEquals("NO MINVALUE", get(3))
                assertEquals("NO MAXVALUE", get(4))
                assertEquals("CACHE 1;", get(5))
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
    fun `sequence - ignore create new`() {
        // given
        val testCase = TestCase("new_sequence", POSTGRES_CONTAINER)
        testCase.readSchema()

        val options = DiffOptions().apply {
            ignoreSequence("accounts_id_seq")
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

    @Test
    fun `column - comment`() {
        // given
        val testCase = TestCase("column_comment", POSTGRES_CONTAINER)
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
                        assertTrue(it is PgColumn).apply {
                            assertNull(it.description)
                        }
                    }
                    assertNotNull(after).let {
                        assertTrue(it is PgColumn).apply {
                            assertEquals("account id", it.description)
                        }
                    }
                }
                get(1).apply {
                    assertEquals(DiffOperation.CHANGE, operation)
                    assertNotNull(before).let {
                        assertTrue(it is PgColumn).apply {
                            assertEquals("account email", it.description)
                        }
                    }
                    assertNotNull(after).let {
                        assertTrue(it is PgColumn).apply {
                            assertEquals("account email updated", it.description)
                        }
                    }
                }
                get(2).apply {
                    assertEquals(DiffOperation.CHANGE, operation)
                    assertNotNull(before).let {
                        assertTrue(it is PgColumn).apply {
                            assertEquals("account username", it.description)
                        }
                    }
                    assertNotNull(after).let {
                        assertTrue(it is PgColumn).apply {
                            assertNull(it.description)
                        }
                    }
                }
            }
        }

        // when
        val migrationItems = testCase.generateMigrations()

        // then
        assertNotNull(migrationItems).apply {
            assertEquals(3, size)
            get(0).apply {
                assertEquals(DiffOperation.CHANGE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("COMMENT ON COLUMN \"accounts\".\"id\" IS 'account id';", get(0))
                }
            }
            get(1).apply {
                assertEquals(DiffOperation.CHANGE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("COMMENT ON COLUMN \"accounts\".\"email\" IS 'account email updated';", get(0))
                }
            }
            get(2).apply {
                assertEquals(DiffOperation.CHANGE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("COMMENT ON COLUMN \"accounts\".\"username\" IS NULL;", get(0))
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
    fun `table - comment`() {
        // given
        val testCase = TestCase("table_comment", POSTGRES_CONTAINER)
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
                        assertTrue(it is PgTable).apply {
                            assertNull(it.description)
                        }
                    }
                    assertNotNull(after).let {
                        assertTrue(it is PgTable).apply {
                            assertEquals("accounts comment", it.description)
                        }
                    }
                }
                get(1).apply {
                    assertEquals(DiffOperation.CHANGE, operation)
                    assertNotNull(before).let {
                        assertTrue(it is PgTable).apply {
                            assertEquals("orders comment", it.description)
                        }
                    }
                    assertNotNull(after).let {
                        assertTrue(it is PgTable).apply {
                            assertNull(it.description)
                        }
                    }
                }
                get(2).apply {
                    assertEquals(DiffOperation.CHANGE, operation)
                    assertNotNull(before).let {
                        assertTrue(it is PgTable).apply {
                            assertEquals("users comment", it.description)
                        }
                    }
                    assertNotNull(after).let {
                        assertTrue(it is PgTable).apply {
                            assertEquals("users comment updated", it.description)
                        }
                    }
                }
            }
        }

        // when
        val migrationItems = testCase.generateMigrations()

        // then
        assertNotNull(migrationItems).apply {
            assertEquals(3, size)
            get(0).apply {
                assertEquals(DiffOperation.CHANGE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("COMMENT ON TABLE \"accounts\" IS 'accounts comment';", get(0))
                }
            }
            get(1).apply {
                assertEquals(DiffOperation.CHANGE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("COMMENT ON TABLE \"orders\" IS NULL;", get(0))
                }
            }
            get(2).apply {
                assertEquals(DiffOperation.CHANGE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("COMMENT ON TABLE \"users\" IS 'users comment updated';", get(0))
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
    fun `views`() {
        // given
        val testCase = TestCase("views", POSTGRES_CONTAINER)
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
                        assertTrue(it is PgView).apply {
                            assertEquals("hello_world", it.name)
                        }
                    }
                    assertNotNull(after).let {
                        assertTrue(it is PgView).apply {
                            assertEquals("hello_world", it.name)
                        }
                    }
                }
                get(1).apply {
                    assertEquals(DiffOperation.REMOVE, operation)
                    assertNull(after)
                    assertNotNull(before).let {
                        assertTrue(it is PgView).apply {
                            assertEquals("vista", it.name)
                        }
                    }
                }
                get(2).apply {
                    assertEquals(DiffOperation.INSERT, operation)
                    assertNull(before)
                    assertNotNull(after).let {
                        assertTrue(it is PgView).apply {
                            assertEquals("test_view", it.name)
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
                    assertEquals("DROP VIEW \"hello_world\";", get(0))
                }
            }
            get(1).apply {
                assertEquals(DiffOperation.INSERT, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("CREATE VIEW \"hello_world\" AS", get(0))
                    assertEquals("SELECT 'Hello World'::text AS hello;", get(1))
                }
            }
            get(2).apply {
                assertEquals(DiffOperation.REMOVE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("DROP VIEW \"vista\";", get(0))
                }
            }
            get(3).apply {
                assertEquals(DiffOperation.INSERT, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("CREATE VIEW \"test_view\" AS", get(0))
                    assertEquals("SELECT test_table.id,", get(1))
                    assertEquals("    test_table.created_date,", get(2))
                    assertEquals("    test_table.modified_date,", get(3))
                    assertEquals("    test_table.version,", get(4))
                    assertEquals("    test_table.name", get(5))
                    assertEquals("   FROM test_table", get(6))
                    assertEquals("  WHERE ((test_table.name)::text = 'Petr'::text);", get(7))
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
    fun `table - primary key`() {
        // given
        val testCase = TestCase("table_primary_key", POSTGRES_CONTAINER)
        testCase.readSchema()

        // when
        val diff = testCase.generateDiff()

        // then
        assertNotNull(diff).apply {
            changes.apply {
                assertEquals(3, size)
                get(0).apply {
                    assertEquals(DiffOperation.INSERT, operation)
                    assertNull(before)
                    assertNotNull(after).let {
                        assertTrue(it is PgPrimaryKey).apply {
                            assertEquals(1, it.columns.size)
                            assertEquals("id", it.columns[0].name)
                        }
                    }
                }
                get(1).apply {
                    assertEquals(DiffOperation.REMOVE, operation)
                    assertNull(after)
                    assertNotNull(before).let {
                        assertTrue(it is PgPrimaryKey).apply {
                            assertEquals(1, it.columns.size)
                            assertEquals("id", it.columns[0].name)
                        }
                    }
                }
                get(2).apply {
                    assertEquals(DiffOperation.CHANGE, operation)
                    assertNotNull(before).let {
                        assertTrue(it is PgPrimaryKey).apply {
                            assertEquals(1, it.columns.size)
                            assertEquals("id", it.columns[0].name)
                        }
                    }
                    assertNotNull(after).let {
                        assertTrue(it is PgPrimaryKey).apply {
                            assertEquals(2, it.columns.size)
                            assertEquals("id", it.columns[0].name)
                            assertEquals("email", it.columns[1].name)
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
                assertEquals(DiffOperation.INSERT, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER TABLE \"accounts\"", get(0))
                    assertEquals("    ADD CONSTRAINT \"accounts_pkey\" PRIMARY KEY (id);", get(1))
                }
            }
            get(1).apply {
                assertEquals(DiffOperation.REMOVE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER TABLE \"orders\"", get(0))
                    assertEquals("    DROP CONSTRAINT \"orders_pkey\";", get(1))
                }
            }
            get(2).apply {
                assertEquals(DiffOperation.REMOVE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER TABLE \"users\"", get(0))
                    assertEquals("    DROP CONSTRAINT \"users_pkey\";", get(1))
                }
            }
            get(3).apply {
                assertEquals(DiffOperation.INSERT, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER TABLE \"users\"", get(0))
                    assertEquals("    ADD CONSTRAINT \"users_pkey\" PRIMARY KEY (id, email);", get(1))
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
    fun `table - unique constraint`() {
        // given
        val testCase = TestCase("table_unique_constraint", POSTGRES_CONTAINER)
        testCase.readSchema()

        // when
        val diff = testCase.generateDiff()

        // then
        assertNotNull(diff).apply {
            changes.apply {
                assertEquals(3, size)
                get(0).apply {
                    assertEquals(DiffOperation.INSERT, operation)
                    assertNull(before)
                    assertNotNull(after).let {
                        assertTrue(it is PgUniqueConstraint).apply {
                            assertEquals(1, it.columns.size)
                            assertEquals("id", it.columns[0].name)
                        }
                    }
                }
                get(1).apply {
                    assertEquals(DiffOperation.REMOVE, operation)
                    assertNull(after)
                    assertNotNull(before).let {
                        assertTrue(it is PgUniqueConstraint).apply {
                            assertEquals(1, it.columns.size)
                            assertEquals("id", it.columns[0].name)
                        }
                    }
                }
                get(2).apply {
                    assertEquals(DiffOperation.CHANGE, operation)
                    assertNotNull(before).let {
                        assertTrue(it is PgUniqueConstraint).apply {
                            assertEquals(1, it.columns.size)
                            assertEquals("id", it.columns[0].name)
                        }
                    }
                    assertNotNull(after).let {
                        assertTrue(it is PgUniqueConstraint).apply {
                            assertEquals(2, it.columns.size)
                            assertEquals("id", it.columns[0].name)
                            assertEquals("email", it.columns[1].name)
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
                assertEquals(DiffOperation.INSERT, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER TABLE \"accounts\"", get(0))
                    assertEquals("    ADD CONSTRAINT \"accounts_id_unique\" UNIQUE (id);", get(1))
                }
            }
            get(1).apply {
                assertEquals(DiffOperation.REMOVE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER TABLE \"orders\"", get(0))
                    assertEquals("    DROP CONSTRAINT \"orders_id_unique\";", get(1))
                }
            }
            get(2).apply {
                assertEquals(DiffOperation.REMOVE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER TABLE \"users\"", get(0))
                    assertEquals("    DROP CONSTRAINT \"users_unique\";", get(1))
                }
            }
            get(3).apply {
                assertEquals(DiffOperation.INSERT, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER TABLE \"users\"", get(0))
                    assertEquals("    ADD CONSTRAINT \"users_unique\" UNIQUE (id, email);", get(1))
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
    fun `table - foreign keys`() {
        // given
        val testCase = TestCase("table_foreign_keys", POSTGRES_CONTAINER)
        testCase.readSchema()

        // when
        val diff = testCase.generateDiff()

        // then
        assertNotNull(diff).apply {
            changes.apply {
                assertEquals(3, size)
                get(0).apply {
                    assertEquals(DiffOperation.REMOVE, operation)
                    assertNull(after)
                    assertNotNull(before).let {
                        assertTrue(it is PgForeignKey).apply {
                            assertEquals("fk_media_parent_id", it.name)
                        }
                    }
                }
                get(1).apply {
                    assertEquals(DiffOperation.CHANGE, operation)
                    assertNotNull(before).let {
                        assertTrue(it is PgForeignKey).apply {
                            assertEquals("fk_record_media_id", it.name)
                            assertNotNull(it.column).apply {
                                assertEquals("parent_id", name)
                                assertNotNull(table).apply {
                                    assertEquals("record", name)
                                }
                            }
                            assertNotNull(it.reference).apply {
                                assertEquals("id", name)
                                assertNotNull(table).apply {
                                    assertEquals("media", name)
                                }
                            }
                        }
                    }
                    assertNotNull(after).let {
                        assertTrue(it is PgForeignKey).apply {
                            assertEquals("fk_record_media_id", it.name)
                            assertNotNull(it.column).apply {
                                assertEquals("media_id", name)
                                assertNotNull(table).apply {
                                    assertEquals("record", name)
                                }
                            }
                            assertNotNull(it.reference).apply {
                                assertEquals("id", name)
                                assertNotNull(table).apply {
                                    assertEquals("media", name)
                                }
                            }
                        }
                    }
                }
                get(2).apply {
                    assertEquals(DiffOperation.INSERT, operation)
                    assertNull(before)
                    assertNotNull(after).let {
                        assertTrue(it is PgForeignKey).apply {
                            assertEquals("fk_record_parent_id", it.name)
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
                    assertEquals("ALTER TABLE \"media\"", get(0))
                    assertEquals("    DROP CONSTRAINT \"fk_media_parent_id\";", get(1))
                }
            }
            get(1).apply {
                assertEquals(DiffOperation.REMOVE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER TABLE \"record\"", get(0))
                    assertEquals("    DROP CONSTRAINT \"fk_record_media_id\";", get(1))
                }
            }
            get(2).apply {
                assertEquals(DiffOperation.INSERT, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER TABLE \"record\"", get(0))
                    assertEquals("    ADD CONSTRAINT \"fk_record_media_id\" FOREIGN KEY (\"media_id\") REFERENCES \"media\"(\"id\");", get(1))
                }
            }
            get(3).apply {
                assertEquals(DiffOperation.INSERT, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER TABLE \"record\"", get(0))
                    assertEquals("    ADD CONSTRAINT \"fk_record_parent_id\" FOREIGN KEY (\"parent_id\") REFERENCES \"record\"(\"id\");", get(1))
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
    fun `table - compare foreign keys by references`() {
        // given
        val testCase = TestCase("table_smart_foreign_keys_check", POSTGRES_CONTAINER)
        testCase.readSchema()

        // when
        val diffOptions = DiffOptions().apply {
            foreignKeyCompareMethod = KrendelDiffConfig.ForeignKeyCompareMethod.REFERENCES
        }
        val diff = testCase.generateDiff(diffOptions)

        // then
        assertNotNull(diff).apply {
            assertNotNull(changes).apply {
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

    @Test
    fun `migration - groups`() {
        // given
        val testCase = TestCase("test_migration_locks", POSTGRES_CONTAINER)
        testCase.readSchema()

        // when
        val diff = testCase.generateDiff(null)

        //then
        assertNotNull(diff)

        //when
        val migrationReport = testCase.generateMigrationReport()
        val migrationItemGroupSet = migrationReport.migrationItemGroupSet

        //then
        assertNotNull(migrationItemGroupSet).apply {
            assertEquals(5, size)
            get(0).apply {
                assertEquals(1, locks.size)
                locks[0].apply {
                    assertEquals(Postgres12TableLockLevel.ACCESS_EXCLUSIVE, right)
                }

            }

            get(1).apply {
                assertEquals(1, locks.size)
                locks[0].apply {
                    assertEquals(Postgres12TableLockLevel.EXCLUSIVE, right)
                }
            }

            get(2).apply {
                assertEquals(1, locks.size)
                locks[0].apply {
                    assertEquals(Postgres12TableLockLevel.SHARE, right)
                }
            }

            get(3).apply {
                assertEquals(1, locks.size)
                locks[0].apply {
                    assertEquals(Postgres12TableLockLevel.EXCLUSIVE, right)
                }
            }

            get(4).apply {
                assertEquals(1, locks.size)
                locks[0].apply {
                    assertEquals(Postgres12TableLockLevel.SHARE, right)
                }
            }
        }

    }

}
