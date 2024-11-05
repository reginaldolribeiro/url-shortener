package com.reginaldolribeiro.url_shortener.adapter.repository.url;

import com.reginaldolribeiro.url_shortener.app.domain.Url;
import com.reginaldolribeiro.url_shortener.app.domain.User;
import com.reginaldolribeiro.url_shortener.app.exception.UserNotFoundException;
import com.reginaldolribeiro.url_shortener.app.port.UrlRepositoryPort;
import com.reginaldolribeiro.url_shortener.app.port.UserRepositoryPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Slf4j
public class UrlDatabaseRepository implements UrlRepositoryPort {

    private final UrlDynamoDbRepository urlDynamoDbRepository;
    private final UserRepositoryPort userRepositoryPort;

    public UrlDatabaseRepository(UrlDynamoDbRepository urlDynamoDbRepository, UserRepositoryPort userRepositoryPort) {
        this.urlDynamoDbRepository = urlDynamoDbRepository;
        this.userRepositoryPort = userRepositoryPort;
    }


    @Override
    public Url save(Url url) {
        if (url == null) {
            throw new IllegalArgumentException("Url cannot be null.");
        }
        var saveEntity = urlDynamoDbRepository.save(UrlMapper.toEntity(url));
        var user = getUser(saveEntity.getUserId());
        return UrlMapper.toDomain(saveEntity, user);
    }

    @Override
    public Optional<Url> findByShortenedUrl(String id) {
        if (id == null || id.isBlank())
            throw new IllegalArgumentException("Url cannot be null.");

        return urlDynamoDbRepository.findByShortenedUrl(id)
                .map(savedEntity -> UrlMapper.toDomain(savedEntity, getUser(savedEntity.getUserId())))
                .stream().findFirst();
    }

    private User getUser(String userId) {
        return userRepositoryPort.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User " + userId + " not found."));
    }

}