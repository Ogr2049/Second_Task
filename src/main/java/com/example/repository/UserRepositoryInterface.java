package com.example.repository;

import com.example.entity.UserEntity;
import java.util.List;
import java.util.Optional;

public interface UserRepositoryInterface {
    UserEntity MyInsertNewUser(UserEntity myUserEntity);
    Optional<UserEntity> findUserById(Long userIdValue);
    List<UserEntity> retrieveAllUsers();
    UserEntity updateExistingUser(UserEntity myUserEntity);
    boolean removeUserById(Long userIdValue);
    Optional<UserEntity> findUserByEmailAddress(String emailAddress);
    boolean checkIfEmailExists(String emailAddress);
}
