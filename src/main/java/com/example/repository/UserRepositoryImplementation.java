package com.example.repository;

import com.example.entity.UserEntity;
import com.example.config.DatabaseConfigurationManager;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class UserRepositoryImplementation implements UserRepositoryInterface {
    private static final Logger myLoggerInstance = LoggerFactory.getLogger(UserRepositoryImplementation.class);
    
    @Override
    public UserEntity MyInsertNewUser(UserEntity myUserEntity) {
        Transaction myTransaction = null;
        Session mySession = DatabaseConfigurationManager.getDatabaseSession();
        try {
            myTransaction = mySession.beginTransaction();
            mySession.persist(myUserEntity);
            myTransaction.commit();
            myLoggerInstance.info("Created user with ID: {}", myUserEntity.getUserId());
            return myUserEntity;
        } catch (Exception myException) {
            if (myTransaction != null) myTransaction.rollback();
            myLoggerInstance.error("Failed to create user: {}", myException.getMessage());
            throw new DataAccessCustomException("Failed to create user", myException);
        } finally {
            mySession.close();
        }
    }
    
    @Override
    public Optional<UserEntity> findUserById(Long userIdValue) {
        Session mySession = DatabaseConfigurationManager.getDatabaseSession();
        try {
            UserEntity foundUser = mySession.find(UserEntity.class, userIdValue);
            return Optional.ofNullable(foundUser);
        } catch (Exception myException) {
            myLoggerInstance.error("Failed to find user by ID {}: {}", userIdValue, myException.getMessage());
            throw new DataAccessCustomException("Failed to find user by ID", myException);
        } finally {
            mySession.close();
        }
    }
    
    @Override
    public List<UserEntity> retrieveAllUsers() {
        Session mySession = DatabaseConfigurationManager.getDatabaseSession();
        try {
            TypedQuery<UserEntity> myQuery = mySession.createNamedQuery("UserEntity.findAllUsers", UserEntity.class);
            return myQuery.getResultList();
        } catch (Exception myException) {
            myLoggerInstance.error("Failed to find all users: {}", myException.getMessage());
            throw new DataAccessCustomException("Failed to find all users", myException);
        } finally {
            mySession.close();
        }
    }
    
    @Override
    public UserEntity updateExistingUser(UserEntity myUserEntity) {
        Transaction myTransaction = null;
        Session mySession = DatabaseConfigurationManager.getDatabaseSession();
        try {
            myTransaction = mySession.beginTransaction();
            UserEntity mergedUser = (UserEntity) mySession.merge(myUserEntity);
            myTransaction.commit();
            myLoggerInstance.info("Updated user with ID: {}", myUserEntity.getUserId());
            return mergedUser;
        } catch (Exception myException) {
            if (myTransaction != null) myTransaction.rollback();
            myLoggerInstance.error("Failed to update user: {}", myException.getMessage());
            throw new DataAccessCustomException("Failed to update user", myException);
        } finally {
            mySession.close();
        }
    }
    
    @Override
    public boolean removeUserById(Long userIdValue) {
        Transaction myTransaction = null;
        Session mySession = DatabaseConfigurationManager.getDatabaseSession();
        try {
            myTransaction = mySession.beginTransaction();
            UserEntity userToDelete = mySession.find(UserEntity.class, userIdValue);
            if (userToDelete != null) {
                mySession.delete(userToDelete);
                myTransaction.commit();
                myLoggerInstance.info("Deleted user with ID: {}", userIdValue);
                return true;
            }
            return false;
        } catch (Exception myException) {
            if (myTransaction != null) myTransaction.rollback();
            myLoggerInstance.error("Failed to delete user with ID {}: {}", userIdValue, myException.getMessage());
            throw new DataAccessCustomException("Failed to delete user", myException);
        } finally {
            mySession.close();
        }
    }
    
    @Override
    public Optional<UserEntity> findUserByEmailAddress(String emailAddress) {
        Session mySession = DatabaseConfigurationManager.getDatabaseSession();
        try {
            TypedQuery<UserEntity> myQuery = mySession.createNamedQuery("UserEntity.findUserByEmail", UserEntity.class);
            myQuery.setParameter("userEmail", emailAddress);
            return myQuery.getResultStream().findFirst();
        } catch (Exception myException) {
            myLoggerInstance.error("Failed to find user by email {}: {}", emailAddress, myException.getMessage());
            throw new DataAccessCustomException("Failed to find user by email", myException);
        } finally {
            mySession.close();
        }
    }
    
    @Override
    public boolean checkIfEmailExists(String emailAddress) {
        return findUserByEmailAddress(emailAddress).isPresent();
    }
}
