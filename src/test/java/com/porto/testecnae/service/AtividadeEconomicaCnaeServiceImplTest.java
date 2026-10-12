package com.porto.testecnae.service;

import com.porto.testecnae.domain.exception.CodigoNotFoundException;
import com.porto.testecnae.domain.model.AtividadeEconomicaCnae;
import com.porto.testecnae.dto.AtividadeEconomicaCnaeResponse;
import com.porto.testecnae.repository.AtividadeEconomicaCnaeRepository;
import com.porto.testecnae.service.impl.AtividadeEconomicaCnaeServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtividadeEconomicaCnaeServiceImplTest {

    @Mock
    private AtividadeEconomicaCnaeRepository repository; // Matches your private final field name

    @InjectMocks
    private AtividadeEconomicaCnaeServiceImpl cnaeService;

    @Test
    @DisplayName("Should return list of mapped DTOs when searching by valid description term")
    void buscarPorDescricao_Success() {
        // Arrange
        String term = "comercio";
        AtividadeEconomicaCnae entity = new AtividadeEconomicaCnae();
        entity.setCodigo("1234-5/00");
        entity.setDescricao("Comercio Varejista");

        // Mocking your specific 'buscarPorDescricao' repository method
        when(repository.buscarPorDescricao(term)).thenReturn(List.of(entity));

        // Act
        List<AtividadeEconomicaCnaeResponse> result = cnaeService.buscarPorDescricao(term);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository, times(1)).buscarPorDescricao(term);
    }

    @Test
    @DisplayName("Should return empty list when description term matches nothing")
    void buscarPorDescricao_EmptyResult() {
        // Arrange
        String term = "unknown_term";
        when(repository.buscarPorDescricao(term)).thenReturn(Collections.emptyList());

        // Act
        List<AtividadeEconomicaCnaeResponse> result = cnaeService.buscarPorDescricao(term);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, times(1)).buscarPorDescricao(term);
    }

    @Test
    @DisplayName("Should return mapped DTO when searching by valid existing code")
    void buscarPorCodigo_Success() {
        // Arrange
        String codigo = "12345";
        AtividadeEconomicaCnae entity = new AtividadeEconomicaCnae();
        entity.setCodigo(codigo);
        entity.setDescricao("Atividade de Teste");

        when(repository.findByCodigo(codigo)).thenReturn(Optional.of(entity));

        // Act
        AtividadeEconomicaCnaeResponse response = cnaeService.buscarPorCodigo(codigo);

        // Assert
        assertNotNull(response);
        verify(repository, times(1)).findByCodigo(codigo);
    }

    @Test
    @DisplayName("Should throw CodigoNotFoundException when code does not exist")
    void buscarPorCodigo_NotFound_ThrowsException() {
        // Arrange
        String codigo = "99999";
        when(repository.findByCodigo(codigo)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CodigoNotFoundException.class, () -> cnaeService.buscarPorCodigo(codigo));
        verify(repository, times(1)).findByCodigo(codigo);
    }

    @Test
    @DisplayName("Should return list of all mapped DTOs when listing all activities")
    void listarTodas_Success() {
        // Arrange
        AtividadeEconomicaCnae entity1 = new AtividadeEconomicaCnae();
        entity1.setCodigo("1111-1/00");
        entity1.setDescricao("Atividade Um");

        AtividadeEconomicaCnae entity2 = new AtividadeEconomicaCnae();
        entity2.setCodigo("2222-2/00");
        entity2.setDescricao("Atividade Dois");

        when(repository.findAll()).thenReturn(List.of(entity1, entity2));

        // Act
        List<AtividadeEconomicaCnaeResponse> result = cnaeService.listarTodas();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no activities exist in the repository")
    void listarTodas_EmptyResult() {
        // Arrange
        when(repository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<AtividadeEconomicaCnaeResponse> result = cnaeService.listarTodas();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, times(1)).findAll();
    }
}
