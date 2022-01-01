package com.tailrocks.sqldiff.core.postgres

import com.tailrocks.sqldiff.AbstractContainerBaseTest
import com.tailrocks.sqldiff.TestCase
import com.tailrocks.sqldiff.core.postgres.diff.DiffOperation
import com.tailrocks.sqldiff.core.postgres.diff.DiffOptions
import com.tailrocks.sqldiff.core.postgres.model.PgTable
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TablesMigrationTests : AbstractContainerBaseTest() {

    @Test
    fun `table - create new`() {
        // given
        val testCase = TestCase("new_table", POSTGRES_CONTAINER)
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
                        assertTrue(it is PgTable).apply {
                            assertEquals("new_table", it.name)
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
                assertEquals("CREATE TABLE \"new_table\" (", get(0))
                assertEquals("    column1 bigint", get(1))
                assertEquals(");", get(2))
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
    fun `table - ignore create new`() {
        // given
        val testCase = TestCase("new_table", POSTGRES_CONTAINER)
        testCase.readSchema()

        val options = DiffOptions().apply {
            ignoreTable("new_table")
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
    fun `table - create new ignore column`() {
        // given
        val testCase =
            TestCase("table_create_ignore_column", POSTGRES_CONTAINER)
        testCase.readSchema()

        val options = DiffOptions().apply {
            ignoreColumn("new_table", "column1")
        }

        // when
        val diff = testCase.generateDiff(options)

        // then
        assertNotNull(diff).apply {
            changes.apply {
                assertEquals(1, size)
                get(0).apply {
                    assertEquals(DiffOperation.INSERT, operation)
                    assertNull(before)
                    assertNotNull(after).let {
                        assertTrue(it is PgTable).apply {
                            assertEquals("new_table", it.name)
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
                assertEquals("CREATE TABLE \"new_table\" (", get(0))
                assertEquals("    column2 bigint", get(1))
                assertEquals(");", get(2))
            }
        }

        // when
        testCase.migrateAndRefreshSchema()
        val diffAfterMigration = testCase.generateDiff(options)

        // then
        assertNotNull(diffAfterMigration).apply {
            assertEquals(0, changes.size)
        }
    }

    @Test
    fun `table - create new rich`() {
        // given
        val testCase = TestCase("new_table_rich", POSTGRES_CONTAINER)
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
                        assertTrue(it is PgTable).apply {
                            assertEquals("test_table", it.name)
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
                assertEquals("CREATE TABLE \"test_table\" (", get(0))
                assertEquals("    id bigint NOT NULL,", get(1))
                assertEquals("    created_date timestamp without time zone DEFAULT now() NOT NULL,", get(2))
                assertEquals("    modified_date timestamp without time zone DEFAULT now() NOT NULL,", get(3))
                assertEquals("    version bigint DEFAULT 0 NOT NULL,", get(4))
                assertEquals("    name character varying(255) NOT NULL,", get(5))
                assertEquals("    data jsonb,", get(6))
                assertEquals("    page character varying(2083) NOT NULL,", get(7))
                assertEquals("    status crawler_page_status NOT NULL,", get(8))
                assertEquals("    expired boolean,", get(9))
                assertEquals("    old_ids _int8,", get(10))
                assertEquals("    release_date date,", get(11))
                assertEquals("    rating_value double precision NOT NULL,", get(12))
                assertEquals("    year integer,", get(13))
                assertEquals("    title text NOT NULL,", get(14))
                assertEquals("    sync_date timestamp without time zone,", get(15))
                assertEquals("    schedule _text,", get(16))
                assertEquals("    squares _int4,", get(17))
                assertEquals("    pay_by_quarter1 _int4,", get(18))
                assertEquals("    pay_by_quarter2 _int4,", get(19))
                assertEquals("    pay_by_quarter3 _int4", get(20))
                assertEquals(");", get(21))
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
    fun `table - create with comments`() {
        // given
        val testCase =
            TestCase("table_create_with_comments", POSTGRES_CONTAINER)
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
                        assertTrue(it is PgTable).apply {
                            assertEquals("accounts", it.name)
                            assertEquals("accounts table", it.description)
                            assertNotNull(it.columns).apply {
                                assertEquals(1, size)
                                get(0).apply {
                                    assertEquals("id", name)
                                    assertEquals("account id", description)
                                }
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
            assertEquals(3, size)
            get(0).apply {
                assertEquals(DiffOperation.INSERT, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("CREATE TABLE \"accounts\" (", get(0))
                    assertEquals("    id bigint NOT NULL", get(1))
                    assertEquals(");", get(2))
                }
            }
            get(1).apply {
                assertEquals(DiffOperation.CHANGE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("COMMENT ON TABLE \"accounts\" IS 'accounts table';", get(0))
                }
            }
            get(2).apply {
                assertEquals(DiffOperation.CHANGE, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("COMMENT ON COLUMN \"accounts\".\"id\" IS 'account id';", get(0))
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
    fun `table - create with identity`() {
        // given
        val testCase =
            TestCase("table_create_with_identity", POSTGRES_CONTAINER)
        testCase.readSchema()

        // when
        val diff = testCase.generateDiff()

        // then
        assertNotNull(diff).apply {
            assertNotNull(changes).apply {
                assertEquals(2, size)
                get(0).apply {
                    assertEquals(DiffOperation.INSERT, operation)
                    assertNull(before)
                    assertNotNull(after).let {
                        assertTrue(it is PgTable).apply {
                            assertEquals("test_table", it.name)
                            assertNotNull(it.columns).apply {
                                assertEquals(3, size)
                                get(0).apply {
                                    assertEquals("id", name)
                                    assertNotNull(sequence).apply {
                                        assertEquals("test_table_id_seq", name)
                                        assertEquals("i", depType)
                                    }
                                }
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
            assertEquals(3, size)
            get(0).apply {
                assertEquals(DiffOperation.INSERT, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("CREATE TABLE \"test_table\" (", get(0))
                    assertEquals("    id bigint NOT NULL,", get(1))
                    assertEquals("    created_date timestamp without time zone DEFAULT now() NOT NULL,", get(2))
                    assertEquals("    total integer", get(3))
                    assertEquals(");", get(4))
                }
            }
            get(1).apply {
                assertEquals(DiffOperation.INSERT, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER TABLE \"test_table\" ALTER COLUMN \"id\" ADD GENERATED BY DEFAULT AS IDENTITY (", get(0))
                    assertEquals("    SEQUENCE NAME \"test_table_id_seq\"", get(1))
                    assertEquals("    START WITH 1", get(2))
                    assertEquals("    INCREMENT BY 1", get(3))
                    assertEquals("    NO MINVALUE", get(4))
                    assertEquals("    NO MAXVALUE", get(5))
                    assertEquals("    CACHE 1", get(6))
                    assertEquals(");", get(7))
                }
            }
            get(2).apply {
                assertEquals(DiffOperation.INSERT, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("ALTER TABLE \"test_table\"", get(0))
                    assertEquals("    ADD CONSTRAINT \"test_table_pkey\" PRIMARY KEY (id);", get(1))
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
    fun `table - create new partitioned table`() {
        // given
        val testCase = TestCase("new_partitioned_table", POSTGRES_CONTAINER)
        testCase.readSchema()

        // when
        val diff = testCase.generateDiff()

        // then
        assertNotNull(diff).apply {
            changes.apply {
                assertEquals(4, size)
                get(0).apply {
                    assertEquals(DiffOperation.INSERT, operation)
                    assertNull(before)
                    assertNotNull(after).let {
                        assertTrue(it is PgTable).apply {
                            assertEquals("huge_table", it.name)
                        }
                    }
                }
                get(1).apply {
                    assertEquals(DiffOperation.INSERT, operation)
                    assertNull(before)
                    assertNotNull(after).let {
                        assertTrue(it is PgTable).apply {
                            assertEquals("huge_table_01", it.name)
                        }
                    }
                }
                get(2).apply {
                    assertEquals(DiffOperation.INSERT, operation)
                    assertNull(before)
                    assertNotNull(after).let {
                        assertTrue(it is PgTable).apply {
                            assertEquals("huge_table_02", it.name)
                        }
                    }
                }
                get(3).apply {
                    assertEquals(DiffOperation.INSERT, operation)
                    assertNull(before)
                    assertNotNull(after).let {
                        assertTrue(it is PgTable).apply {
                            assertEquals("huge_table_03", it.name)
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
                    assertEquals("CREATE TABLE \"huge_table\" (", get(0))
                    assertEquals("    id bigint NOT NULL,", get(1))
                    assertEquals("    line text NOT NULL", get(2))
                    assertEquals(")", get(3))
                    assertEquals("PARTITION BY RANGE (id);", get(4))
                }
            }
            get(1).apply {
                assertEquals(DiffOperation.INSERT, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("CREATE TABLE \"huge_table_01\" PARTITION OF \"huge_table\"", get(0))
                    assertEquals("    FOR VALUES FROM ('1') TO ('200000');", get(1))
                }
            }
            get(2).apply {
                assertEquals(DiffOperation.INSERT, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("CREATE TABLE \"huge_table_02\" PARTITION OF \"huge_table\"", get(0))
                    assertEquals("    FOR VALUES FROM ('200000') TO ('400000');", get(1))
                }
            }
            get(3).apply {
                assertEquals(DiffOperation.INSERT, operation)
                assertNotNull(query).lines().apply {
                    assertEquals("CREATE TABLE \"huge_table_03\" PARTITION OF \"huge_table\"", get(0))
                    assertEquals("    FOR VALUES FROM ('400000') TO ('600000');", get(1))
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
    fun `table - drop table`() {
        // given
        val testCase = TestCase("drop_table", POSTGRES_CONTAINER)
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
                        assertTrue(it is PgTable).apply {
                            assertEquals("new_table", it.name)
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
            assertEquals("DROP TABLE \"new_table\";", get(0).query)
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

}
