package com.example;

import com.example.entity.UserEntity;
import com.example.service.UserManagementServiceImpl;
import com.example.config.DatabaseConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

public class ApplicationMain {
    private static final Logger myLoggerInstance = LoggerFactory.getLogger(ApplicationMain.class);
    private final UserManagementServiceImpl myUserServiceInstance;
    private final Scanner myScannerInstance;
    
    public ApplicationMain() {
        this.myUserServiceInstance = new UserManagementServiceImpl();
        this.myScannerInstance = new Scanner(System.in);
    }
    
    public void executeApplication() {
        myLoggerInstance.info("Starting User Management Application");
        displayWelcomeMessage();
        
        boolean isApplicationRunning = true;
        while (isApplicationRunning) {
            displayMainMenu();
            String userInputValue = myScannerInstance.nextLine().trim();
            
            switch (userInputValue) {
                case "1" -> addNewUserOperation();
                case "2" -> viewUserDetailsOperation();
                case "3" -> listAllUsersOperation();
                case "4" -> editUserOperation();
                case "5" -> deleteUserOperation();
                case "6" -> searchUserByEmailOperation();
                case "0" -> isApplicationRunning = false;
                default -> System.out.println("Неверная опция. Пожалуйста, попробуйте снова.");
            }
            
            if (isApplicationRunning) {
                System.out.println("\nНажмите Enter для продолжения...");
                myScannerInstance.nextLine();
            }
        }
        
        shutdownApplication();
    }
    
    private void displayWelcomeMessage() {
        System.out.println("====================================");
        System.out.println("    СИСТЕМА УПРАВЛЕНИЯ ПОЛЬЗОВАТЕЛЯМИ");
        System.out.println("====================================\n");
    }
    
