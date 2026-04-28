package com.mycompany.app;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

public class AppTest {
    @Test
    void appMainRuns() {
        assertDoesNotThrow(() -> App.main(new String[]{}));
    }
}
