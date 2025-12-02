package com.example.demo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * REAL-WORLD EXAMPLE: When to use @BeforeAll
 * 
 * Scenario: Testing a service that needs expensive setup
 * - Database connection (expensive, should be done once)
 * - Loading configuration from file (expensive, same for all tests)
 * - Initializing test data (same data for all tests)
 */
class RealWorldBeforeAllExample {

    // These represent expensive resources that should be initialized once
    private static Map<String, String> databaseConnection;
    private static Map<String, Object> testConfiguration;
    private static Map<Integer, String> testUsers;

    // This represents something that might change per test
    private String currentSessionId;

    /**
     * @BeforeAll: Perfect for expensive operations that don't change between tests
     * 
     * In real applications, this might:
     * - Connect to a database (takes time, same connection for all tests)
     * - Load configuration from file (same config for all tests)
     * - Initialize test data (same users/products for all tests)
     * - Start embedded servers (same server for all tests)
     */
    @BeforeAll
    static void initializeExpensiveResources() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("🚀 @BeforeAll: Initializing expensive resources...");
        System.out.println("═══════════════════════════════════════════════════");
        
        // 1. Simulate database connection (expensive operation)
        System.out.println("   📊 Connecting to database...");
        databaseConnection = new HashMap<>();
        databaseConnection.put("host", "localhost");
        databaseConnection.put("port", "5432");
        databaseConnection.put("database", "test_db");
        databaseConnection.put("status", "connected");
        // In real code: databaseConnection = DatabaseManager.connect();
        System.out.println("   ✅ Database connected!");
        
        // 2. Simulate loading configuration (expensive file I/O)
        System.out.println("   📄 Loading configuration file...");
        testConfiguration = new HashMap<>();
        testConfiguration.put("apiUrl", "https://api.example.com");
        testConfiguration.put("timeout", 5000);
        testConfiguration.put("maxRetries", 3);
        // In real code: testConfiguration = ConfigLoader.load("test-config.properties");
        System.out.println("   ✅ Configuration loaded!");
        
        // 3. Simulate loading test data (same data for all tests)
        System.out.println("   👥 Loading test users...");
        testUsers = new HashMap<>();
        testUsers.put(1, "Alice");
        testUsers.put(2, "Bob");
        testUsers.put(3, "Charlie");
        // In real code: testUsers = TestDataLoader.loadUsers();
        System.out.println("   ✅ Test users loaded: " + testUsers.size() + " users");
        
        System.out.println("═══════════════════════════════════════════════════\n");
    }

    /**
     * @BeforeEach: For things that should be fresh for each test
     */
    @BeforeEach
    void setUpForEachTest() {
        // Generate a new session ID for each test
        currentSessionId = "session-" + System.currentTimeMillis();
    }

    @Test
    void test1_UserLookup() {
        System.out.println("Test 1: Looking up user by ID");
        
        // Use the shared database connection (initialized once in @BeforeAll)
        assertEquals("connected", databaseConnection.get("status"));
        
        // Use the shared test users (loaded once in @BeforeAll)
        assertEquals("Alice", testUsers.get(1));
        assertEquals("Bob", testUsers.get(2));
        
        // Use the shared configuration (loaded once in @BeforeAll)
        assertEquals("https://api.example.com", testConfiguration.get("apiUrl"));
        
        System.out.println("   ✅ Test passed using shared resources\n");
    }

    @Test
    void test2_UserAuthentication() {
        System.out.println("Test 2: Authenticating user");
        
        // Same database connection (reused, not recreated!)
        assertEquals("connected", databaseConnection.get("status"));
        
        // Same test users (reused, not reloaded!)
        assertTrue(testUsers.containsKey(1));
        assertTrue(testUsers.containsKey(2));
        
        // Fresh session ID (created in @BeforeEach)
        assertNotNull(currentSessionId);
        assertTrue(currentSessionId.startsWith("session-"));
        
        System.out.println("   ✅ Test passed using shared resources\n");
    }

    @Test
    void test3_UserProfileUpdate() {
        System.out.println("Test 3: Updating user profile");
        
        // Still using the same database connection
        assertEquals("connected", databaseConnection.get("status"));
        
        // Still using the same test users
        assertEquals("Charlie", testUsers.get(3));
        
        // New session ID (fresh for this test)
        assertNotNull(currentSessionId);
        
        System.out.println("   ✅ Test passed using shared resources\n");
    }

    /**
     * KEY TAKEAWAY:
     * 
     * ✅ Use @BeforeAll when:
     *    - Setup is expensive (database, file I/O, network)
     *    - Setup is the same for all tests
     *    - You want to optimize test execution time
     * 
     * ❌ Don't use @BeforeAll when:
     *    - Setup needs to be fresh for each test
     *    - Tests modify shared state (can cause test interference)
     *    - Setup is quick and simple
     * 
     * 💡 Remember: @BeforeAll runs ONCE, @BeforeEach runs before EACH test
     */
}

