package com.acd.researchrepo.environment;

import jakarta.annotation.PostConstruct;
import java.util.List;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/** Fails application startup when required environment variables are missing or blank. */
@Component
public class RequiredEnvValidator {

  private static final List<String> REQUIRED_KEYS =
      List.of(
          "SPRING_DATASOURCE_USERNAME",
          "SPRING_DATASOURCE_PASSWORD",
          "SPRING_SECURITY_USER_NAME",
          "SPRING_SECURITY_USER_PASSWORD");

  private final Environment environment;

  public RequiredEnvValidator(Environment environment) {
    this.environment = environment;
  }

  @PostConstruct
  void validate() {
    List<String> missing =
        REQUIRED_KEYS.stream().filter(key -> isBlank(environment.getProperty(key))).toList();

    if (!missing.isEmpty()) {
      throw new IllegalStateException(
          "Missing or empty required environment variables: " + missing);
    }
  }

  private static boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
