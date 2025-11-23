package com.example.config;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConfigurationManager {
    private static final Logger myLoggerInstance = LoggerFactory.getLogger(DatabaseConfigurationManager.class);
    private static SessionFactory mySessionFactoryInstance;
    
    static {
        initializeMySessionFactory();
    }
    
    private static void initializeMySessionFactory() {
        try {
            StandardServiceRegistry myStandardRegistry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml")
                .build();
            
            Metadata myMetadata = new MetadataSources(myStandardRegistry)
                .addAnnotatedClass(com.example.entity.UserEntity.class)
                .getMetadataBuilder()
                .build();
            
            mySessionFactoryInstance = myMetadata.getSessionFactoryBuilder().build();
            myLoggerInstance.info("Hibernate SessionFactory initialized successfully");
        } catch (Exception myException) {
            myLoggerInstance.error("Hibernate SessionFactory initialization failed: {}", myException.getMessage());
            throw new ExceptionInInitializerError(myException);
        }
    }
    
    public static Session getDatabaseSession() {
        return mySessionFactoryInstance.openSession();
    }
    
    public static void shutdownDatabase() {
        if (mySessionFactoryInstance != null && !mySessionFactoryInstance.isClosed()) {
            mySessionFactoryInstance.close();
            myLoggerInstance.info("Hibernate SessionFactory closed");
        }
    }
    
    public static SessionFactory getMySessionFactory() {
        return mySessionFactoryInstance;
    }
}
