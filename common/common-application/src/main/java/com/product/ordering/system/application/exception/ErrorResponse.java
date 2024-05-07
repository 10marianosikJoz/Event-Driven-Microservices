package com.product.ordering.system.application.exception;

import lombok.Builder;

@Builder
public record ErrorResponse(String responseCode, String message) {}
