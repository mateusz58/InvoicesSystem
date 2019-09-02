package pl.coderstrust;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HelloWorldTest {

    @Test
    void shouldReturnTrueForPalindromeWord() {
        assertTrue(HelloWorld.isPalindrome("abba"));
    }
}
