package com.example.demo;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class demonstrates the usage of @AfterEach and @AfterAll annotations in JUnit 5.
 * 
 * Key Points:
 * - @AfterEach runs AFTER EACH test method completes (whether it passes or fails)
 * - @AfterAll runs ONCE after ALL test methods in the class have completed
 * - Perfect for cleanup operations like closing connections, deleting temp files, etc.
 * 
 * Execution Order:
 * @BeforeAll → @BeforeEach → @Test → @AfterEach → @BeforeEach → @Test → @AfterEach → ... → @AfterAll
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Allows @BeforeAll/@AfterAll to be non-static
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Enable test ordering
class AfterEachAfterAllExampleTest {

    // Shared resources
    private static List<String> testResults;
    private static List<File> tempFiles;
    private static int testCounter;
    private String currentTestName;
    private File currentTempFile;

    /**
     * @BeforeAll - Runs ONCE before ALL test methods
     * Initialize shared resources that all tests will use
     */
    @BeforeAll
    void setUpOnce() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  @BeforeAll: Initializing test suite (runs ONCE)          ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        
        testResults = new ArrayList<>();
        tempFiles = new ArrayList<>();
        testCounter = 0;
        
        System.out.println("✓ Shared resources initialized");
        System.out.println("✓ Test counter reset to 0\n");
    }

    /**
     * @BeforeEach - Runs before EACH test method
     * Set up test-specific resources
     */
    @BeforeEach
    void setUpEach() {
        testCounter++;
        System.out.println("┌────────────────────────────────────────────────────────────┐");
        System.out.println("│  @BeforeEach: Setting up for test #" + testCounter + " (runs before EACH test)  │");
        System.out.println("└────────────────────────────────────────────────────────────┘");
        
        // Create a temporary file for this test
        try {
            currentTempFile = File.createTempFile("test_" + testCounter + "_", ".tmp");
            tempFiles.add(currentTempFile);
            System.out.println("✓ Created temp file: " + currentTempFile.getName());
        } catch (IOException e) {
            fail("Failed to create temp file: " + e.getMessage());
        }
        
        currentTestName = "Test_" + testCounter;
        System.out.println("✓ Test name set: " + currentTestName + "\n");
    }

    /**
     * @AfterEach - Runs AFTER EACH test method
     * 
     * Key Characteristics:
     * - Runs after EVERY test method, regardless of whether it passes or fails
     * - Perfect for cleaning up test-specific resources
     * - Use for: closing file handles, clearing test data, resetting state
     * 
     * Common Use Cases:
     * - Delete temporary files created during the test
     * - Close database connections opened in the test
     * - Clear caches or reset mock objects
     * - Log test completion status
     */
    @AfterEach
    void tearDownEach() {
        System.out.println("┌────────────────────────────────────────────────────────────┐");
        System.out.println("│  @AfterEach: Cleaning up after test #" + testCounter + " (runs after EACH test) │");
        System.out.println("└────────────────────────────────────────────────────────────┘");
        
        // Clean up the temporary file created in @BeforeEach
        if (currentTempFile != null && currentTempFile.exists()) {
            boolean deleted = currentTempFile.delete();
            if (deleted) {
                System.out.println("✓ Deleted temp file: " + currentTempFile.getName());
            } else {
                System.out.println("⚠ Warning: Could not delete temp file: " + currentTempFile.getName());
            }
        }
        
        // Record test completion
        testResults.add(currentTestName + " completed");
        System.out.println("✓ Recorded test result: " + currentTestName);
        System.out.println("✓ Total tests completed so far: " + testResults.size() + "\n");
        
        // Reset instance variables for next test
        currentTestName = null;
        currentTempFile = null;
    }

    /**
     * @AfterAll - Runs ONCE after ALL test methods
     * 
     * Key Characteristics:
     * - Runs ONCE after ALL tests in the class have completed
     * - The method must be static (unless using @TestInstance(Lifecycle.PER_CLASS))
     * - Perfect for expensive cleanup operations
     * - Use for: closing database connections, deleting all temp files, generating reports
     * 
     * Common Use Cases:
     * - Close shared database connections
     * - Delete all temporary files/directories
     * - Generate test reports
     * - Clean up external resources
     * - Shut down test servers or services
     */
    @AfterAll
    void tearDownOnce() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  @AfterAll: Final cleanup (runs ONCE after ALL tests)      ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        
        // Clean up any remaining temporary files
        System.out.println("Cleaning up " + tempFiles.size() + " temporary files...");
        int deletedCount = 0;
        for (File file : tempFiles) {
            if (file.exists() && file.delete()) {
                deletedCount++;
            }
        }
        System.out.println("✓ Deleted " + deletedCount + " temporary files");
        
        // Print test summary
        System.out.println("\n📊 Test Summary:");
        System.out.println("   Total tests executed: " + testCounter);
        System.out.println("   Test results recorded: " + testResults.size());
        System.out.println("\n   Test execution order:");
        for (int i = 0; i < testResults.size(); i++) {
            System.out.println("   " + (i + 1) + ". " + testResults.get(i));
        }
        
        // Clean up shared resources
        testResults.clear();
        tempFiles.clear();
        System.out.println("\n✓ All shared resources cleaned up");
        System.out.println("✓ Test suite completed successfully!\n");
    }

    // ============================================================================
    // TEST METHODS - Demonstrating @AfterEach and @AfterAll behavior
    // ============================================================================

    /**
     * Test 1: Demonstrates that @AfterEach runs after this test
     * Notice the temp file created in @BeforeEach will be deleted in @AfterEach
     */
    @Test
    @Order(1)
    void test1_BasicAfterEachExample() {
        System.out.println("  ▶ Running test1_BasicAfterEachExample");
        
        // Verify temp file was created in @BeforeEach
        assertNotNull(currentTempFile);
        assertTrue(currentTempFile.exists(), "Temp file should exist");
        System.out.println("    ✓ Temp file exists: " + currentTempFile.getName());
        
        // Write some data to the temp file
        try (FileWriter writer = new FileWriter(currentTempFile)) {
            writer.write("Test data from test1");
        } catch (IOException e) {
            fail("Failed to write to temp file: " + e.getMessage());
        }
        
        // Verify data was written
        assertTrue(currentTempFile.length() > 0, "File should have content");
        System.out.println("    ✓ Data written to temp file");
        System.out.println("    ✓ Test1 will complete, then @AfterEach will delete the temp file");
    }

    /**
     * Test 2: Another test to show @AfterEach runs after each test
     * This test gets a NEW temp file (created in @BeforeEach)
     */
    @Test
    @Order(2)
    void test2_AnotherAfterEachExample() {
        System.out.println("  ▶ Running test2_AnotherAfterEachExample");
        
        // This is a NEW temp file (different from test1)
        assertNotNull(currentTempFile);
        assertTrue(currentTempFile.exists(), "Temp file should exist");
        System.out.println("    ✓ New temp file created: " + currentTempFile.getName());
        System.out.println("    ✓ This is a different file from test1");
        System.out.println("    ✓ Test2 will complete, then @AfterEach will delete THIS temp file");
    }

    /**
     * Test 3: Demonstrates that @AfterEach runs even if test fails
     * (In a real scenario, you'd want cleanup even on failure)
     */
    @Test
    @Order(3)
    void test3_AfterEachRunsEvenOnFailure() {
        System.out.println("  ▶ Running test3_AfterEachRunsEvenOnFailure");
        
        // Verify temp file exists
        assertNotNull(currentTempFile);
        assertTrue(currentTempFile.exists());
        System.out.println("    ✓ Temp file exists: " + currentTempFile.getName());
        
        // This test will pass, but even if it failed, @AfterEach would still run
        assertTrue(true, "This test passes");
        System.out.println("    ✓ Test passes");
        System.out.println("    ⚠ Note: @AfterEach runs even if test fails!");
    }

    /**
     * Test 4: Demonstrates shared state across tests
     * All tests share the testResults list, which is cleaned up in @AfterAll
     */
    @Test
    @Order(4)
    void test4_SharedStateExample() {
        System.out.println("  ▶ Running test4_SharedStateExample");
        
        // Verify that previous tests have added to testResults
        // (testResults is populated in @AfterEach)
        assertNotNull(testResults);
        System.out.println("    ✓ Shared testResults list exists");
        System.out.println("    ✓ Previous tests have added " + (testResults.size()) + " results");
        System.out.println("    ✓ All results will be cleared in @AfterAll");
    }

    // ============================================================================
    // PRACTICAL EXAMPLE: Database Connection Pattern
    // ============================================================================

    /**
     * This inner class demonstrates a common pattern using @AfterEach and @AfterAll
     * for managing database connections
     */
    static class DatabaseTestExample {
        private static java.sql.Connection sharedConnection; // Shared across all tests
        private java.sql.Statement currentStatement; // Per-test resource

        @BeforeAll
        static void setUpDatabase() {
            System.out.println("@BeforeAll: Opening shared database connection");
            // In real code: sharedConnection = DriverManager.getConnection(...);
            System.out.println("✓ Database connection opened (expensive operation - done once)");
        }

        @BeforeEach
        void setUpStatement() {
            System.out.println("@BeforeEach: Creating statement for this test");
            // In real code: currentStatement = sharedConnection.createStatement();
            System.out.println("✓ Statement created (cheap operation - done per test)");
        }

        @Test
        void testDatabaseQuery1() {
            System.out.println("  Executing query 1...");
            // In real code: currentStatement.executeQuery("SELECT * FROM users");
            System.out.println("  ✓ Query 1 executed");
        }

        @Test
        void testDatabaseQuery2() {
            System.out.println("  Executing query 2...");
            // In real code: currentStatement.executeQuery("SELECT * FROM orders");
            System.out.println("  ✓ Query 2 executed");
        }

        @AfterEach
        void tearDownStatement() {
            System.out.println("@AfterEach: Closing statement (cleanup per test)");
            // In real code: 
            // if (currentStatement != null) currentStatement.close();
            System.out.println("✓ Statement closed");
        }

        @AfterAll
        static void tearDownDatabase() {
            System.out.println("@AfterAll: Closing shared database connection");
            // In real code: 
            // if (sharedConnection != null) sharedConnection.close();
            System.out.println("✓ Database connection closed (expensive cleanup - done once)");
        }
    }

    // ============================================================================
    // EXECUTION ORDER VISUALIZATION
    // ============================================================================

    /**
     * EXECUTION ORDER EXAMPLE:
     * 
     * When you run this test class, the execution order will be:
     * 
     * 1. @BeforeAll setUpOnce()           ← Runs ONCE at the start
     * 
     * 2. @BeforeEach setUpEach()          ← Runs before test1
     * 3. test1_BasicAfterEachExample()    ← Test method
     * 4. @AfterEach tearDownEach()        ← Runs after test1
     * 
     * 5. @BeforeEach setUpEach()          ← Runs before test2
     * 6. test2_AnotherAfterEachExample()  ← Test method
     * 7. @AfterEach tearDownEach()        ← Runs after test2
     * 
     * 8. @BeforeEach setUpEach()          ← Runs before test3
     * 9. test3_AfterEachRunsEvenOnFailure() ← Test method
     * 10. @AfterEach tearDownEach()       ← Runs after test3
     * 
     * 11. @BeforeEach setUpEach()         ← Runs before test4
     * 12. test4_SharedStateExample()      ← Test method
     * 13. @AfterEach tearDownEach()       ← Runs after test4
     * 
     * 14. @AfterAll tearDownOnce()        ← Runs ONCE at the end
     * 
     * KEY TAKEAWAYS:
     * - @AfterEach runs after EVERY test (even if test fails)
     * - @AfterAll runs ONCE after ALL tests complete
     * - Use @AfterEach for per-test cleanup (temp files, statements, etc.)
     * - Use @AfterAll for shared resource cleanup (connections, reports, etc.)
     */
}







