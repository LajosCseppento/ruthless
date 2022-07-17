package dev.lajoscseppento.ruthless.plugin.impl;

import dev.lajoscseppento.gradle.plugin.common.property.ObjectSystemProperty;
import org.gradle.api.GradleException;
import org.gradle.jvm.toolchain.JavaLanguageVersion;

/** System property for Java language version. */
public class JavaLanguageVersionSystemProperty extends ObjectSystemProperty<JavaLanguageVersion> {
  private static final String JAVA_LANGUAGE_VERSION_PROPERTY_NAME = "ruthless.java.languageVersion";

  public JavaLanguageVersionSystemProperty() {
    super(
        JAVA_LANGUAGE_VERSION_PROPERTY_NAME,
        () -> {
          throw new GradleException(
              "Missing Java toolchain language version, please set the "
                  + JAVA_LANGUAGE_VERSION_PROPERTY_NAME
                  + " system property");
        },
        value -> {
          try {
            return JavaLanguageVersion.of(value);
          } catch (Exception ex) {
            throw new GradleException("Not recognised Java language version: " + value, ex);
          }
        });
  }
}
