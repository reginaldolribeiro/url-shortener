package com.reginaldolribeiro.url_shortener.adapter.repository.url;

import com.reginaldolribeiro.url_shortener.FixtureTests;
import com.reginaldolribeiro.url_shortener.app.domain.Url;
import com.reginaldolribeiro.url_shortener.app.domain.User;
import com.reginaldolribeiro.url_shortener.app.exception.UserNotFoundException;
import com.reginaldolribeiro.url_shortener.app.port.UserRepositoryPort;
import org.junit.jupiter.api.Assertions;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlDatabaseRepositoryTest {

    public static final Url URL = FixtureTests.createSampleUrl();
    public static final User USER = FixtureTests.createSampleUser();
    private static final UrlEntity URL_ENTITY = FixtureTests.createSampleUrlEntity();

    @InjectMocks
    private UrlDatabaseRepository urlDatabaseRepository;

    @Mock
    private DynamoDbClient dynamoDbClient;

    @Mock
    private UserRepositoryPort userRepositoryPort;


    @Nested
    @DisplayName("save method")
    class SaveTests {

        @Test
        @DisplayName("should save URL successfully")
        void shouldSaveUrlSuccessfully() {
            var putItemResponse = PutItemResponse.builder().build();
            when(dynamoDbClient.putItem(any(PutItemRequest.class))).thenReturn(putItemResponse);

            Assertions.assertDoesNotThrow(() -> urlDatabaseRepository.save(URL));

            verify(dynamoDbClient, times(1)).putItem(any(PutItemRequest.class));
        }

        @Test
        @DisplayName("should throw UrlSaveDatabaseException on DynamoDbException")
        void shouldThrowExceptionOnSaveFailure() {
            doThrow(DynamoDbException.class).when(dynamoDbClient).putItem(any(PutItemRequest.class));
            assertThrows(UrlSaveDatabaseException.class, () -> urlDatabaseRepository.save(URL));

            verify(dynamoDbClient, times(1)).putItem(any(PutItemRequest.class));
        }
    }


    @Nested
    @DisplayName("findByShortenedUrl method")
    class FindByShortenedUrlTests {

        @ParameterizedTest
        @ValueSource(strings = {"short123", "abcDEF", "urlXYZ"})
        @DisplayName("should find URL when it exists")
        void shouldFindUrl(String shortUrl) {
            var urlEntity = FixtureTests.createSampleUrlEntity(shortUrl);
            Map<String, AttributeValue> item = mapToAttributeValue(urlEntity);
            var queryResponse = QueryResponse.builder().items(item).build();

            when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(queryResponse);
            when(userRepositoryPort.findById(urlEntity.userId()))
                    .thenReturn(Optional.of(USER));

            var result = urlDatabaseRepository.findByShortenedUrl(shortUrl);

            assertTrue(result.isPresent());
            assertEquals(shortUrl, result.get().getId());

            verify(dynamoDbClient, times(1)).query(any(QueryRequest.class));
            verify(userRepositoryPort, times(1)).findById(urlEntity.userId());
        }

        @Test
        @DisplayName("should return empty when URL does not exist")
        void shouldReturnEmptyWhenUrlNotFound() {
            var shortUrl = "nonExistent";
            var queryResponse = QueryResponse.builder().items(Collections.emptyList()).build();

            when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(queryResponse);

            var result = urlDatabaseRepository.findByShortenedUrl(shortUrl);

            assertFalse(result.isPresent());

            verify(dynamoDbClient, times(1)).query(any(QueryRequest.class));
            verify(userRepositoryPort, never()).findById(anyString());
        }

        @Test
        @DisplayName("should throw UrlSearchDatabaseException on DynamoDbException")
        void shouldThrowExceptionOnSearchFailure() {
            var shortUrl = "errorUrl";
            when(dynamoDbClient.query(any(QueryRequest.class)))
                    .thenThrow(DynamoDbException.builder().message("DynamoDB error").build());

            assertThrows(UrlSearchDatabaseException.class, () ->
                    urlDatabaseRepository.findByShortenedUrl(shortUrl));

            verify(dynamoDbClient, times(1)).query(any(QueryRequest.class));
        }

        @Test
        @DisplayName("should throw UserNotFoundException when user is missing")
        void shouldThrowUserNotFoundException() {
            Map<String, AttributeValue> item = mapToAttributeValue(URL_ENTITY);
            var queryResponse = QueryResponse.builder().items(item).build();

            when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(queryResponse);
            when(userRepositoryPort.findById(URL_ENTITY.userId())).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () ->
                    urlDatabaseRepository.findByShortenedUrl(FixtureTests.SHORT_URL_CODE));

            verify(dynamoDbClient, times(1)).query(any(QueryRequest.class));
            verify(userRepositoryPort, times(1)).findById(URL_ENTITY.userId());
        }
    }



    private Map<String, AttributeValue> mapToAttributeValue(UrlEntity urlEntity) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("shortUrlId", AttributeValue.builder().s(urlEntity.shortUrlId()).build());
        item.put("longUrl", AttributeValue.builder().s(urlEntity.longUrl()).build());
        item.put("createdAt", AttributeValue.builder().s(urlEntity.createdAt().toString()).build());
        item.put("updatedAt", AttributeValue.builder().s(urlEntity.updatedAt().toString()).build());
        item.put("userId", AttributeValue.builder().s(urlEntity.userId()).build());
        item.put("clicks", AttributeValue.builder().n(String.valueOf(urlEntity.clicks())).build());
        item.put("isActive", AttributeValue.builder().bool(urlEntity.isActive()).build());
        return item;
    }

}