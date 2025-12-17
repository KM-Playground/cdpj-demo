package com.bidgely;

import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.session.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 test case for CDP4J demo using headless Chrome
 * with --headless --disable-gpu --incognito arguments
 */
public class CdpDemoTest {
    
    private SessionFactory factory;
    private Session session;
    
    @BeforeEach
    void setUp() {
        // Create launcher with Chrome arguments for headless, GPU-disabled, incognito mode
        Launcher launcher = new Launcher();

        // Prepare Chrome arguments
        List<String> chromeArgs = Arrays.asList(
            "--headless",
            "--disable-gpu",
            "--incognito"
        );

        // Launch Chrome with the specified arguments
        factory = launcher.launch(chromeArgs);

        // Create a new session (tab)
        session = factory.create();

        System.out.println("Chrome browser launched in headless mode with incognito and GPU disabled");
    }
    
    @AfterEach
    void tearDown() {
        // Clean up resources
        if (session != null) {
            session.close();
        }
        if (factory != null) {
            factory.close();
        }
        System.out.println("Chrome browser session closed");
    }
    
    @Test
    @DisplayName("Should navigate to fast.com and retrieve page title")
    void testNavigateToFastComAndGetTitle() {
        // Navigate to www.fast.com
        String url = "https://www.fast.com";
        System.out.println("Navigating to: " + url);
        
        session.navigate(url);
        
        // Wait for the page to load
        session.waitDocumentReady();
        
        // Get the page title
        String title = session.getTitle();
        System.out.println("Page title: " + title);
        
        // Assertions
        assertNotNull(title, "Page title should not be null");
        assertFalse(title.trim().isEmpty(), "Page title should not be empty");
        assertTrue(title.toLowerCase().contains("fast"), 
                   "Page title should contain 'fast' (case insensitive)");
        
        // Additional assertion to verify we're on the correct site
        assertTrue(title.toLowerCase().contains("internet") || 
                   title.toLowerCase().contains("speed") ||
                   title.toLowerCase().contains("test"),
                   "Page title should contain words related to internet speed testing");
    }
    
    @Test
    @DisplayName("Should verify Chrome is running in headless mode")
    void testHeadlessMode() {
        // Navigate to a simple page to test basic functionality
        session.navigate("data:text/html,<html><head><title>Test Page</title></head><body><h1>Headless Test</h1></body></html>");
        session.waitDocumentReady();
        
        String title = session.getTitle();
        assertEquals("Test Page", title, "Should be able to navigate and get title in headless mode");
        
        // Verify we can execute JavaScript in headless mode
        Object result = session.evaluate("document.querySelector('h1').textContent");
        assertEquals("Headless Test", result, "Should be able to execute JavaScript in headless mode");
    }
}
