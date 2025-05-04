package org.example.junitlearning;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@ExtendWith(LoggingExtension.class)
public class TestCalculatorWithExtension {

    private Calculator cal;

    @RegisterExtension
    private final LoggingExtension logging = new LoggingExtension();

    @BeforeEach
    public void setUp() {
        cal = new Calculator();
    }

    @AfterEach
    public void tearDown() {
        cal = null;
    }

    @Test
    @DisplayName("Test the Addition of two number")
    public void testAddMethod() {
        Integer result = cal.add(10, 20);
        assertEquals(30, result);
        assertTrue(logging.isExtensionClass());
    }

}

class LoggingExtension implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        System.out.println("Completed the Test Execution: " + context.getTestMethod().get().getName());
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        System.out.println("Started the Test Execution: " + context.getTestMethod().get().getName());
    }

    public boolean isExtensionClass() {
        System.out.println("Normal method of Extension class is called");
        return true;
    }
}
