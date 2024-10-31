package com.reginaldolribeiro.url_shortener.adapter.repository.url;

import com.reginaldolribeiro.url_shortener.FixtureTests;
import com.reginaldolribeiro.url_shortener.app.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UrlMapperTest {

    @Nested
    @DisplayName("toEntity Method Tests")
    class ToEntityTests {

        @Test
        @DisplayName("Should return null when Url is null")
        void testToEntity_NullUrl() {
            assertNull(UrlMapper.toEntity(null));
        }

        @Test
        @DisplayName("Should correctly map Url to UrlEntity")
        void testToEntity() {
            var url = FixtureTests.createSampleUrl();
            var entity = UrlMapper.toEntity(url);

            assertNotNull(entity);
            assertEquals(url.getId(), entity.getShortUrlId());
            assertEquals(url.getLongUrl(), entity.getLongUrl());
            assertEquals(url.getCreatedAt(), entity.getCreatedAt());
            assertEquals(url.getUpdatedAt(), entity.getUpdatedAt());
            assertEquals(url.getUser().getId().toString(), entity.getUserId());
            assertEquals(url.getClicks(), entity.getClicks());
            assertEquals(url.isActive(), entity.isActive());
        }

    }

    @Nested
    @DisplayName("toDomain Method Tests")
    class ToDomainTests {

        private static final User SAMPLE_USER = FixtureTests.createSampleUser("Sample User", "sample.user@example.com");

        @Test
        @DisplayName("Should return null when UrlEntity is null")
        void testToDomain_NullEntity() {
            assertNull(UrlMapper.toDomain(null, SAMPLE_USER));
        }

        @Test
        @DisplayName("Should correctly map UrlEntity to Url")
        void testToDomain() {
            var entity = FixtureTests.createSampleUrlEntity();
            var url = UrlMapper.toDomain(entity, SAMPLE_USER);

            assertNotNull(url, "Mapped Url should not be null");
            assertEquals(entity.getShortUrlId(), url.getId(), "IDs should match");
            assertEquals(entity.getLongUrl(), url.getLongUrl(), "Long URLs should match");
            assertEquals(entity.getCreatedAt(), url.getCreatedAt(), "Creation times should match");
            assertEquals(entity.getUpdatedAt(), url.getUpdatedAt(), "Update times should match");
            assertEquals(SAMPLE_USER, url.getUser(), "Users should match");
            assertEquals(entity.getClicks(), url.getClicks(), "Clicks count should match");
            assertEquals(entity.isActive(), url.isActive(), "Active statuses should match");
        }

    }
}