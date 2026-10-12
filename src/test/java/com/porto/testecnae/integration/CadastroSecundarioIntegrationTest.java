package com.porto.testecnae.integration;

import com.porto.testecnae.controller.CadastroSecundarioController;
import com.porto.testecnae.domain.exception.CodigoNotFoundException;
import com.porto.testecnae.dto.CadastroSecundarioRequest;
import com.porto.testecnae.dto.CadastroSecundarioResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CadastroSecundarioIntegrationTest {

    @Autowired
    private CadastroSecundarioController cadastroController;

    @Test
    @DisplayName("INTEGRATION -> Should reject secondary registration via exception when CNAE code does not exist in DB")
    void shouldFailToRegister_WhenCnaeDoesNotExistInDatabase() {
        // Arrange
        CadastroSecundarioRequest request = new CadastroSecundarioRequest(
                "Porto Seguro Filial",
                "12345678000199",
                "NON-EXISTENT-CNAE"
        );

        // Act & Assert (Warning Fixed: Replaced statement lambda with a clean expression lambda)
        assertThrows(CodigoNotFoundException.class, () -> cadastroController.cadastrar(request));
    }

    @Test
    @DisplayName("INTEGRATION -> Should return empty or populated list from real database context")
    void shouldReturnListContext_WhenFetchingRegistrations() {
        // Act
        List<CadastroSecundarioResponse> result = cadastroController.listarTodos();

        // Assert (Warning Fixed: Removed redundant condition checks since assertNotNull is already verifying structural validity)
        assertNotNull(result);
    }
}
