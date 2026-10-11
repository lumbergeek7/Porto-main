package com.porto.testecnae.service;

import com.porto.testecnae.domain.exception.CodigoNotFoundException;
import com.porto.testecnae.domain.model.AtividadeEconomicaCnae;
import com.porto.testecnae.domain.model.CadastroSecundario;
import com.porto.testecnae.dto.CadastroSecundarioRequest;
import com.porto.testecnae.dto.CadastroSecundarioResponse;
import com.porto.testecnae.repository.AtividadeEconomicaCnaeRepository;
import com.porto.testecnae.repository.CadastroSecundarioRepository;
import com.porto.testecnae.service.impl.CadastroSecundarioServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CadastroSecundarioServiceImplTest {

    @Mock
    private CadastroSecundarioRepository repository;

    @Mock
    private AtividadeEconomicaCnaeRepository cnaeRepository;

    @InjectMocks
    private CadastroSecundarioServiceImpl cadastroService;

    @Test
    @DisplayName("Should successfully register when valid request and CNAE exists")
    void cadastrar_Success() {
        // Arrange
        String mockCnaeCode = "12345";

        // Creating mock record/request matching your accessor methods like request.codigoCnae()
        CadastroSecundarioRequest request = new CadastroSecundarioRequest(
                "My Company LTDA",
                "12345678901",
                mockCnaeCode
        );

        AtividadeEconomicaCnae mockCnae = new AtividadeEconomicaCnae();
        mockCnae.setCodigo(mockCnaeCode);

        CadastroSecundario mockSavedCadastro = CadastroSecundario.builder()
                .id(1L)
                .nomeFantasia("My Company LTDA")
                .documento("12345678901")
                .cnae(mockCnae)
                .build();

        // Mocking both repository actions used internally
        when(cnaeRepository.findByCodigo(mockCnaeCode)).thenReturn(Optional.of(mockCnae));
        when(repository.save(any(CadastroSecundario.class))).thenReturn(mockSavedCadastro);

        // Act
        CadastroSecundarioResponse response = cadastroService.cadastrar(request);

        // Assert
        assertNotNull(response);
        verify(cnaeRepository, times(1)).findByCodigo(mockCnaeCode);
        verify(repository, times(1)).save(any(CadastroSecundario.class));
    }

    @Test
    @DisplayName("Should throw CodigoNotFoundException when the requested CNAE code does not exist")
    void cadastrar_CnaeNotFound_ThrowsException() {
        // Arrange
        String invalidCnaeCode = "99999";
        CadastroSecundarioRequest request = new CadastroSecundarioRequest(
                "My Company LTDA",
                "12345678901",
                invalidCnaeCode
        );

        // Mocking missing code scenario
        when(cnaeRepository.findByCodigo(invalidCnaeCode)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CodigoNotFoundException.class, () -> {
            cadastroService.cadastrar(request);
        });

        // Verification checks to prove it failed early and safely skipped saving to the DB
        verify(cnaeRepository, times(1)).findByCodigo(invalidCnaeCode);
        verify(repository, never()).save(any(CadastroSecundario.class));
    }
}
