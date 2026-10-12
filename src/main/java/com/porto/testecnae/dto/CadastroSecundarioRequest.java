package com.porto.testecnae.dto;

import jakarta.validation.constraints.NotBlank;

public record CadastroSecundarioRequest(
        @NotBlank(message = "O nome fantasia não pode ser vazio.")
        String nomeFantasia,

        @NotBlank(message = "O documento não pode ser vazio.")
        String documento,

        @NotBlank(message = "O código CNAE não pode ser vazio.")
        String codigoCnae
) {
}
