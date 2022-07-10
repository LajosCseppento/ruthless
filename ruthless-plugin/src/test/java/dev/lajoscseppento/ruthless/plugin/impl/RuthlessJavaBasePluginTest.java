package dev.lajoscseppento.ruthless.plugin.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.lajoscseppento.ruthless.plugin.TestUtils;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.ClearSystemProperty;
import org.junitpioneer.jupiter.SetSystemProperty;

class RuthlessJavaBasePluginTest {
  private Project project;

  @BeforeEach
  void setUp() {
    project = TestUtils.createProject();
    project.getGradle().getPluginManager().apply("dev.lajoscseppento.ruthless");
  }

  @Test
  @SetSystemProperty(key = "ruthless.java.languageVersion", value = "8")
  void testApply() {
    // Given

    // When
    project.getPlugins().apply(RuthlessJavaBasePlugin.class);

    // Then
    assertThat(project.getPlugins().hasPlugin(RuthlessJavaBasePlugin.class)).isTrue();
  }

  @Test
  @ClearSystemProperty(key = "ruthless.java.languageVersion")
  void testApplyFailsIfJavaVersionIsMissing() {
    // Given

    // When
    assertThatThrownBy(() -> project.getPlugins().apply(RuthlessJavaBasePlugin.class))
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
    assertThatThrownBy(() -> project.getPlugins().apply(RuthlessJavaBasePlugin.class))
        // Then
        .isInstanceOf(GradleException.class)
        .hasMessage(
            "Failed to apply plugin class 'dev.lajoscseppento.ruthless.plugin.impl.RuthlessJavaBasePlugin'.")
        .getCause()
        .isInstanceOf(GradleException.class)
        .hasMessage("Not recognised Java language version: eleven please, with a banana split");
  }
}
