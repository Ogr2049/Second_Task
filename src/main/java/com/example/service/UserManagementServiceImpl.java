package com.example.service;

import com.example.entity.UserEntity;
import com.example.repository.UserRepositoryInterface;
import com.example.repository.UserRepositoryImplementation;
import com.example.util.ValidationUtils;
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
            if (!ValidationUtils.isValidName(userName)) {
                throw new IllegalArgumentException("Имя должно содержать только буквы и быть от 2 до 50 символов");
            }
            existingUserEntity.setUserName(userName.trim());
        }
        
        if (userEmail != null && !userEmail.trim().isEmpty()) {
            String newEmailValue = userEmail.trim();
            if (!ValidationUtils.isValidEmail(newEmailValue)) {
                throw new IllegalArgumentException("Неверный формат email");
            }
            if (!newEmailValue.equals(existingUserEntity.getUserEmail())) {
                if (myUserRepositoryInstance.checkIfEmailExists(newEmailValue)) {
                    throw new IllegalArgumentException("Email уже используется: " + newEmailValue);
                }
                existingUserEntity.setUserEmail(newEmailValue);
            }
        }
        
        if (userAge != null) {
            if (!ValidationUtils.isValidAge(userAge)) {
                throw new IllegalArgumentException("Возраст должен быть от 1 до 120");
            }
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
        if (!ValidationUtils.isValidName(userName)) {
            throw new IllegalArgumentException("Имя должно содержать только буквы и быть от 2 до 50 символов");
        }
        if (!ValidationUtils.isValidEmail(userEmail)) {
            throw new IllegalArgumentException("Неверный формат email");
        }
        if (!ValidationUtils.isValidAge(userAge)) {
            throw new IllegalArgumentException("Возраст должен быть от 1 до 120");
        }
    }
}