package com.example;

import com.example.entity.UserEntity;
import com.example.service.UserManagementServiceImpl;
import com.example.config.DatabaseConfigurationManager;
import com.example.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
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
                case "1" -> addNewUserOperationWithValidation();
                case "2" -> viewUserDetailsOperation();
                case "3" -> listAllUsersOperation();
                case "4" -> editUserOperationWithValidation();
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
    
    private void addNewUserOperationWithValidation() {
        System.out.println("\n--- Добавление нового пользователя ---");
        
        String userNameInput = null;
        String userEmailInput = null;
        Integer userAgeInput = null;
        
        while (true) {
            System.out.print("Введите полное имя (от 2 до 50 букв, или 'отмена' для выхода): ");
            userNameInput = myScannerInstance.nextLine().trim();
            
            if (userNameInput.equalsIgnoreCase("отмена")) {
                System.out.println("Операция отменена.");
                return;
            }
            
            if (ValidationUtils.isValidName(userNameInput)) {
                break;
            } else {
                System.out.println("Ошибка: имя должно содержать только буквы и быть от 2 до 50 символов");
            }
        }
        
        while (true) {
            System.out.print("Введите email (или 'отмена' для выхода): ");
            userEmailInput = myScannerInstance.nextLine().trim();
            
            if (userEmailInput.equalsIgnoreCase("отмена")) {
                System.out.println("Операция отменена.");
                return;
            }
            
            if (ValidationUtils.isValidEmail(userEmailInput)) {
                break;
            } else {
                System.out.println("Ошибка: неверный формат email");
            }
        }
        
        while (true) {
            System.out.print("Введите возраст (1-120, или 'отмена' для выхода): ");
            String ageStr = myScannerInstance.nextLine().trim();
            
            if (ageStr.equalsIgnoreCase("отмена")) {
                System.out.println("Операция отменена.");
                return;
            }
            
            try {
                userAgeInput = Integer.parseInt(ageStr);
                if (ValidationUtils.isValidAge(userAgeInput)) {
                    break;
                } else {
                    System.out.println("Ошибка: возраст должен быть от 1 до 120");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число");
            }
        }
        
        try {
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
            System.out.print("Введите ID пользователя (или 'отмена' для выхода): ");
            String input = myScannerInstance.nextLine().trim();
            
            if (input.equalsIgnoreCase("отмена")) {
                System.out.println("Операция отменена.");
                return;
            }
            
            Long userIdInput = Long.parseLong(input);
            
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
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ID должен быть числом");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
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
    
    private void editUserOperationWithValidation() {
        System.out.println("\n--- Редактирование пользователя ---");
        
        try {
            System.out.print("Введите ID пользователя для редактирования (или 'отмена' для выхода): ");
            String idInput = myScannerInstance.nextLine().trim();
            
            if (idInput.equalsIgnoreCase("отмена")) {
                System.out.println("Операция отменена.");
                return;
            }
            
            Long userIdInput = Long.parseLong(idInput);
            
            Optional<UserEntity> existingUserOpt = myUserServiceInstance.getUserByIdValue(userIdInput);
            if (existingUserOpt.isEmpty()) {
                System.out.println("Пользователь не найден с ID: " + userIdInput);
                return;
            }
            
            UserEntity existingUser = existingUserOpt.get();
            String newUserName = null;
            String newUserEmail = null;
            Integer newUserAge = null;
            
            while (true) {
                System.out.print("Введите новое имя (от 2 до 50 букв, нажмите Enter чтобы оставить текущее: '" + 
                               existingUser.getUserName() + "', или 'отмена' для выхода): ");
                String userNameInput = myScannerInstance.nextLine().trim();
                
                if (userNameInput.equalsIgnoreCase("отмена")) {
                    System.out.println("Операция отменена.");
                    return;
                }
                
                if (userNameInput.isEmpty()) {
                    System.out.println("Имя не изменено.");
                    break;
                }
                
                if (ValidationUtils.isValidName(userNameInput)) {
                    newUserName = userNameInput;
                    break;
                } else {
                    System.out.println("Ошибка: имя должно содержать только буквы и быть от 2 до 50 символов");
                }
            }
            
            while (true) {
                System.out.print("Введите новый email (нажмите Enter чтобы оставить текущий: '" + 
                               existingUser.getUserEmail() + "', или 'отмена' для выхода): ");
                String userEmailInput = myScannerInstance.nextLine().trim();
                
                if (userEmailInput.equalsIgnoreCase("отмена")) {
                    System.out.println("Операция отменена.");
                    return;
                }
                
                if (userEmailInput.isEmpty()) {
                    System.out.println("Email не изменен.");
                    break;
                }
                
                if (ValidationUtils.isValidEmail(userEmailInput)) {
                    newUserEmail = userEmailInput;
                    break;
                } else {
                    System.out.println("Ошибка: неверный формат email");
                }
            }
            
            while (true) {
                System.out.print("Введите новый возраст (1-120, нажмите Enter чтобы оставить текущий: " + 
                               existingUser.getUserAge() + ", или 'отмена' для выхода): ");
                String userAgeInputString = myScannerInstance.nextLine().trim();
                
                if (userAgeInputString.equalsIgnoreCase("отмена")) {
                    System.out.println("Операция отменена.");
                    return;
                }
                
                if (userAgeInputString.isEmpty()) {
                    System.out.println("Возраст не изменен.");
                    break;
                }
                
                try {
                    int age = Integer.parseInt(userAgeInputString);
                    if (ValidationUtils.isValidAge(age)) {
                        newUserAge = age;
                        break;
                    } else {
                        System.out.println("Ошибка: возраст должен быть от 1 до 120");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка: возраст должен быть числом");
                }
            }
            
            UserEntity updatedUser = myUserServiceInstance.modifyUserData(userIdInput, 
                newUserName, 
                newUserEmail, 
                newUserAge);
            
            System.out.println("Пользователь успешно обновлен!");
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ID должен быть числом");
        } catch (Exception myException) {
            System.out.println("Ошибка: " + myException.getMessage());
        }
    }
    
    private void deleteUserOperation() {
        System.out.println("\n--- Удаление пользователя ---");
        try {
            System.out.print("Введите ID пользователя для удаления (или 'отмена' для выхода): ");
            String input = myScannerInstance.nextLine().trim();
            
            if (input.equalsIgnoreCase("отмена")) {
                System.out.println("Операция отменена.");
                return;
            }
            
            Long userIdInput = Long.parseLong(input);
            
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
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ID должен быть числом");
        } catch (Exception myException) {
            System.out.println("Ошибка: " + myException.getMessage());
        }
    }
    
    private void searchUserByEmailOperation() {
        System.out.println("\n--- Поиск пользователя по Email ---");
        try {
            System.out.print("Введите email адрес (или 'отмена' для выхода): ");
            String emailInput = myScannerInstance.nextLine().trim();
            
            if (emailInput.equalsIgnoreCase("отмена")) {
                System.out.println("Операция отменена.");
                return;
            }
            
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