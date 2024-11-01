package com.reginaldolribeiro.url_shortener.adapter.repository.url;

import com.reginaldolribeiro.url_shortener.app.domain.Url;
import com.reginaldolribeiro.url_shortener.app.domain.User;
import com.reginaldolribeiro.url_shortener.app.exception.UserNotFoundException;
import com.reginaldolribeiro.url_shortener.app.port.UrlRepositoryPort;
import com.reginaldolribeiro.url_shortener.app.port.UserRepositoryPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.Optional;


@Service
@Slf4j
public class UrlDatabaseRepository implements UrlRepositoryPort {

    public static final String URL_MAPPINGS = "UrlMappings";

    private final UserRepositoryPort userRepositoryPort;
    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final DynamoDbTable<UrlEntity> urlTable;

    public UrlDatabaseRepository(UserRepositoryPort userRepositoryPort, DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.userRepositoryPort = userRepositoryPort;
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
        this.urlTable = dynamoDbEnhancedClient.table(URL_MAPPINGS, TableSchema.fromBean(UrlEntity.class));
    }

    @Override
    @CachePut(value = "urlCache", key = "#url.id", unless = "#result == null")
    public Url save(Url url) {
        log.info("Saving URL to database ....");
        if (url == null) {
            throw new IllegalArgumentException("Url cannot be null.");
        }

        var urlEntity = UrlMapper.toEntity(url);
        try {
            urlTable.putItem(urlEntity);
            return UrlMapper.toDomain(urlEntity, url.getUser());
        } catch (DynamoDbException e) {
            log.error("Error saving URL with ID: {}", urlEntity.getShortUrlId(), e);
            throw new UrlSaveDatabaseException("Failed to save URL with ID: " + urlEntity.getShortUrlId(), e);
        }
    }

    @Override
    @Cacheable(value = "urlCache", key = "#shortenedUrl")
    public Optional<Url> findByShortenedUrl(String shortenedUrl) {
        log.info("Searching for {} in the database", shortenedUrl);
        if(shortenedUrl == null || shortenedUrl.isBlank())
            throw new IllegalArgumentException("Url cannot be null.");

        try {
            QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                    .partitionValue(shortenedUrl)
                    .build());

            var results = urlTable.query(r -> r.queryConditional(queryConditional));
            var urlEntity = results.items().stream().findFirst();

            var url = urlEntity.map(entity -> UrlMapper.toDomain(entity, getUser(entity.getUserId())));
            return url.isPresent() ? url : Optional.empty();

        } catch (DynamoDbException e) {
            log.error("Error finding user with ID: {}", shortenedUrl, e);
            throw new UrlSearchDatabaseException("Failed to search URL with ID: " + shortenedUrl, e);
        }
    }

    private User getUser(String userId) {
        return userRepositoryPort.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User " + userId + " not found."));
    }

}