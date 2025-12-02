package com.example.demo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class demonstrates the usage of @BeforeAll annotation in JUnit 5.
 * 
 * Key Points:
 * - @BeforeAll runs ONCE before ALL test methods in the class
 * - The method must be static (unless using @TestInstance(Lifecycle.PER_CLASS))
 * - Perfect for expensive setup operations like database connections, file I/O, etc.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // This allows @BeforeAll to be non-static
class BeforeAllExampleTest {

    // Shared resources that will be initialized once
    private static List<String> sharedData;
    private static int expensiveResource;
    private String instanceVariable;

    /**
     * @BeforeAll - Runs ONCE before ALL test methods
     * 
     * Common use cases:
     * - Database connection setup
     * - Loading configuration files
     * - Initializing expensive resources
     * - Setting up test data that all tests need
     * 
     * Note: With @TestInstance(Lifecycle.PER_CLASS), this can be non-static
     */
    @BeforeAll
    void setUpOnce() {
        System.out.println("=== @BeforeAll: This runs ONCE before all tests ===");
        
        // Initialize shared data structure
        sharedData = new ArrayList<>();
        sharedData.add("Initial Data");
        sharedData.add("Common Data");
        
        // Simulate expensive operation (e.g., database connection, file reading)
        expensiveResource = initializeExpensiveResource();
        
        System.out.println("Shared data initialized: " + sharedData);
        System.out.println("Expensive resource initialized: " + expensiveResource);
        System.out.println();
    }

    /**
     * @BeforeEach - Runs before EACH test method
     * This is here to show the difference from @BeforeAll
     */
    @BeforeEach
    void setUpEach() {
        System.out.println("--- @BeforeEach: This runs before EACH test ---");
        instanceVariable = "Test Instance";
    }

    /**
     * Test 1: Uses the shared data initialized in @BeforeAll
     */
    @Test
    void test1_UsingSharedData() {
        System.out.println("Running test1_UsingSharedData");
        
        // All tests can use the same sharedData initialized in @BeforeAll
        assertNotNull(sharedData);
        assertEquals(2, sharedData.size());
        assertTrue(sharedData.contains("Initial Data"));
        
        // Modify the shared data
        sharedData.add("Test1 Data");
        assertEquals(3, sharedData.size());
        
        System.out.println("Shared data after test1: " + sharedData);
        System.out.println();
    }

    /**
     * Test 2: Also uses the same shared data
     * Notice that sharedData still has the modification from test1!
     */
    @Test
    void test2_UsingSharedDataAgain() {
        System.out.println("Running test2_UsingSharedDataAgain");
        
        // This test sees the modification from test1 because @BeforeAll only runs once
        assertNotNull(sharedData);
        assertEquals(3, sharedData.size()); // Still has "Test1 Data" from previous test!
        assertTrue(sharedData.contains("Test1 Data"));
        
        // Add more data
        sharedData.add("Test2 Data");
        assertEquals(4, sharedData.size());
        
        System.out.println("Shared data after test2: " + sharedData);
        System.out.println();
    }

    /**
     * Test 3: Uses the expensive resource initialized once
     */
    @Test
    void test3_UsingExpensiveResource() {
        System.out.println("Running test3_UsingExpensiveResource");
        
        // All tests can use the same expensive resource
        assertNotNull(expensiveResource);
        assertTrue(expensiveResource > 0);
        
        System.out.println("Using expensive resource: " + expensiveResource);
        System.out.println();
    }

    /**
     * Test 4: Demonstrates that @BeforeEach runs before each test
     */
    @Test
    void test4_BeforeEachExample() {
        System.out.println("Running test4_BeforeEachExample");
        
        // @BeforeEach runs before each test, so instanceVariable is fresh
        assertEquals("Test Instance", instanceVariable);
        
        // Modify it
        instanceVariable = "Modified";
        assertEquals("Modified", instanceVariable);
        
        System.out.println("Instance variable: " + instanceVariable);
        System.out.println();
    }

    /**
     * Simulates an expensive initialization operation
     * This would typically be something like:
     * - Opening a database connection
     * - Reading a large configuration file
     * - Initializing external services
     */
    private int initializeExpensiveResource() {
        System.out.println("Initializing expensive resource (simulating slow operation)...");
        // Simulate some time-consuming operation
        try {
            Thread.sleep(100); // Simulate delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return 42; // Return some initialized value
    }
}

