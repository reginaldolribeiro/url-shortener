package com.reginaldolribeiro.url_shortener.adapter.controller.user;

import com.reginaldolribeiro.url_shortener.FixtureTests;
import com.reginaldolribeiro.url_shortener.adapter.configuration.JacksonConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = JacksonConfig.class) // Load custom ObjectMapper from JacksonConfig
class UserResponseTest {

    public static final UserResponse USER_RESPONSE = FixtureTests.sampleActiveUserResponse();

    @Autowired
    private JacksonTester<UserResponse> json;

    @Nested
    @DisplayName("UserResponse should Serialize to JSON in snake_case")
    class ShouldSerialize {

        @Test
        @DisplayName("UserResponse should serialize to JSON in snake_case")
        void shouldSerializeUserResponseToSnakeCase() throws Exception {
            var result = json.write(USER_RESPONSE);

            assertThat(result).hasJsonPathStringValue("$.id");
            assertThat(result).hasJsonPathStringValue("$.name", USER_RESPONSE.name());
            assertThat(result).hasJsonPathStringValue("$.email", USER_RESPONSE.email());
            assertThat(result).hasJsonPathStringValue("$.created_at", USER_RESPONSE.createdAt());
            assertThat(result).hasJsonPathStringValue("$.updated_at", USER_RESPONSE.updatedAt());
            assertThat(result).hasJsonPathBooleanValue("$.active", USER_RESPONSE.active());
        }

        @Test
        @DisplayName("UserResponse should serialize boolean fields as JSON true/false")
        void shouldSerializeBooleanFieldsAsJsonTrueFalse() throws Exception {
            UserResponse activeUser = FixtureTests.sampleActiveUserResponse();
            UserResponse inactiveUser = FixtureTests.sampleInactiveUserResponse();

            assertThat(json.write(activeUser)).hasJsonPathBooleanValue("$.active", true);
            assertThat(json.write(inactiveUser)).hasJsonPathBooleanValue("$.active", false);
        }

        @Test
        @DisplayName("UserResponse should serialize an empty name field correctly")
        void shouldSerializeEmptyNameFieldCorrectly() throws Exception {
            UserResponse userResponse = new UserResponse(
                    UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                    "", // Empty name
                    "user@example.com",
                    LocalDateTime.of(2024, 10, 30, 15, 45),
                    LocalDateTime.of(2024, 10, 30, 12, 30),
                    true
            );

            var result = json.write(userResponse);

            assertThat(result).hasJsonPathStringValue("$.name", "");
        }

    }


    @Nested
    @DisplayName("UserResponse should Deserialize to JSON in snake_case")
    class ShouldDeserialize {

        @Test
        @DisplayName("UserResponse should deserialize from JSON in snake_case")
        void shouldDeserializeUserResponseFromSnakeCase() throws Exception {
            String jsonContent = """
            {
                "id": "123e4567-e89b-12d3-a456-426614174000",
                "name": "User",
                "email": "user@example.com",
                "created_at": "2024-10-30T15:45:00",
                "updated_at": "2024-10-30T12:30:00",
                "active": true
            }
            """;

            var userResponse = json.parseObject(jsonContent);

            assertThat(userResponse.id()).isEqualTo(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
            assertThat(userResponse.name()).isEqualTo("User");
            assertThat(userResponse.email()).isEqualTo("user@example.com");
            assertThat(userResponse.createdAt()).isEqualTo(LocalDateTime.of(2024, 10, 30, 15, 45));
            assertThat(userResponse.updatedAt()).isEqualTo(LocalDateTime.of(2024, 10, 30, 12, 30));
            assertThat(userResponse.active()).isTrue();
        }

        @Test
        @DisplayName("UserResponse should deserialize from JSON with extra fields in snake_case ")
        void shouldDeserializeUserResponseWithExtraFieldsFromSnakeCase() throws Exception {
            String jsonContent = """
            {
                "id": "123e4567-e89b-12d3-a456-426614174000",
                "name": "User",
                "email": "user@example.com",
                "created_at": "2024-10-30T15:45:00",
                "updated_at": "2024-10-30T12:30:00",
                "active": true,
                "test": "test"
            }
            """;

            var userResponse = json.parseObject(jsonContent);

            assertThat(userResponse.id()).isEqualTo(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
            assertThat(userResponse.name()).isEqualTo("User");
            assertThat(userResponse.email()).isEqualTo("user@example.com");
            assertThat(userResponse.createdAt()).isEqualTo(LocalDateTime.of(2024, 10, 30, 15, 45));
            assertThat(userResponse.updatedAt()).isEqualTo(LocalDateTime.of(2024, 10, 30, 12, 30));
            assertThat(userResponse.active()).isTrue();
        }

    }

}