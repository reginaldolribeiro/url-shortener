package com.reginaldolribeiro.url_shortener.adapter.repository.user;

import com.reginaldolribeiro.url_shortener.app.domain.User;
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
public class UserDatabaseRepository implements UserRepositoryPort {

    public static final String USER_TABLE = "User";

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final DynamoDbTable<UserEntity> userTable;

    public UserDatabaseRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
        this.userTable = dynamoDbEnhancedClient.table(USER_TABLE, TableSchema.fromBean(UserEntity.class));
    }

    @Override
//    @Cacheable(value = "userCache", key = "'userCache::' + #userId", unless = "#result == null or #result.isEmpty()")
//    @Cacheable(value = "userCache", key = "'userCache::' + #userId", unless = "#result == null or !#result.isPresent()")
//    @Cacheable(value = "userCache", key = "'userCache::' + #userId", unless = "#result?.isEmpty()")
    @Cacheable(value = "userCache", key = "#userId", unless = "#result == null")
    public Optional<User> findById(String userId) {
        if(userId == null || userId.isBlank())
            throw new IllegalArgumentException("User ID cannot be null.");

        try {
            QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                    .partitionValue(userId)
                    .build());

            var results = userTable.query(r -> r.queryConditional(queryConditional));
            var userEntity = results.items().stream().findFirst();

            var optionalUser = userEntity.map(UserMapper::toDomain);

            // Return empty Optional if no result to avoid caching null values
            return optionalUser.isPresent() ? optionalUser : Optional.empty();

        } catch (DynamoDbException e) {
            log.error("Error finding user with ID: {}", userId, e);
            throw new UserSearchDatabaseException("Failed to search user with ID: " + userId, e);
        }
    }

    @Override
    @CachePut(value = "userCache", key = "#user.id", unless = "#result == null")
    public User save(User user) {
        var userEntity = UserMapper.toEntity(user);
        if (userEntity == null) {
            throw new IllegalArgumentException("User entity cannot be null.");
        }
        try {
            userTable.putItem(userEntity);
            return UserMapper.toDomain(userEntity);
        } catch (DynamoDbException e) {
            log.error("Error saving user with ID: {}", userEntity.getId(), e);
            throw new UserSaveDatabaseException("Failed to save user with ID: " + userEntity.getId(), e);
        }
    }

}
