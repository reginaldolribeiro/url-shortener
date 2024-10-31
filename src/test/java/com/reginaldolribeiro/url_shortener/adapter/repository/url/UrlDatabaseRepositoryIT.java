package com.reginaldolribeiro.url_shortener.adapter.repository.url;

import com.reginaldolribeiro.url_shortener.FixtureTests;
import com.reginaldolribeiro.url_shortener.adapter.repository.DynamoDbTestConfiguration;
import com.reginaldolribeiro.url_shortener.app.domain.Url;
import com.reginaldolribeiro.url_shortener.app.port.UserRepositoryPort;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {DynamoDbTestConfiguration.class, UrlDatabaseRepository.class})
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UrlDatabaseRepositoryIT {

    public static final String URL_MAPPINGS_TABLE = "UrlMappings";
    public static final Url URL = FixtureTests.createSampleUrl();

    @Autowired
    private DynamoDbClient dynamoDbClient;

    @Autowired
    private UrlDatabaseRepository urlDatabaseRepository;

    @MockBean
    private UserRepositoryPort userRepositoryPort;

    @BeforeAll
    void setupTable(){
        try {
            dynamoDbClient.describeTable(r -> r.tableName(URL_MAPPINGS_TABLE));
        } catch (ResourceNotFoundException e) {
            dynamoDbClient.createTable(CreateTableRequest.builder()
                    .tableName(URL_MAPPINGS_TABLE)
                    .keySchema(
                            KeySchemaElement.builder()
                                    .attributeName("shortUrlId")  // Primary key (HASH)
                                    .keyType(KeyType.HASH)
                                    .build(),
                            KeySchemaElement.builder()
                                    .attributeName("userId")  // Sort key (RANGE)
                                    .keyType(KeyType.RANGE)
                                    .build()
                    )
                    .attributeDefinitions(
                            AttributeDefinition.builder()
                                    .attributeName("shortUrlId")
                                    .attributeType(ScalarAttributeType.S)
                                    .build(),
                            AttributeDefinition.builder()
                                    .attributeName("userId")
                                    .attributeType(ScalarAttributeType.S)
                                    .build()
                    )
                    .billingMode(BillingMode.PAY_PER_REQUEST)
                    .build());
        }
        urlDatabaseRepository.save(URL);
    }


    @Nested
    @DisplayName("save method")
    class SaveTests {

        @Test
        @DisplayName("should save URL successfully")
        void shouldSaveUrlSuccessfully() {
            var user = FixtureTests.createSampleUser();
            var customUrl = FixtureTests.createCustomUrl(UUID.randomUUID().toString(),
                    "https://example.com/custom-long-url",
                    user);

            when(userRepositoryPort.findById(user.getId().toString())).thenReturn(Optional.of(user));

            var existingUrl = urlDatabaseRepository.findByShortenedUrl(customUrl.getId());
            assertFalse(existingUrl.isPresent(), "URL should not exist before save");

            assertDoesNotThrow(() -> urlDatabaseRepository.save(customUrl));

            var savedUrl = urlDatabaseRepository.findByShortenedUrl(customUrl.getId());
            assertTrue(savedUrl.isPresent(), "URL should exist after save");

            assertAll(
                    () -> assertEquals(customUrl.getId(), savedUrl.get().getId()),
                    () -> assertEquals(customUrl.getLongUrl(), savedUrl.get().getLongUrl()),
                    () -> assertEquals(customUrl.getCreatedAt(), savedUrl.get().getCreatedAt()),
                    () -> assertEquals(customUrl.getUpdatedAt(), savedUrl.get().getUpdatedAt()),
                    () -> assertEquals(customUrl.getUser(), savedUrl.get().getUser()),
                    () -> assertEquals(customUrl.getClicks(), savedUrl.get().getClicks()),
                    () -> assertTrue(savedUrl.get().isActive())
            );

            verify(userRepositoryPort, times(1)).findById(user.getId().toString());
        }

        @Test
        @DisplayName("Should throw an error while try to save a Null Url")
        void shouldThrownErrorForNullUrl() {
            var exception = assertThrows(IllegalArgumentException.class, () -> urlDatabaseRepository.save(null));
            assertNotNull(exception);
            assertEquals("Url cannot be null.", exception.getMessage());
        }

    }

    @Nested
    @DisplayName("Should find a valid Url")
    class ShouldFindUrl {

        @Test
        @DisplayName("Should find an existing Url by ID")
        void shouldFindUrlById(){
            when(userRepositoryPort.findById(URL.getUser().getId().toString()))
                    .thenReturn(Optional.of(URL.getUser()));

            var savedUrl = assertDoesNotThrow(() -> urlDatabaseRepository.findByShortenedUrl(URL.getId()));
            assertTrue(savedUrl.isPresent());
            assertAll(
                    () -> assertEquals(URL.getId(), savedUrl.get().getId()),
                    () -> assertEquals(URL.getLongUrl(), savedUrl.get().getLongUrl()),
                    () -> assertEquals(URL.getCreatedAt(), savedUrl.get().getCreatedAt()),
                    () -> assertEquals(URL.getUpdatedAt(), savedUrl.get().getUpdatedAt()),
                    () -> assertEquals(URL.getUser(), savedUrl.get().getUser()),
                    () -> assertEquals(URL.getClicks(), savedUrl.get().getClicks()),
                    () -> assertTrue(savedUrl.get().isActive())
            );
            verify(userRepositoryPort, times(1)).findById(URL.getUser().getId().toString());
        }

        @Test
        @DisplayName("Should return empty when Url not found by ID")
        void shouldReturnEmptyWhenUrlNotFoundById() {
            var nonExistentId = UUID.randomUUID().toString();
            var url = assertDoesNotThrow(() -> urlDatabaseRepository.findByShortenedUrl(nonExistentId));

            assertTrue(url.isEmpty());
            verifyNoInteractions(userRepositoryPort);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when ID is null")
        void shouldThrowExceptionWhenIdIsNull() {
            var exception = assertThrows(IllegalArgumentException.class, () -> urlDatabaseRepository.findByShortenedUrl(null));
            assertEquals("Url cannot be null.", exception.getMessage());
            verifyNoInteractions(userRepositoryPort);
        }

    }


    @AfterAll
    void teardownTable(){
        // Delete the table after all tests are complete
        try {
            dynamoDbClient.deleteTable(DeleteTableRequest.builder()
                    .tableName(URL_MAPPINGS_TABLE)
                    .build());
        } catch (ResourceNotFoundException e) {
            // Table may already be deleted
        }
    }

}
