package com.tritonkor.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tritonkor.persistence.entity.proxy.contract.Questions;
import com.tritonkor.persistence.entity.proxy.contract.UserProxy;
import com.tritonkor.persistence.entity.proxy.impl.QuestionsProxy;
import com.tritonkor.persistence.entity.proxy.impl.UserProxyImpl;
import com.tritonkor.persistence.exception.EntityNotFoundException;
import com.tritonkor.persistence.exception.EntityUpdateException;
import com.tritonkor.persistence.init.FakeDatabaseInitializer;
import com.tritonkor.persistence.repository.contract.TestRepository;
import com.tritonkor.persistence.repository.contract.UserRepository;
import com.tritonkor.persistence.repository.impl.jdbc.TestRepositoryImpl;
import com.tritonkor.persistence.repository.impl.jdbc.UserRepositoryImpl;
import com.tritonkor.persistence.util.ConnectionManager;
import com.tritonkor.persistence.util.PropertyManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import com.tritonkor.persistence.entity.Test;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.function.Executable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestRepositoryTest {

    private static ConnectionManager connectionManager;
    private static TestRepository testRepository;

    private static ApplicationContext context;

    private static Questions questions;
    private static UserProxy userProxy;

    @BeforeAll
    static void setup() {
        PropertyManager propertyManager = new PropertyManager(
                TestRepository.class.getClassLoader()
                        .getResourceAsStream("application.properties")
        );

        context = new AnnotationConfigApplicationContext(PersistenceConfig.class);

        connectionManager = new ConnectionManager(propertyManager);
        testRepository = context.getBean(TestRepositoryImpl.class);

        questions = context.getBean(QuestionsProxy.class);
        userProxy = context.getBean(UserProxyImpl.class);
    }

    @BeforeEach
    void init() throws SQLException {
        FakeDatabaseInitializer.run(connectionManager.get());
    }

    @org.junit.jupiter.api.Test
    void findOneById_whenTestExists_thenReturnsTest() {

        // Given
        UUID testId = UUID.fromString("3552c844-e5dc-4135-a4ff-b0bb148ee2af");
        UUID ownerId = UUID.fromString("acf647ec-b69c-43cc-86fd-96628a1fb40a");

        Test expectedTest = Test.builder()
                .id(testId)
                .title("Test 1")
                .owner_id(ownerId)
                .owner(userProxy)
                .questions(questions)
                .tags(tId -> Collections.unmodifiableSet(testRepository.findAllTags(tId)))
                .createdAt(LocalDateTime.of(1990, 1, 1, 10, 0, 0))
                .build();

        // When
        Optional<Test> actualOptionalTest = testRepository.findById(testId);

        // Then
        assertTrue(actualOptionalTest.isPresent(), "The found Test object is not null");
        assertEquals(expectedTest, actualOptionalTest.get(),
                "The searched object is equal to the found one");
    }

    @org.junit.jupiter.api.Test
    void findOneById_whenTestDoesNotExist_thenReturnsEmptyOptional() {
        // Given
        UUID id = UUID.randomUUID();

        // When
        Optional<Test> actualOptionalTest = testRepository.findById(id);

        // Then
        assertTrue(actualOptionalTest.isEmpty(),
                "Empty optional if the address with this id does not exist");
    }

    @org.junit.jupiter.api.Test
    @Tag("slow")
    void findAll_thenReturnsSetOfUser() {
        // Given
        int testsSize = 2;

        // When
        Set<Test> tests = testRepository.findAll();

        // Then
        assertNotNull(tests);
        assertEquals(testsSize, tests.size());
    }

    @org.junit.jupiter.api.Test
    void save_whenInsertNewTest_thenReturnsAddressEntityWithGeneratedId() {
        UUID ownerId = UUID.fromString("acf647ec-b69c-43cc-86fd-96628a1fb40a");

        // Given
        Test expectedTest = Test.builder()
                .id(null)
                .title("Test 3")
                .owner_id(ownerId)
                .owner(userProxy)
                .questions(questions)
                .tags(tId -> Collections.unmodifiableSet(testRepository.findAllTags(tId)))
                .createdAt(LocalDateTime.of(1990, 1, 1, 10, 0, 0))
                .build();

        // When
        Test actualTest = testRepository.save(expectedTest);
        UUID id = actualTest.getId();
        Optional<Test> optionalFoundedTest = testRepository.findById(id);

        // Then
        assertNotNull(id);
        assertTrue(optionalFoundedTest.isPresent());
        assertEquals(actualTest, optionalFoundedTest.orElse(null));
    }

    @org.junit.jupiter.api.Test
    void save_whenUpdateExistTest_thenReturnsTest() {
        // Given
        UUID testId = UUID.fromString("3552c844-e5dc-4135-a4ff-b0bb148ee2af");
        UUID ownerId = UUID.fromString("acf647ec-b69c-43cc-86fd-96628a1fb40a");

        Test expectedTest = Test.builder()
                .id(testId)
                .title("Test 1")
                .owner_id(ownerId)
                .owner(userProxy)
                .questions(questions)
                .tags(tId -> Collections.unmodifiableSet(testRepository.findAllTags(tId)))
                .createdAt(LocalDateTime.of(1990, 1, 1, 10, 0, 0))
                .build();

        // When
        testRepository.save(expectedTest);
        var optionalTest = testRepository.findById(testId);

        // Then
        assertEquals(expectedTest, optionalTest.orElse(null));
    }

    @org.junit.jupiter.api.Test
    void save_whenUpdateNotExistTest_thenThrowEntityUpdateException() {
        // Given
        UUID testId = UUID.randomUUID();
        UUID ownerId = UUID.fromString("acf647ec-b69c-43cc-86fd-96628a1fb40a");

        Test expectedTest = Test.builder()
                .id(testId)
                .title("Test 1")
                .owner_id(ownerId)
                .owner(userProxy)
                .questions(questions)
                .tags(tId -> Collections.unmodifiableSet(testRepository.findAllTags(tId)))
                .createdAt(LocalDateTime.of(1990, 1, 1, 10, 0, 0))
                .build();

        // When
        Executable executable = () -> {
            testRepository.save(expectedTest);
            var optionalTest = testRepository.findById(testId);
        };

        // Then
        assertThrows(EntityUpdateException.class, executable);
    }

    @AfterAll
    static void tearDown() throws SQLException {
        connectionManager.closePool();
    }
}
