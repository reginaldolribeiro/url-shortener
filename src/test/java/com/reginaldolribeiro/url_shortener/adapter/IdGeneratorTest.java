package com.reginaldolribeiro.url_shortener.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class IdGeneratorTest {

    @InjectMocks
    private IdGenerator idGenerator;

    /**
     * 	^: Starts at the beginning.
     * 	[0-9A-Za-z]: Contains only digits (0-9), uppercase letters (A-Z), or lowercase letters (a-z).
     * 	+: Has one or more of these characters.
     * 	$: Ends at the last character.
     */
    @Test
    public void testShouldGenerateAnBase62IdWith7Chars() {
        String BASE62_PATTERN_REGEX = "^[0-9A-Za-z]+$";

        var id = idGenerator.generate();
        assertNotNull(id);
        assertEquals(7, id.length());

        boolean isBase62 = id.matches(BASE62_PATTERN_REGEX);
        assertTrue(isBase62);
    }

}