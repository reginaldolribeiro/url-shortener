package com.reginaldolribeiro.url_shortener.adapter.repository.url;


class UrlDatabaseRepositoryTest{}

/*
import com.reginaldolribeiro.url_shortener.FixtureTests;
import com.reginaldolribeiro.url_shortener.app.domain.Url;
import com.reginaldolribeiro.url_shortener.app.domain.User;
import com.reginaldolribeiro.url_shortener.app.port.UserRepositoryPort;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Disabled
class UrlDatabaseRepositoryTest {

    public static final Url URL = FixtureTests.createSampleUrl();
    public static final User USER = FixtureTests.createSampleUser();
    private static final UrlEntity URL_ENTITY = FixtureTests.createSampleUrlEntity();

    @InjectMocks
    private UrlDatabaseRepository urlDatabaseRepository;

    @Mock
    private UrlDynamoDbRepository urlDynamoDbRepository;
    @Mock
    private UserRepositoryPort userRepositoryPort;



//    @BeforeEach
//    void setUp() {
//        when(dynamoDbEnhancedClient.table(URL_MAPPINGS, TableSchema.fromBean(UrlEntity.class)))
//                .thenReturn(urlTable);
//        urlDatabaseRepository = new UrlDatabaseRepository(userRepositoryPort, urlDynamoDbRepository);
//    }


    @Nested
    @DisplayName("save method")
    class SaveTests {

        @Test
        @DisplayName("should save URL successfully")
        void shouldSaveUrlSuccessfully() {
            doNothing().when(urlTable).putItem(any(UrlEntity.class));

            assertDoesNotThrow(() -> urlDatabaseRepository.save(URL));
            verify(urlTable, times(1)).putItem(any(UrlEntity.class));
        }

//        @Test
//        @DisplayName("should save URL successfully")
//        void shouldSaveUrlSuccessfully() {
//            // Mock the table method directly in the test
//            when(dynamoDbEnhancedClient.table(URL_MAPPINGS, TableSchema.fromBean(UrlEntity.class)))
//                    .thenReturn(urlTable);
//
//            // Initialize the repository after setting up the mock
//            UrlDatabaseRepository urlDatabaseRepository = new UrlDatabaseRepository(userRepositoryPort, dynamoDbEnhancedClient);
//
//            // Mock the putItem action on the urlTable
//            doNothing().when(urlTable).putItem(any(UrlEntity.class));
//
//            // Execute the save method
//            assertDoesNotThrow(() -> urlDatabaseRepository.save(URL));
//
//            // Verify that putItem was called
//            verify(urlTable, times(1)).putItem(any(UrlEntity.class));
//        }

        @Test
        @DisplayName("should throw UrlSaveDatabaseException on DynamoDbException")
        void shouldThrowExceptionOnSaveFailure() {
            doThrow(DynamoDbException.class).when(urlTable).putItem(any(UrlEntity.class));
            assertThrows(UrlSaveDatabaseException.class, () -> urlDatabaseRepository.save(URL));
            verify(urlTable, times(1)).putItem(any(UrlEntity.class));
        }
    }


    @Nested
    @DisplayName("findByShortenedUrl method")
    class FindByShortenedUrlTests {

//        @ParameterizedTest
//        @ValueSource(strings = {"short123", "abcDEF", "urlXYZ"})
//        @DisplayName("should find URL when it exists")
//        void shouldFindUrl(String shortUrl) {
//            // Mock the results to return a single page containing `URL_ENTITY`
//            var results = mock(PageIterable.class);
//            var page = mock(Page.class);
//            when(page.items()).thenReturn(Collections.singletonList(URL_ENTITY));
//            when(results.stream()).thenReturn(Stream.of(page));
//
//            // Use `any(QueryConditional.class)` to avoid strict stubbing issues
//            when(urlTable.query(any(QueryConditional.class))).thenReturn(results);
//            when(userRepositoryPort.findById(URL_ENTITY.getUserId()))
//                    .thenReturn(Optional.of(USER));
//
//            var result = urlDatabaseRepository.findByShortenedUrl(shortUrl);
//
//            assertTrue(result.isPresent());
//            assertEquals(shortUrl, result.get().getId());
//
//            verify(urlTable, times(1)).query(any(QueryConditional.class));
//            verify(userRepositoryPort, times(1)).findById(URL_ENTITY.getUserId());
//        }

//        @Test
//        @DisplayName("should return empty when URL does not exist")
//        void shouldReturnEmptyWhenUrlNotFound() {
//            // Mock an empty Page and PageIterable
//            Page<UrlEntity> emptyPage = mock(Page.class);
//            when(emptyPage.items()).thenReturn(Collections.emptyList()); // Ensure items() is not null
//
//            PageIterable<UrlEntity> emptyResults = mock(PageIterable.class);
//            when(emptyResults.stream()).thenReturn(Stream.of(emptyPage)); // Return a stream with the empty page
//
//            // Stub the query method on urlTable to return emptyResults
//            doReturn(emptyResults).when(urlTable).query(any(Consumer.class));
//
//            // Execute the method under test
//            var result = urlDatabaseRepository.findByShortenedUrl("nonExistentUrl");
//
//            // Assertions and verifications
//            assertFalse(result.isPresent());
//            verify(urlTable).query(any(Consumer.class));
//            verify(userRepositoryPort, never()).findById(anyString());
//        }

        @Test
        @DisplayName("should return empty when URL does not exist")
        void shouldReturnEmptyWhenUrlNotFound() {
            // Use the helper method to create a fully controlled empty PageIterable
            PageIterable<UrlEntity> emptyResults = createEmptyPageIterable();

            // Stub the query method on urlTable to return emptyResults
            doReturn(emptyResults).when(urlTable).query(any(Consumer.class));

            // Execute the method under test
            var result = urlDatabaseRepository.findByShortenedUrl("nonExistentUrl");

            // Assertions and verifications
            assertFalse(result.isPresent(), "Expected no results when URL does not exist");
            verify(urlTable).query(any(Consumer.class));
            verify(userRepositoryPort, never()).findById(anyString());
        }

//        @Test
//        @DisplayName("should throw UrlSearchDatabaseException on DynamoDbException")
//        void shouldThrowExceptionOnSearchFailure() {
//            var shortUrl = "errorUrl";
//            when(dynamoDbClient.query(any(QueryRequest.class)))
//                    .thenThrow(DynamoDbException.builder().message("DynamoDB error").build());
//
//            assertThrows(UrlSearchDatabaseException.class, () ->
//                    urlDatabaseRepository.findByShortenedUrl(shortUrl));
//
//            verify(dynamoDbClient, times(1)).query(any(QueryRequest.class));
//        }
//
//        @Test
//        @DisplayName("should throw UserNotFoundException when user is missing")
//        void shouldThrowUserNotFoundException() {
//            Map<String, AttributeValue> item = mapToAttributeValue(URL_ENTITY);
//            var queryResponse = QueryResponse.builder().items(item).build();
//
//            when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(queryResponse);
//            when(userRepositoryPort.findById(URL_ENTITY.getUserId())).thenReturn(Optional.empty());
//
//            assertThrows(UserNotFoundException.class, () ->
//                    urlDatabaseRepository.findByShortenedUrl(FixtureTests.SHORT_URL_CODE));
//
//            verify(dynamoDbClient, times(1)).query(any(QueryRequest.class));
//            verify(userRepositoryPort, times(1)).findById(URL_ENTITY.getUserId());
//        }
    }



    private Map<String, AttributeValue> mapToAttributeValue(UrlEntity urlEntity) {

        var json = """
                {
                    "id": %s,
                    "name": %s
                }
                """.formatted("1", "regin");



        Map<String, AttributeValue> item = new HashMap<>();
        item.put("shortUrlId", AttributeValue.builder().s(urlEntity.getShortUrlId()).build());
        item.put("longUrl", AttributeValue.builder().s(urlEntity.getLongUrl()).build());
        item.put("createdAt", AttributeValue.builder().s(urlEntity.getCreatedAt().toString()).build());
        item.put("updatedAt", AttributeValue.builder().s(urlEntity.getUpdatedAt().toString()).build());
        item.put("userId", AttributeValue.builder().s(urlEntity.getUserId()).build());
        item.put("clicks", AttributeValue.builder().n(String.valueOf(urlEntity.getClicks())).build());
        item.put("isActive", AttributeValue.builder().bool(urlEntity.isActive()).build());
        return item;
    }

    // Helper method to create a mocked empty PageIterable
    private <T> PageIterable<T> createEmptyPageIterable() {
        Page<T> emptyPage = mock(Page.class);
        when(emptyPage.items()).thenReturn(Collections.emptyList());

        PageIterable<T> emptyPageIterable = mock(PageIterable.class);
        when(emptyPageIterable.stream()).thenReturn(Stream.of(emptyPage));

        return emptyPageIterable;
    }

}*/
