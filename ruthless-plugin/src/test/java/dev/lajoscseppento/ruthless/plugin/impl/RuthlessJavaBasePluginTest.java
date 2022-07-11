package dev.lajoscseppento.ruthless.plugin.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.lajoscseppento.ruthless.plugin.TestUtils;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.ClearSystemProperty;
import org.junitpioneer.jupiter.SetSystemProperty;

class RuthlessJavaBasePluginTest {
  private Project project;
  private PluginManager pluginManager;

  @BeforeEach
  void setUp() {
    project = TestUtils.createProject();
    pluginManager = project.getPluginManager();

    project.getGradle().getPluginManager().apply("dev.lajoscseppento.ruthless");
  }

  @Test
  @SetSystemProperty(key = "ruthless.java.languageVersion", value = "8")
  void testApply() {
    // Given

    // When
    pluginManager.apply(RuthlessJavaBasePlugin.class);

    // Then
    assertThat(pluginManager.hasPlugin("java")).isTrue();
  }

  @Test
  @ClearSystemProperty(key = "ruthless.java.languageVersion")
  void testApplyFailsIfJavaVersionIsMissing() {
    // Given

    // When
    assertThatThrownBy(() -> pluginManager.apply(RuthlessJavaBasePlugin.class))
        // Then
        .isInstanceOf(GradleException.class)
        .hasMessage(
            "Failed to apply plugin class 'dev.lajoscseppento.ruthless.plugin.impl.RuthlessJavaBasePlugin'.")
        .getCause()
        .isInstanceOf(GradleException.class)
        .hasMessage(
            "Missing Java toolchain language version, please set the ruthless.java.languageVersion system property")
        .hasNoCause();
  }

  @Test
  @SetSystemProperty(key = "ruthless.java.languageVersion", value = "  ")
  void testApplyFailsIfJavaVersionIsBlank() {
    // Given

    // When
    assertThatThrownBy(() -> pluginManager.apply(RuthlessJavaBasePlugin.class))
        // Then
        .isInstanceOf(GradleException.class)
        .hasMessage(
            "Failed to apply plugin class 'dev.lajoscseppento.ruthless.plugin.impl.RuthlessJavaBasePlugin'.")
        .getCause()
        .isInstanceOf(GradleException.class)
        .hasMessage(
            "Missing Java toolchain language version, please set the ruthless.java.languageVersion system property")
        .hasNoCause();
  }

  @Test
  @SetSystemProperty(
      key = "ruthless.java.languageVersion",
      value = "eleven please, with a banana split")
  void testApplyFailsIfJavaVersionIsInvalid() {
    // Given

    // When
    assertThatThrownBy(() -> pluginManager.apply(RuthlessJavaBasePlugin.class))
        // Then
        .isInstanceOf(GradleException.class)
        .hasMessage(
            "Failed to apply plugin class 'dev.lajoscseppento.ruthless.plugin.impl.RuthlessJavaBasePlugin'.")
        .getCause()
        .isInstanceOf(GradleException.class)
        .hasMessage("Not recognised Java language version: eleven please, with a banana split");
  }
}
