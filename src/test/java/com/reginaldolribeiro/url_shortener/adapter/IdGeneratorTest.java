package com.reginaldolribeiro.url_shortener.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class IdGeneratorTest {

    @InjectMocks
    private IdGenerator idGenerator;

    @BeforeEach
    void setUp() {
        idGenerator = new IdGenerator();
    }

    /**
     * 	^: Starts at the beginning.
     * 	[0-9A-Za-z]: Contains only digits (0-9), uppercase letters (A-Z), or lowercase letters (a-z).
     * 	+: Has one or more of these characters.
     * 	$: Ends at the last character.
     */
    @Test
    public void testShouldGenerateAnBase62IdWith7Chars() {
        final int SHORT_URL_ID_LENGTH = 7;
        final String BASE62_PATTERN_REGEX = "^[0-9A-Za-z]+$";

        var id = idGenerator.generate();
        assertNotNull(id);
        assertFalse(id.isBlank());
        assertFalse(id.isEmpty());
        assertEquals(SHORT_URL_ID_LENGTH, id.length());

        boolean isBase62 = id.matches(BASE62_PATTERN_REGEX);
        assertTrue(isBase62);
    }

    @Test
    void testToBase62HandlesZeroValue() {
        // Test if the toBase62 method correctly handles the case when the input value is zero
        String zeroBase62 = IdGenerator.toBase62(0L);
        assertEquals("0", zeroBase62, "Base62 representation of zero should be '0'");
    }

    @Test
    void testToBase62HandlesNegativeValues() {
        // Test if the toBase62 method correctly handles negative values by converting them to positive
        String base62Negative = IdGenerator.toBase62(-12345L);
        assertNotNull(base62Negative);
        assertFalse(base62Negative.isEmpty());
    }

    @Test
    void testIdGenerationUniqueness() {
        // Generate multiple IDs and check for uniqueness
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            String id = idGenerator.generate();
            assertTrue(ids.add(id), "ID should be unique, but found duplicate: " + id);
        }
        assertEquals(1000, ids.size(), "The number of unique IDs generated should be 1000.");
    }

}