package com.example.service;

import com.example.entity.UserEntity;
import com.example.repository.UserRepositoryInterface;
import com.example.repository.UserRepositoryImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class UserManagementServiceImpl {
    private static final Logger myLoggerInstance = LoggerFactory.getLogger(UserManagementServiceImpl.class);
    private final UserRepositoryInterface myUserRepositoryInstance;
    
    public UserManagementServiceImpl() {
        this.myUserRepositoryInstance = new UserRepositoryImplementation();
    }
    
    public UserManagementServiceImpl(UserRepositoryInterface myUserRepositoryInstance) {
        this.myUserRepositoryInstance = myUserRepositoryInstance;
    }
    
    public UserEntity registerNewUser(String userName, String userEmail, Integer userAge) {
        validateUserInputData(userName, userEmail, userAge);
        
        if (myUserRepositoryInstance.checkIfEmailExists(userEmail)) {
            throw new IllegalArgumentException("Email адрес уже зарегистрирован: " + userEmail);
        }
        
        UserEntity newUserEntity = new UserEntity(userName, userEmail, userAge);
        return myUserRepositoryInstance.MyInsertNewUser(newUserEntity);
    }
    
    public Optional<UserEntity> getUserByIdValue(Long userIdValue) {
        if (userIdValue == null || userIdValue <= 0) {
            throw new IllegalArgumentException("Неверный ID пользователя");
        }
        return myUserRepositoryInstance.findUserById(userIdValue);
    }
    
    public List<UserEntity> getAllUsersList() {
        return myUserRepositoryInstance.retrieveAllUsers();
    }
    
    public UserEntity modifyUserData(Long userIdValue, String userName, String userEmail, Integer userAge) {
        UserEntity existingUserEntity = myUserRepositoryInstance.findUserById(userIdValue)
            .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден с ID: " + userIdValue));
        
        if (userName != null && !userName.trim().isEmpty()) {
            existingUserEntity.setUserName(userName.trim());
        }
        
        if (userEmail != null && !userEmail.trim().isEmpty()) {
            String newEmailValue = userEmail.trim();
            if (!newEmailValue.equals(existingUserEntity.getUserEmail())) {
                if (myUserRepositoryInstance.checkIfEmailExists(newEmailValue)) {
                    throw new IllegalArgumentException("Email уже используется: " + newEmailValue);
                }
                existingUserEntity.setUserEmail(newEmailValue);
            }
        }
        
        if (userAge != null) {
            validateUserAgeValue(userAge);
            existingUserEntity.setUserAge(userAge);
        }
        
        return myUserRepositoryInstance.updateExistingUser(existingUserEntity);
    }
    
    public boolean deleteUserById(Long userIdValue) {
        if (userIdValue == null || userIdValue <= 0) {
            throw new IllegalArgumentException("Неверный ID пользователя");
        }
        return myUserRepositoryInstance.removeUserById(userIdValue);
    }
    
    public Optional<UserEntity> findUserByEmailString(String userEmail) {
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Email не может быть пустым");
        }
        return myUserRepositoryInstance.findUserByEmailAddress(userEmail.trim());
    }
    
    private void validateUserInputData(String userName, String userEmail, Integer userAge) {
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя пользователя обязательно");
        }
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Email адрес обязателен");
        }
        if (!isValidEmailFormat(userEmail)) {
            throw new IllegalArgumentException("Неверный формат email");
        }
        validateUserAgeValue(userAge);
    }
    
    private void validateUserAgeValue(Integer userAge) {
        if (userAge == null) {
            throw new IllegalArgumentException("Возраст обязателен");
        }
        if (userAge < 1 || userAge > 120) {
            throw new IllegalArgumentException("Возраст должен быть от 1 до 120");
        }
    }
    
    private boolean isValidEmailFormat(String userEmail) {
        return userEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
