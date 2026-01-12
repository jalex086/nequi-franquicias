package co.com.bancolombia.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UseCasesConfigTest {

    @Test
    void testUseCasesConfigExists() {
        // Test simple que verifica que la clase existe
        UseCasesConfig config = new UseCasesConfig();
        assertNotNull(config);
    }
}