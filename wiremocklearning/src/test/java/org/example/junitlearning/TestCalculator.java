package org.example.junitlearning;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("This is a Calculator Test class")
public class TestCalculator {

    private Calculator cal;


    @BeforeAll
    public static void beforeAll() {
        System.out.println("This is a Before All Method");
    }

    @BeforeEach
    public void setup() {
        cal = new Calculator();
        System.out.println("This is Before Each Method");
    }

    @Test
    @DisplayName("Test Addition of two numbers. ")
    public void testAddMethod() {
        //Calculator cal = new Calculator();
        Integer result = cal.add(10, 20);
        assertEquals(30, result);
        assertEquals(30, result, "Add method returned incorrect result");
        System.out.println("This is a Add Method");
    }


    @Test
    @DisplayName("Test subtraction of two numbers. ")
    public void testSubtractMethod() {
        //Calculator cal = new Calculator();
        Integer result = cal.subtract(10, 78);
        assertEquals(-68, result);
        System.out.println("This is a Subtract Method");
    }

    @Test
    @DisplayName("Test if the number is even or odd. ")
    public void testIsEvenMethod() {
        //Calculator cal = new Calculator();
        boolean result = cal.isEven(12);
        assertTrue(result);
        System.out.println("This is a IsEven Method");
    }


    @AfterEach
    public void tearDown() {
        cal = null;
        System.out.println("This is After Each Method");
    }

    @AfterAll
    public static void afterAll() {
        System.out.println("This is a After All Method");
    }

}
