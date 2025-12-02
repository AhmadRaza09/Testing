package com.example.demo;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SIMPLE EXAMPLE: @BeforeAll with static method (most common approach)
 * 
 * This is the traditional and most common way to use @BeforeAll.
 * The method MUST be static when using the default test instance lifecycle.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SimpleBeforeAllExample {

    // Static variables that will be shared across all tests
    private static String databaseConnection;
    private static int testCounter;
    
    // Instance variable (resets for each test)
    private String currentTestName;

    /**
     * @BeforeAll - MUST be static in default JUnit lifecycle
     * 
     * This method runs ONCE before ALL test methods execute.
     * Perfect for:
     * - Setting up database connections
     * - Loading configuration
     * - Initializing shared resources
     */
    @BeforeAll
    static void setUpOnce() {
        System.out.println("\n🔵 @BeforeAll: Setting up once for all tests...");
        
        // Simulate database connection setup
        databaseConnection = "Connected to Test Database";
        testCounter = 0;
        
        System.out.println("✅ Database connection established: " + databaseConnection);
        System.out.println("✅ Test counter initialized: " + testCounter);
    }

    /**
     * @BeforeEach - Runs before EACH test
     * This is NOT static, so it can access instance variables
     */
    @BeforeEach
    void setUpEach() {
        testCounter++; // Increment counter for each test
        System.out.println("\n🟢 @BeforeEach: Preparing for test #" + testCounter);
    }

    @Order(1)
    @Test
    void test1_FirstTest() {
        currentTestName = "First Test";
        System.out.println("   Running: " + currentTestName);
        
        // Use the shared database connection
        assertNotNull(databaseConnection);
        assertEquals("Connected to Test Database", databaseConnection);
        
        // Check counter
        assertEquals(1, testCounter);
    }

    @Order(2)
    @Test
    void test2_SecondTest() {
        currentTestName = "Second Test";
        System.out.println("   Running: " + currentTestName);
        
        // Same database connection (reused from @BeforeAll)
        assertNotNull(databaseConnection);
        assertEquals("Connected to Test Database", databaseConnection);
        
        // Counter increased (because @BeforeEach ran again)
        assertEquals(2, testCounter);
    }

    @Order(3)
    @Test
    void test3_ThirdTest() {
        currentTestName = "Third Test";
        System.out.println("   Running: " + currentTestName);
        
        // Still using the same database connection
        assertNotNull(databaseConnection);
        
        // Counter increased again
        assertEquals(3, testCounter);
    }
}

