package com.acd.researchrepo.exception;

import com.acd.researchrepo.dto.external.error.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.MDC;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Custom AuthenticationEntryPoint that returns a structured JSON error response for 401
 * unauthorized errors. This is separate from GlobalExceptionHandler because security filter-chain
 * errors happen before the controller layer, so @ControllerAdvice can't catch them.
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  /**
   * Creates an authentication entry point that serializes error responses with the specified object mapper.
   *
   * @param objectMapper the object mapper used to serialize authentication error responses
   */
  public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /**
   * Writes a JSON response indicating that authentication is required.
   *
   * @param response HTTP response to populate with the unauthorized error payload
   * @throws IOException if the error response cannot be written
   */
  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {

    response.setContentType("application/json");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .code(ErrorCode.UNAUTHENTICATED.name())
            .message(ErrorCode.UNAUTHENTICATED.getDefaultMessage())
            .details(null)
            .traceId(MDC.get("traceId"))
            .build();

    objectMapper.writeValue(response.getWriter(), errorResponse);
  }
}
