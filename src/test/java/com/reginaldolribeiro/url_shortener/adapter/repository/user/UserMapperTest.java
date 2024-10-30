package com.reginaldolribeiro.url_shortener.adapter.repository.user;

import com.reginaldolribeiro.url_shortener.FixtureTests;
import com.reginaldolribeiro.url_shortener.app.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Nested
    @DisplayName("toEntity Method Tests")
    class ToEntityTests {

        @Test
        @DisplayName("Should return null when User is null")
        void testToEntity_NullUser() {
            assertNull(UserMapper.toEntity(null));
        }

        @ParameterizedTest(name = "Convert User with ID: {0}")
        @MethodSource("provideUsersForToEntity")
        @DisplayName("Should correctly map User to UserEntity")
        void testToEntity(User user) {
            UserEntity entity = UserMapper.toEntity(user);
            assertNotNull(entity, "Mapped UserEntity should not be null");
            assertEquals(user.getId().toString(), entity.getId(), "IDs should match");
            assertEquals(user.getName(), entity.getName(), "Names should match");
            assertEquals(user.getEmail(), entity.getEmail(), "Emails should match");
            assertEquals(user.getCreatedAt(), entity.getCreatedAt(), "Creation times should match");
            assertEquals(user.getUpdatedAt(), entity.getUpdatedAt(), "Update times should match");
            assertEquals(user.isActive(), entity.isActive(), "Active statuses should match");
        }

        static Stream<Arguments> provideUsersForToEntity() {
            return Stream.of(
                    Arguments.of(FixtureTests.createSampleUser()),
                    Arguments.of(FixtureTests.createSampleUser("Bob", "bob@example.com")),
                    Arguments.of(FixtureTests.createSampleUser("Charlie", "charlie@example.com"))
            );
        }
    }

    @Nested
    @DisplayName("toDomain Method Tests")
    class ToDomainTests {

        @Test
        @DisplayName("Should return null when UserEntity is null")
        void testToDomain_NullEntity() {
            assertNull(UserMapper.toDomain(null));
        }

        @ParameterizedTest(name = "Convert UserEntity with ID: {0}")
        @MethodSource("provideEntitiesForToDomain")
        @DisplayName("Should correctly map UserEntity to User")
        void testToDomain(UserEntity entity) {
            User user = UserMapper.toDomain(entity);
            assertNotNull(user, "Mapped User should not be null");
            assertEquals(UUID.fromString(entity.getId()), user.getId(), "IDs should match");
            assertEquals(entity.getName(), user.getName(), "Names should match");
            assertEquals(entity.getEmail(), user.getEmail(), "Emails should match");
            assertEquals(entity.getCreatedAt(), user.getCreatedAt(), "Creation times should match");
            assertEquals(entity.getUpdatedAt(), user.getUpdatedAt(), "Update times should match");
            assertEquals(entity.isActive(), user.isActive(), "Active statuses should match");
        }

        static Stream<Arguments> provideEntitiesForToDomain() {
            return Stream.of(
                    Arguments.of(FixtureTests.createSampleUserEntity()),
                    Arguments.of(FixtureTests.createSampleUserEntity("Dana", "dana@example.com")),
                    Arguments.of(FixtureTests.createSampleUserEntity("Eve", "eve@example.com"))
            );
        }
    }
}