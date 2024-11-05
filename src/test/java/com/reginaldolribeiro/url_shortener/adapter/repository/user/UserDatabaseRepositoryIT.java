package com.reginaldolribeiro.url_shortener.adapter.repository.user;

import com.reginaldolribeiro.url_shortener.FixtureTests;
import com.reginaldolribeiro.url_shortener.adapter.repository.DynamoDbTestConfiguration;
import com.reginaldolribeiro.url_shortener.app.domain.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {DynamoDbTestConfiguration.class, UserDatabaseRepository.class, UserDynamoDbRepository.class})
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDatabaseRepositoryIT {

    public static final User USER = FixtureTests.createSampleUser();
    public static final String USER_TABLE_NAME = "User";

    @Autowired
    private UserDatabaseRepository userDatabaseRepository;

    @Autowired
    private UserDynamoDbRepository userDynamoDbRepository;

    @Autowired
    private DynamoDbClient dynamoDbClient;

    @BeforeAll
    void setupTable(){
        try {
            dynamoDbClient.describeTable(r -> r.tableName(USER_TABLE_NAME));
        } catch (ResourceNotFoundException e) {
            dynamoDbClient.createTable(CreateTableRequest.builder()
                    .tableName(USER_TABLE_NAME)
                    .keySchema(
                            KeySchemaElement.builder()
                                    .attributeName("id")  // Primary key (HASH)
                                    .keyType("HASH")
                                    .build(),
                            KeySchemaElement.builder()
                                    .attributeName("email")  // Sort key (RANGE)
                                    .keyType("RANGE")
                                    .build()
                    )
                    .attributeDefinitions(
                            AttributeDefinition.builder()
                                    .attributeName("id")
                                    .attributeType(ScalarAttributeType.S)
                                    .build(),
                            AttributeDefinition.builder()
                                    .attributeName("email")
                                    .attributeType(ScalarAttributeType.S)
                                    .build()
                    )
                    .billingMode("PAY_PER_REQUEST")
                    .build());
        }
        userDatabaseRepository.save(USER);
    }

    /*@BeforeEach
    void setupEach(){
        System.out.println("Setup each ...");
        userDatabaseRepository.save(USER);
    }*/


    @Nested
    @DisplayName("save method")
    class SaveTests {

        @Test
        @DisplayName("Should successfully save a user")
        void shouldSaveUserSuccessfully() {
            var customUser = FixtureTests.createCustomUser(UUID.randomUUID(),
                    "Custom User",
                    "customuser@example.com");
            var customUserId = customUser.getId().toString();

            // Step 1: Verify the record does not exist
            var existingUser = userDatabaseRepository.findById(customUserId);
            assertFalse(existingUser.isPresent(), "User should not exist before save");

            // Step 2: Perform the save operation
            assertDoesNotThrow(() -> userDatabaseRepository.save(customUser));

            // Step 3: Verify the record now exists
            var savedUser = userDatabaseRepository.findById(customUserId);
            assertTrue(savedUser.isPresent(), "User should exist after save");

            assertAll(
                    () -> assertEquals(customUser.getId(), savedUser.get().getId()),
                    () -> assertEquals(customUser.getName(), savedUser.get().getName()),
                    () -> assertEquals(customUser.getEmail(), savedUser.get().getEmail()),
                    () -> assertEquals(customUser.getCreatedAt(), savedUser.get().getCreatedAt()),
                    () -> assertEquals(customUser.getUpdatedAt(), savedUser.get().getUpdatedAt()),
                    () -> assertTrue(savedUser.get().isActive())
            );
        }

        @Test
        @DisplayName("Should throw an error while try to save a Null User")
        void shouldThrownErrorForNullUser() {
            var exception = assertThrows(IllegalArgumentException.class, () -> userDatabaseRepository.save(null));
            assertNotNull(exception);
            assertEquals("User entity cannot be null.", exception.getMessage());
        }
    }


    @Nested
    @DisplayName("Should find a valid User")
    class ShouldFindUser {

        @Test
        @DisplayName("Should find an existing user by ID")
        void shouldFindUserById(){
//            userDatabaseRepository.save(USER);
            var savedUser = assertDoesNotThrow(() -> userDatabaseRepository.findById(USER.getId().toString()));
            assertTrue(savedUser.isPresent());
            assertAll(
                    () -> assertEquals(USER.getId(), savedUser.get().getId()),
                    () -> assertEquals(USER.getName(), savedUser.get().getName()),
                    () -> assertEquals(USER.getEmail(), savedUser.get().getEmail()),
                    () -> assertEquals(USER.getCreatedAt(), savedUser.get().getCreatedAt()),
                    () -> assertEquals(USER.getUpdatedAt(), savedUser.get().getUpdatedAt()),
                    () -> assertTrue(savedUser.get().isActive())
            );
        }

        @Test
        @DisplayName("Should return empty when user not found by ID")
        void shouldReturnEmptyWhenUserNotFoundById() {
            var nonExistentId = UUID.randomUUID().toString();
            var user = assertDoesNotThrow(() -> userDatabaseRepository.findById(nonExistentId));

            assertTrue(user.isEmpty());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when ID is null")
        void shouldThrowExceptionWhenIdIsNull() {
            var exception = assertThrows(IllegalArgumentException.class, () -> userDatabaseRepository.findById(null));
            assertEquals("User ID cannot be null.", exception.getMessage());
        }

    }

    @AfterAll
    void teardownTable(){
        // Delete the table after all tests are complete
        try {
            dynamoDbClient.deleteTable(DeleteTableRequest.builder()
                    .tableName("User")
                    .build());
        } catch (ResourceNotFoundException e) {
            // Table may already be deleted
        }
    }

    /*
        @AfterEach
        void teardownEach(){
            // Delete the user record after each test to maintain a clean state
            dynamoDbClient.deleteItem(DeleteItemRequest.builder()
                    .tableName(USER_TABLE_NAME)
                    .key(Map.of(
                            "id", AttributeValue.builder().s(USER_ID).build(),
                            "email", AttributeValue.builder().s(USER.getEmail()).build()
                    ))
                    .build())
        }
    */

}