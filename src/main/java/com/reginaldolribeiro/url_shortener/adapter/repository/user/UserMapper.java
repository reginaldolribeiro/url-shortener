package com.reginaldolribeiro.url_shortener.adapter.repository.user;

import com.reginaldolribeiro.url_shortener.app.domain.User;

import java.util.UUID;

public class UserMapper {

    /**
     * Converts a User domain object to a UserEntity for persistence.
     *
     * @param user the User domain object
     * @return the corresponding UserEntity
     */
    public static UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }
        return new UserEntity(
                user.getId().toString(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.isActive()
        );
    }

    /**
     * Converts a UserEntity from persistence to a User domain object.
     *
     * @param entity the UserEntity from the database
     * @return the corresponding User domain object
     */
    public static User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return new User.Builder()
                .id(UUID.fromString(entity.getId()))
                .name(entity.getName())
                .email(entity.getEmail())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .active(entity.isActive())
                .build();
    }
}
