package com.tritonkor.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tritonkor.persistence.entity.User;
import com.tritonkor.persistence.entity.User.Role;
import com.tritonkor.persistence.exception.EntityUpdateException;
import com.tritonkor.persistence.repository.contract.UserRepository;
import com.tritonkor.persistence.repository.impl.jdbc.UserRepositoryImpl;
import com.tritonkor.persistence.util.ConnectionManager;
import com.tritonkor.persistence.util.PropertyManager;
import com.tritonkor.persistence.init.FakeDatabaseInitializer;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


class UserRepositoryTest {
    private static ConnectionManager connectionManager;
    private static UserRepository userRepository;
    private static byte[] imageBytes;

    private static ApplicationContext context;

    @BeforeAll
    static void setup() {
        PropertyManager propertyManager = new PropertyManager(
            UserRepositoryTest.class.getClassLoader()
                .getResourceAsStream("application.properties")
        );
        context = new AnnotationConfigApplicationContext(AppConfig.class);

        connectionManager = new ConnectionManager(propertyManager);
        userRepository = context.getBean(UserRepositoryImpl.class);

        String hexString = "0x089504E470D0A1A0A0000000D4948445200000030000000300806000000F9B78C0000000970485973000000EC400000EC40195F36F000000017352474200AECE1CE90000000467414D410000B18F0BFC6105000000097048597300000EC400000EC40195F36F000000017352474200000000527443436F6C6F7253706163652E6465660000000049454E44AE426082";

        imageBytes = hexStringToByteArray(hexString);
    }

    @BeforeEach
    void init() throws SQLException {
        FakeDatabaseInitializer.run(connectionManager.get());
    }

    @Test
    void findOneById_whenUserExists_thenReturnsUser() {
        // Given
        UUID userId = UUID.fromString("5c2a6d9b-8a32-4366-ad16-ca14f387c564");
        User expectedUser = User.builder().id(userId)
                .username("user1")
                .email("user1@example.com")
                .password("password1")
                .avatar(imageBytes)
                .birthday(LocalDate.of(1990, 1, 1))
                .role(Role.STUDENT)
                .build();


        // When
        Optional<User> actualOptionalUser = userRepository.findById(userId);

        // Then
        assertTrue(actualOptionalUser.isPresent(), "The found User object is not null");
        assertEquals(expectedUser, actualOptionalUser.get(), "The searched object is equal to the found one");
    }

    @Test
    void findOneById_whenUserDoesNotExist_thenReturnsEmptyOptional() {
        // Given
        UUID id = UUID.randomUUID();

        // When
        Optional<User> actualOptionalUser = userRepository.findById(id);

        // Then
        assertTrue(actualOptionalUser.isEmpty(), "Empty optional if the address with this id does not exist");
    }

    @Test
    @Tag("slow")
    void findAll_thenReturnsSetOfUser() {
        // Given
        int usersSize = 3;

        // When
        Set<User> users = userRepository.findAll();

        // Then
        assertNotNull(users);
        assertEquals(usersSize, users.size());
    }

    @Test
    void save_whenInsertNewUser_thenReturnsAddressEntityWithGeneratedId() {
        // Given
        User expectedUser = User.builder().id(null)
                .username("arakviel")
                .email("arakviel@gmail.com")
                .password("password")
                .avatar(imageBytes)
                .birthday(LocalDate.of(1998, 2, 25))
                .role(Role.TEACHER)
                .build();

        // When
        User actualUser = userRepository.save(expectedUser);
        UUID id = actualUser.getId();
        Optional<User> optionalFoundedUser = userRepository.findById(id);

        // Then
        assertNotNull(id);
        assertTrue(optionalFoundedUser.isPresent());
        assertEquals(actualUser, optionalFoundedUser.orElse(null));
    }

    @Test
    void save_whenUpdateExistUser_thenReturnsUser() {
        // Given
        UUID userId = UUID.fromString("5c2a6d9b-8a32-4366-ad16-ca14f387c564");
        User expectedUser = User.builder().id(userId)
                .username("arakviel")
                .email("arakviel@gmail.com")
                .password("password")
                .avatar(imageBytes)
                .birthday(LocalDate.of(1998, 2, 25))
                .role(Role.TEACHER)
                .build();

        // When
        userRepository.save(expectedUser);
        var optionalUser = userRepository.findById(userId);


        // Then
        assertEquals(expectedUser, optionalUser.orElse(null));
    }

    @Test
    void save_whenUpdateNotExistUser_thenThrowEntityUpdateException() {
        // Given
        UUID userId = UUID.randomUUID();
        User expectedUser = User.builder().id(userId)
                .username("arakviel")
                .email("arakviel@gmail.com")
                .password("password")
                .avatar(imageBytes)
                .birthday(LocalDate.of(1998, 2, 25))
                .role(Role.TEACHER)
                .build();

        // When
        Executable executable = () -> {
            userRepository.save(expectedUser);
            var optionalUser = userRepository.findById(userId);
        };

        // Then
        assertThrows(EntityUpdateException.class, executable);
    }

    @AfterAll
    static void tearDown() throws SQLException {
        connectionManager.closePool();
    }

    private static byte[] hexStringToByteArray(String hexString) {
        if (hexString.startsWith("0x")) {
            hexString = hexString.substring(2);
        }

        // Ensure the hex string length is even
        if (hexString.length() % 2 != 0) {
            hexString = "0" + hexString;
        }

        int len = hexString.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }

        return data;
    }
}
