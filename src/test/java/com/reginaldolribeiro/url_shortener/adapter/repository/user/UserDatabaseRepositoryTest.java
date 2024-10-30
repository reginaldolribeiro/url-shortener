package com.reginaldolribeiro.url_shortener.adapter.repository.user;

import com.reginaldolribeiro.url_shortener.FixtureTests;
import com.reginaldolribeiro.url_shortener.adapter.helper.DateTimeHelper;
import com.reginaldolribeiro.url_shortener.app.domain.User;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Disabled
class UserDatabaseRepositoryTest {

    public static final User USER = FixtureTests.createSampleUser();
    public static final String USER_ID = FixtureTests.generateSampleUserId();

    @InjectMocks
    private UserDatabaseRepository userDatabaseRepository;

    @Mock
    private DynamoDbClient dynamoDbClient;

    @Nested
    @DisplayName("save method")
    class SaveTests {

        @Test
        @DisplayName("Should successfully save a user")
        void shouldSaveUserSuccessfully() {
            var putItemResponse = PutItemResponse.builder().build();

            when(dynamoDbClient.putItem(any(PutItemRequest.class))).thenReturn(putItemResponse);

            assertDoesNotThrow(() -> userDatabaseRepository.save(USER));
            verify(dynamoDbClient, times(1)).putItem(any(PutItemRequest.class));
        }

        @Test
        @DisplayName("Should throw UserSaveDatabaseException when DynamoDbException occurs")
        void shouldThrowExceptionWhenDynamoDbExceptionOccurs() {
            when(dynamoDbClient.putItem(any(PutItemRequest.class)))
                    .thenThrow(DynamoDbException.builder().message("DynamoDB error").build());

            var exception = assertThrows(UserSaveDatabaseException.class, () -> {
                userDatabaseRepository.save(USER);
            });

            assertEquals("Failed to save user with ID: " + USER.getId(), exception.getMessage());

            verify(dynamoDbClient, times(1)).putItem(any(PutItemRequest.class));
        }
    }

    @Nested
    @DisplayName("findById method")
    class FindByIdTests {

        @Test
        @DisplayName("Should return user when found")
        void shouldReturnUserWhenFound() {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("id", AttributeValue.builder().s(USER_ID).build());
            item.put("name", AttributeValue.builder().s(FixtureTests.DEFAULT_USER_NAME).build());
            item.put("email", AttributeValue.builder().s(FixtureTests.DEFAULT_USER_EMAIL).build());
            item.put("createdAt", AttributeValue.builder().s(DateTimeHelper.toString(LocalDateTime.now())).build());
            item.put("updatedAt", AttributeValue.builder().s(DateTimeHelper.toString(LocalDateTime.now())).build());
            item.put("active", AttributeValue.builder().bool(true).build());

            var queryResponse = QueryResponse.builder()
                    .items(item)
                    .count(1)
                    .build();

            when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(queryResponse);

            var result = userDatabaseRepository.findById(USER_ID);

            assertTrue(result.isPresent());
            assertNotNull(result.get().getId());
            assertEquals(UUID.class, result.get().getId().getClass());
            assertEquals(FixtureTests.DEFAULT_USER_NAME, result.get().getName());
            assertEquals(FixtureTests.DEFAULT_USER_EMAIL, result.get().getEmail());
            assertTrue(result.get().getCreatedAt().isBefore(LocalDateTime.now(Clock.systemUTC()).plusSeconds(1)));
            assertTrue(result.get().getUpdatedAt().isBefore(LocalDateTime.now(Clock.systemUTC()).plusSeconds(1)));
            assertTrue(result.get().isActive());

            verify(dynamoDbClient, times(1)).query(any(QueryRequest.class));
        }

        @Test
        @DisplayName("Should return empty when user not found")
        void shouldReturnEmptyWhenUserNotFound() {
            var queryResponse = QueryResponse.builder()
                    .items(Collections.emptyList())
                    .count(0)
                    .build();

            when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(queryResponse);

            var result = userDatabaseRepository.findById(USER_ID);

            assertFalse(result.isPresent());

            verify(dynamoDbClient, times(1)).query(any(QueryRequest.class));
        }

        @Test
        @DisplayName("Should throw UserSearchDatabaseException when DynamoDbException occurs")
        void shouldThrowExceptionWhenDynamoDbExceptionOccurs() {
            when(dynamoDbClient.query(any(QueryRequest.class)))
                    .thenThrow(DynamoDbException.builder().message("DynamoDB error").build());

            var exception = assertThrows(UserSearchDatabaseException.class, () -> {
                userDatabaseRepository.findById(USER_ID);
            });

            assertEquals("Failed to search user with ID: " + USER_ID, exception.getMessage());

            verify(dynamoDbClient, times(1)).query(any(QueryRequest.class));
        }

        @ParameterizedTest(name = "Should handle multiple userIds: {0}")
        @ValueSource(strings = {
                "00000000-0000-0000-0000-000000000000",
                "123e4567-e89b-12d3-a456-426614174000",
                "987e6543-e21b-12d3-a456-426614174999"
        })
        @DisplayName("Should return empty for non-existing user IDs")
        void shouldReturnEmptyForNonExistingUserIds(String userId) {
            var queryResponse = QueryResponse.builder()
                    .items(Collections.emptyList())
                    .count(0)
                    .build();

            when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(queryResponse);

            var result = userDatabaseRepository.findById(userId);

            assertFalse(result.isPresent());

            verify(dynamoDbClient, times(1)).query(any(QueryRequest.class));
        }
    }

}