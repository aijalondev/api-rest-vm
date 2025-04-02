package com.api_rest_vm.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorResponse(
        @Schema(description = "O código de status HTTP correspondente ao erro") int statusCode,
        @Schema(description = "Uma mensagem descritiva do erro") String message) {

}
