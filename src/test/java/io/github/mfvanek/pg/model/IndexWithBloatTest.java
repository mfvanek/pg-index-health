package io.github.mfvanek.pg.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IndexWithBloatTest {

    @Test
    void getBloatSizeInBytes() {
        final IndexWithBloat bloat = IndexWithBloat.of("t", "i", 10L, 2L, 20);
        assertEquals(2L, bloat.getBloatSizeInBytes());
    }

    @Test
    void getBloatPercentage() {
        final IndexWithBloat bloat = IndexWithBloat.of("t", "i", 5L, 1L, 25);
        assertEquals(25, bloat.getBloatPercentage());
    }

    @Test
    void testToString() {
        final IndexWithBloat bloat = IndexWithBloat.of("t", "i", 2L, 1L, 50);
        assertNotNull(bloat);
        assertEquals(
                "IndexWithBloat{tableName='t', indexName='i', indexSizeInBytes=2, bloatSizeInBytes=1, bloatPercentage=50}",
                bloat.toString());
    }

    @Test
    void withInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> IndexWithBloat.of("t", "i", 0L, -1L, 0));
        assertThrows(IllegalArgumentException.class, () -> IndexWithBloat.of("t", "i", 0L, 0L, -1));
        assertThrows(IllegalArgumentException.class, () -> IndexWithBloat.of("t", "i", -1L, 0L, 0));
        final IndexWithBloat bloat = IndexWithBloat.of("t", "i", 0L, 0L, 0);
        assertNotNull(bloat);
    }
}
