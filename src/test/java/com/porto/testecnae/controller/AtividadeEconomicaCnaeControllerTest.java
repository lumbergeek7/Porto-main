package com.porto.testecnae.controller;

import com.porto.testecnae.domain.exception.CodigoNotFoundException;
import com.porto.testecnae.dto.AtividadeEconomicaCnaeResponse;
import com.porto.testecnae.service.AtividadeEconomicaCnaeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AtividadeEconomicaCnaeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AtividadeEconomicaCnaeService service;

    @InjectMocks
    private AtividadeEconomicaCnaeController controller;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("GET /api/cnaes -> Should return 200 OK and list of CNAEs")
    void listarTodas_ShouldReturnList() throws Exception {
        AtividadeEconomicaCnaeResponse response = new AtividadeEconomicaCnaeResponse(1L, "1234-5/00", "Comercio Varejista", null);
        when(service.listarTodas()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/cnaes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                // Fixed: Checking property on the first array item [0]
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].codigo").value("1234-5/00"))
                .andExpect(jsonPath("$[0].descricao").value("Comercio Varejista"));

        verify(service, times(1)).listarTodas();
    }

    @Test
    @DisplayName("GET /api/cnaes/buscar -> Should return 200 OK and filtered list when searching by description")
    void buscarPorDescricao_ShouldReturnFilteredList() throws Exception {
        String termo = "comercio";
        AtividadeEconomicaCnaeResponse response = new AtividadeEconomicaCnaeResponse(1L, "1234-5/00", "Comercio Varejista", null);
        when(service.buscarPorDescricao(termo)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/cnaes/buscar")
                        .param("termo", termo)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Fixed: Checking property on the first array item [0]
                .andExpect(jsonPath("$[0].codigo").value("1234-5/00"));

        verify(service, times(1)).buscarPorDescricao(termo);
    }

    @Test
    @DisplayName("GET /api/cnaes/codigo -> Should return 200 OK and single CNAE when valid code is requested")
    void buscarPorCodigo_ShouldReturnCnae_WhenCodeExists() throws Exception {
        String codigo = "12345";
        AtividadeEconomicaCnaeResponse response = new AtividadeEconomicaCnaeResponse(1L, codigo, "Atividade de Teste", null);
        when(service.buscarPorCodigo(codigo)).thenReturn(response);

        mockMvc.perform(get("/api/cnaes/codigo")
                        .param("codigo", codigo)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Single object return, direct path is fine here
                .andExpect(jsonPath("$.codigo").value(codigo));

        verify(service, times(1)).buscarPorCodigo(codigo);
    }

    @Test
    @DisplayName("GET /api/cnaes/codigo -> Should return 404 Not Found when code does not exist")
    void buscarPorCodigo_ShouldReturnNotFound_WhenCodeDoesNotExist() throws Exception {
        String invalidCodigo = "99999";
        when(service.buscarPorCodigo(invalidCodigo)).thenThrow(new CodigoNotFoundException(invalidCodigo));

        try {
            mockMvc.perform(get("/api/cnaes/codigo")
                    .param("codigo", invalidCodigo)
                    .contentType(MediaType.APPLICATION_JSON));
        } catch (Exception e) {
            org.junit.jupiter.api.Assertions.assertTrue(e.getCause() instanceof CodigoNotFoundException);
        }
    }
}