    private void displayMainMenu() {
        System.out.println("\n--- Главное меню ---");
        System.out.println("1. Добавить нового пользователя");
        System.out.println("2. Просмотреть данные пользователя");
        System.out.println("3. Список всех пользователей");
        System.out.println("4. Редактировать пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("6. Найти пользователя по Email");
        System.out.println("0. Выход");
        System.out.print("Выберите опцию: ");
    }
    
    private void addNewUserOperation() {
        System.out.println("\n--- Добавление нового пользователя ---");
        try {
            System.out.print("Введите полное имя: ");
            String userNameInput = myScannerInstance.nextLine().trim();
            
            System.out.print("Введите email: ");
            String userEmailInput = myScannerInstance.nextLine().trim();
            
            System.out.print("Введите возраст: ");
            int userAgeInput = Integer.parseInt(myScannerInstance.nextLine().trim());
            
            UserEntity newUser = myUserServiceInstance.registerNewUser(userNameInput, userEmailInput, userAgeInput);
            System.out.println("Пользователь успешно зарегистрирован!");
            System.out.println("ID пользователя: " + newUser.getUserId());
        } catch (Exception myException) {
            System.out.println("Ошибка: " + myException.getMessage());
            myLoggerInstance.warn("Не удалось добавить пользователя: {}", myException.getMessage());
        }
    }
    
    private void viewUserDetailsOperation() {
        System.out.println("\n--- Просмотр данных пользователя ---");
        try {
            System.out.print("Введите ID пользователя: ");
            Long userIdInput = Long.parseLong(myScannerInstance.nextLine().trim());
            
            myUserServiceInstance.getUserByIdValue(userIdInput).ifPresentOrElse(
                user -> {
                    System.out.println("\nДанные пользователя:");
                    System.out.println("ID: " + user.getUserId());
                    System.out.println("Имя: " + user.getUserName());
                    System.out.println("Email: " + user.getUserEmail());
                    System.out.println("Возраст: " + user.getUserAge());
                    System.out.println("Зарегистрирован: " + user.getUserCreatedAt());
                },
                () -> System.out.println("Пользователь не найден с ID: " + userIdInput)
            );
        } catch (Exception myException) {
            System.out.println("Ошибка: " + myException.getMessage());
        }
    }
    
    private void listAllUsersOperation() {
        System.out.println("\n--- Все зарегистрированные пользователи ---");
        try {
            List<UserEntity> usersList = myUserServiceInstance.getAllUsersList();
            if (usersList.isEmpty()) {
                System.out.println("Пользователи не найдены.");
            } else {
                System.out.printf("%-5s %-20s %-25s %-5s %-20s%n", 
                    "ID", "Имя", "Email", "Возраст", "Дата регистрации");
                System.out.println("-".repeat(80));
                for (UserEntity user : usersList) {
                    System.out.printf("%-5d %-20s %-25s %-5d %-20s%n",
                        user.getUserId(),
                        user.getUserName().length() > 18 ? user.getUserName().substring(0, 15) + "..." : user.getUserName(),
                        user.getUserEmail().length() > 23 ? user.getUserEmail().substring(0, 20) + "..." : user.getUserEmail(),
                        user.getUserAge(),
                        user.getUserCreatedAt().toLocalDate()
                    );
                }
                System.out.println("Всего пользователей: " + usersList.size());
            }
        } catch (Exception myException) {
            System.out.println("Ошибка при получении пользователей: " + myException.getMessage());
            myLoggerInstance.error("Не удалось получить список пользователей: {}", myException.getMessage());
        }
    }
    
    private void editUserOperation() {
        System.out.println("\n--- Редактирование пользователя ---");
        try {
            System.out.print("Введите ID пользователя для редактирования: ");
            Long userIdInput = Long.parseLong(myScannerInstance.nextLine().trim());
            
            System.out.print("Введите новое имя (нажмите Enter чтобы оставить текущее): ");
            String userNameInput = myScannerInstance.nextLine().trim();
            
            System.out.print("Введите новый email (нажмите Enter чтобы оставить текущий): ");
            String userEmailInput = myScannerInstance.nextLine().trim();
            
            System.out.print("Введите новый возраст (нажмите Enter чтобы оставить текущий): ");
            String userAgeInputString = myScannerInstance.nextLine().trim();
            Integer userAgeInput = userAgeInputString.isEmpty() ? null : Integer.parseInt(userAgeInputString);
            
            UserEntity updatedUser = myUserServiceInstance.modifyUserData(userIdInput, 
                userNameInput.isEmpty() ? null : userNameInput, 
                userEmailInput.isEmpty() ? null : userEmailInput, 
                userAgeInput);
            
            System.out.println("Пользователь успешно обновлен!");
        } catch (Exception myException) {
            System.out.println("Ошибка: " + myException.getMessage());
        }
    }
    
    private void deleteUserOperation() {
        System.out.println("\n--- Удаление пользователя ---");
        try {
            System.out.print("Введите ID пользователя для удаления: ");
            Long userIdInput = Long.parseLong(myScannerInstance.nextLine().trim());
            
            System.out.print("Вы уверены? (yes/no): ");
            String confirmationInput = myScannerInstance.nextLine().trim();
            
            if ("yes".equalsIgnoreCase(confirmationInput)) {
                boolean isDeleted = myUserServiceInstance.deleteUserById(userIdInput);
                if (isDeleted) {
                    System.out.println("Пользователь успешно удален!");
                } else {
                    System.out.println("Пользователь не найден с ID: " + userIdInput);
                }
            } else {
                System.out.println("Удаление отменено.");
            }
        } catch (Exception myException) {
            System.out.println("Ошибка: " + myException.getMessage());
        }
    }
    
    private void searchUserByEmailOperation() {
        System.out.println("\n--- Поиск пользователя по Email ---");
        try {
            System.out.print("Введите email адрес: ");
            String emailInput = myScannerInstance.nextLine().trim();
            
            myUserServiceInstance.findUserByEmailString(emailInput).ifPresentOrElse(
                user -> {
                    System.out.println("\nПользователь найден:");
                    System.out.println("ID: " + user.getUserId());
                    System.out.println("Имя: " + user.getUserName());
                    System.out.println("Email: " + user.getUserEmail());
                    System.out.println("Возраст: " + user.getUserAge());
                },
                () -> System.out.println("Пользователь не найден с email: " + emailInput)
            );
        } catch (Exception myException) {
            System.out.println("Ошибка: " + myException.getMessage());
        }
    }
    
    private void shutdownApplication() {
        System.out.println("\nЗавершение работы приложения...");
        DatabaseConfigurationManager.shutdownDatabase();
        myScannerInstance.close();
        myLoggerInstance.info("Application shutdown completed");
        System.out.println("До свидания!");
    }
    
    public static void main(String[] args) {
        ApplicationMain myApplication = new ApplicationMain();
        try {
            myApplication.executeApplication();
        } catch (Exception myException) {
            myLoggerInstance.error("Application error: {}", myException.getMessage(), myException);
            System.out.println("Критическая ошибка: " + myException.getMessage());
        }
    }
}
