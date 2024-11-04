package com.reginaldolribeiro.url_shortener.adapter.repository.user;

import com.reginaldolribeiro.url_shortener.app.domain.User;
import com.reginaldolribeiro.url_shortener.app.port.UserRepositoryPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.Optional;

@Service
@Slf4j
public class UserDatabaseRepository implements UserRepositoryPort {

    public static final String USER_TABLE = "User";

    private final UserDynamoDbRepository userDynamoDbRepository;

    public UserDatabaseRepository(UserDynamoDbRepository userDynamoDbRepository) {
        this.userDynamoDbRepository = userDynamoDbRepository;
    }

    @Override
    public Optional<User> findById(String id) {
        if (id == null || id.isBlank())
            throw new IllegalArgumentException("User ID cannot be null.");

        try {
            var optionalUserEntity = userDynamoDbRepository.findById(id);
            return optionalUserEntity.isPresent() ? Optional.of(UserMapper.toDomain(optionalUserEntity.get())) : Optional.empty();
        } catch (DynamoDbException e) {
            log.error("Error finding user with ID: {}", id, e);
            throw new UserSearchDatabaseException("Failed to search user with ID: " + id, e);
        }
    }

    @Override
    public User save(User user) {
        var userEntity = UserMapper.toEntity(user);
        if (userEntity == null) {
            throw new IllegalArgumentException("User entity cannot be null.");
        }
        try {
            var savedUserEntity = userDynamoDbRepository.save(userEntity);
            return UserMapper.toDomain(savedUserEntity);
        } catch (DynamoDbException e) {
            log.error("Error saving user with ID: {}", userEntity.getId(), e);
            throw new UserSaveDatabaseException("Failed to save user with ID: " + userEntity.getId(), e);
        }
    }

}
