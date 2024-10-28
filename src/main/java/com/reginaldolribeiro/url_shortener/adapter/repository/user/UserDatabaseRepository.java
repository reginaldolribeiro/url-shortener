package com.reginaldolribeiro.url_shortener.adapter.repository.user;

import com.reginaldolribeiro.url_shortener.app.domain.User;
import com.reginaldolribeiro.url_shortener.app.port.UserRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDatabaseRepository implements UserRepositoryPort {

    private final UserDynamoDBRepository userDynamoDBRepository;

    public UserDatabaseRepository(UserDynamoDBRepository userDynamoDBRepository) {
        this.userDynamoDBRepository = userDynamoDBRepository;
    }

    @Override
    public Optional<User> findById(String userId) {
        return userDynamoDBRepository.findById(userId);
    }

    @Override
    public void save(User user) {
        var userEntity = new UserEntity(user.getId().toString(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.isActive());
        userDynamoDBRepository.save(userEntity);
    }

}
