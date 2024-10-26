package com.reginaldolribeiro.url_shortener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class JacksonConfigTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("ObjectMapper should have JavaTimeModule registered")
    void shouldRegisterJavaTimeModule() {
        // Check if JavaTimeModule is registered using its identifier name
        boolean isJavaTimeModuleRegistered = objectMapper.getRegisteredModuleIds()
                .stream()
                .anyMatch(id -> id.toString().contains("jsr310"));  // "jackson-datatype-jsr310" indicates JavaTimeModule

        assertTrue(isJavaTimeModuleRegistered, "JavaTimeModule should be registered with the ObjectMapper from JacksonConfig");
    }

}
