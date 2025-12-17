# CDP4J Demo Project

This project demonstrates how to use the CDP4J library (Chrome DevTools Protocol for Java) to automate Chrome browser interactions using JUnit 5 test cases.

## Dependencies

- **cdp4j**: `io.webfolder:cdp4j:3.0.8` - Chrome DevTools Protocol client for Java
- **JUnit 5**: For testing framework
- **Maven Surefire Plugin**: For JUnit 5 support

## Features

The demo includes:
- **JUnit 5 Test Cases**: Automated tests using CDP4J
- **Headless Chrome**: Browser runs with `--headless --disable-gpu --incognito` arguments
- **Web Navigation**: Automated navigation to www.fast.com
- **Page Title Extraction**: Retrieves and validates page titles
- **Proper Resource Cleanup**: Automatic browser session management

## Prerequisites

- Java 8 or higher
- Maven 3.x
- Chrome browser installed on your system

## Building the Project

```bash
mvn clean compile
```

## Running the Demo

### Option 1: Run JUnit 5 Tests (Recommended)
```bash
# Run all tests
mvn test

# Run only the CDP demo tests
mvn test -Dtest=CdpDemoTest

# Run a specific test method
mvn test -Dtest=CdpDemoTest#testNavigateToFastComAndGetTitle
```

### Option 2: Run the CDP Demo directly (Non-headless)
```bash
mvn exec:java -Dexec.mainClass="com.bidgely.CdpDemo"
```

### Option 3: Run through the main App class
```bash
mvn exec:java -Dexec.mainClass="com.bidgely.App" -Dexec.args="cdp"
```

## What the Demo Does

### JUnit 5 Test Cases (`CdpDemoTest`)

1. **Setup**: Launches Chrome in headless mode with arguments:
   - `--headless` (no GUI)
   - `--disable-gpu` (disable GPU acceleration)
   - `--incognito` (private browsing mode)
   - `--no-sandbox` (for CI environments)
   - `--disable-dev-shm-usage` (overcome resource limitations)

2. **Test Execution**:
   - Creates a new browser session (tab)
   - Navigates to https://www.fast.com
   - Waits for the page to load completely
   - Extracts and validates the page title
   - Performs assertions to verify correct functionality

3. **Cleanup**: Automatically closes browser session and cleans up resources

### Expected Test Output

```
Chrome browser launched in headless mode with incognito and GPU disabled
Navigating to: https://www.fast.com
Page title: Internet Speed Test | Fast.com
Chrome browser session closed

Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
```

## Test Classes

### `CdpDemoTest.java`
- **`testNavigateToFastComAndGetTitle()`**: Tests navigation to fast.com and title extraction
- **`testHeadlessMode()`**: Verifies Chrome runs correctly in headless mode with JavaScript execution

### `AppTest.java`
- **`testAppClassExists()`**: Verifies the main App class exists
- **`testCdpDemoClassExists()`**: Verifies the CdpDemo class exists

## Notes

- **Headless Mode**: Tests run Chrome in headless mode (no visible browser window)
- **Chrome Installation**: Make sure Chrome is installed and accessible from your system PATH
- **Automatic Cleanup**: Browser sessions are automatically closed after each test
- **CI/CD Ready**: Tests include arguments suitable for CI environments (`--no-sandbox`, `--disable-dev-shm-usage`)
- **Incognito Mode**: All tests run in private browsing mode for isolation


