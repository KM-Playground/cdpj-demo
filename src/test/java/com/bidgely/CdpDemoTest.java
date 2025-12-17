package com.bidgely;

import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.session.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 test case for CDP4J demo using headless Chrome
 * with --headless --disable-gpu --incognito arguments.
 *
 * Demonstrates proper browser context isolation and process management
 * for production-grade Chrome automation.
 */
public class CdpDemoTest {

    private static final String HEADLESS = "--headless";
    private static final String DISABLE_GPU = "--disable-gpu";
    private static final String INCOGNITO = "--incognito";

    private Launcher launcher;
    private SessionFactory factory;
    private Session session;
    private String browserContext;

    @BeforeEach
    void setUp() {
        // Create launcher with Chrome arguments for headless, GPU-disabled, incognito mode
        // Store launcher as instance variable to access ProcessManager for proper cleanup
        launcher = new Launcher();

        // Prepare Chrome arguments for headless operation
        List<String> chromeArgs = Arrays.asList(HEADLESS, DISABLE_GPU, INCOGNITO);

        // Launch Chrome with the specified arguments
        // Using Paths.get(launcher.findChrome()) for explicit Chrome binary path
        factory = launcher.launch(Paths.get(launcher.findChrome()), chromeArgs);

        // Create an isolated browser context for better test isolation
        browserContext = factory.createBrowserContext();

        // Create a new session (tab) within the isolated browser context
        session = factory.create(browserContext);

        System.out.println("Chrome browser launched in headless mode with incognito and GPU disabled");
        System.out.println("Browser context created: " + browserContext);
    }
    
    @AfterEach
    void tearDown() {
        // Clean up resources in proper order with nested try-finally blocks
        try {
            if (session != null) {
                session.close();
                System.out.println("Session closed");
            }
        } finally {
            try {
                if (factory != null) {
                    // Dispose the browser context before closing the factory
                    if (browserContext != null) {
                        factory.disposeBrowserContext(browserContext);
                        System.out.println("Browser context disposed: " + browserContext);
                    }
                    factory.close();
                    System.out.println("Factory closed");
                }
            } finally {
                // Explicitly kill the Chrome process using ProcessManager
                // This ensures no orphaned Chrome processes remain after tests
                if (launcher != null && launcher.getProcessManager() != null) {
                    boolean isLauncherClosed = launcher.getProcessManager().kill();
                    System.out.println("Chrome shut down successfully - " + isLauncherClosed);
                }
            }
        }
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

    @Test
    @DisplayName("Should generate PDF from fast.com page using Chrome DevTools Protocol")
    void testPrintToPdf() throws IOException {
        // Navigate to www.fast.com
        String url = "https://www.fast.com";
        System.out.println("Navigating to: " + url);

        session.navigate(url);

        // Wait for the page to load
        session.waitDocumentReady();

        System.out.println("Page loaded. Generating PDF...");

        // Generate PDF using Chrome DevTools Protocol Page.printToPDF command
        // Parameters configured for US Letter size with standard margins
        byte[] pdfBytes = session.getCommand().getPage().printToPDF(
            false,  // landscape - false for portrait mode
            false,  // displayHeaderFooter
            true,   // printBackground - include background graphics
            1.0,    // scale/zoom - 1.0 for 100%
            8.5,    // paperWidth in inches (US Letter)
            11.0,   // paperHeight in inches (US Letter)
            0.4,    // marginTop in inches
            0.4,    // marginBottom in inches
            0.4,    // marginLeft in inches
            0.4,    // marginRight in inches
            "",     // pageRanges - empty for all pages
            false,  // ignoreInvalidPageRanges
            "",     // headerTemplate
            "",     // footerTemplate
            false   // preferCSSPageSize
        );

        // Verify PDF was generated
        assertNotNull(pdfBytes, "PDF bytes should not be null");
        assertTrue(pdfBytes.length > 0, "PDF should have content");

        System.out.println("PDF generated successfully. Size: " + pdfBytes.length + " bytes");

        // Save PDF to target directory for artifact upload
        Path targetDir = Paths.get("target", "pdf-output");
        Files.createDirectories(targetDir);

        Path pdfPath = targetDir.resolve("fast-com-test.pdf");
        Files.write(pdfPath, pdfBytes);

        System.out.println("PDF saved to: " + pdfPath.toAbsolutePath());

        // Verify the file was created
        assertTrue(Files.exists(pdfPath), "PDF file should exist");
        assertTrue(Files.size(pdfPath) > 0, "PDF file should not be empty");
    }
}
