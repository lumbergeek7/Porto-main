package com.porto.testecnae.controller;

import com.porto.testecnae.domain.exception.CodigoNotFoundException;
import com.porto.testecnae.dto.AtividadeEconomicaCnaeResponse;
import com.porto.testecnae.dto.CadastroSecundarioRequest;
import com.porto.testecnae.dto.CadastroSecundarioResponse;
import com.porto.testecnae.service.CadastroSecundarioService;
import org.junit.jupiter.api.Assertions;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CadastroSecundarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CadastroSecundarioService service;

    @InjectMocks
    private CadastroSecundarioController controller;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("POST /api/cadastros-secundarios -> Should return 201 Created when registration succeeds")
    void cadastrar_ShouldReturnCreated() throws Exception {
        CadastroSecundarioResponse response = new CadastroSecundarioResponse(1L, "Empresa Teste", "12345678901", null);
        when(service.cadastrar(any(CadastroSecundarioRequest.class))).thenReturn(response);

        String jsonPayload = """
                {
                    "nomeFantasia": "Empresa Teste",
                    "documento": "12345678901",
                    "codigoCnae": "12345"
                }
                """;

        mockMvc.perform(post("/api/cadastros-secundarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nomeFantasia").value("Empresa Teste"));

        verify(service, times(1)).cadastrar(any(CadastroSecundarioRequest.class));
    }

    @Test
    @DisplayName("POST /api/cadastros-secundarios -> Should intercept exception when CNAE does not exist")
    void cadastrar_ShouldThrowNotFound_WhenCnaeDoesNotExist() throws Exception {
        // Arrange
        when(service.cadastrar(any(CadastroSecundarioRequest.class)))
                .thenThrow(new CodigoNotFoundException("99999"));

        String jsonPayload = """
            {
                "nomeFantasia": "Empresa Teste",
                "documento": "12345678901",
                "codigoCnae": "99999"
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/cadastros-secundarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(result -> Assertions.assertInstanceOf(CodigoNotFoundException.class, result.getResolvedException()));
    }

    @Test
    @DisplayName("GET /api/cadastros-secundarios/validar-cnae -> Should return 200 OK and mapped CNAE response")
    void validarCnae_ShouldReturnCnae_WhenValid() throws Exception {
        String codigoCnae = "12345";
        AtividadeEconomicaCnaeResponse response = new AtividadeEconomicaCnaeResponse(1L, codigoCnae, "Atividade Válida", null);
        when(service.validarCnae(codigoCnae)).thenReturn(response);

        mockMvc.perform(get("/api/cadastros-secundarios/validar-cnae")
                        .param("codigoCnae", codigoCnae)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(codigoCnae));

        verify(service, times(1)).validarCnae(codigoCnae);
    }

    @Test
    @DisplayName("GET /api/cadastros-secundarios -> Should return 200 OK and list of registrations")
    void listarTodos_ShouldReturnList() throws Exception {
        CadastroSecundarioResponse response = new CadastroSecundarioResponse(1L, "Empresa Um", "11111111111", null);
        when(service.listarTodos()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/cadastros-secundarios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                // Fixed: Target property using indexed array location $[0]
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nomeFantasia").value("Empresa Um"));

        verify(service, times(1)).listarTodos();
    }
}
