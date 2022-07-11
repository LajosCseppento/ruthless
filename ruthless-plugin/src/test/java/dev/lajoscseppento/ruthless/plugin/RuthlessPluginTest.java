package dev.lajoscseppento.ruthless.plugin;

import static org.assertj.core.api.Assertions.assertThat;

import org.gradle.api.Project;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junitpioneer.jupiter.SetSystemProperty;

class RuthlessPluginTest {
  @ParameterizedTest
  @SetSystemProperty(key = "ruthless.java.languageVersion", value = "8")
  @ValueSource(
      strings = {
        "dev.lajoscseppento.ruthless.java-application",
        "dev.lajoscseppento.ruthless.java-gradle-plugin",
        "dev.lajoscseppento.ruthless.java-library",
        "dev.lajoscseppento.ruthless.spring-boot-application",
        "dev.lajoscseppento.ruthless.spring-boot-library"
      })
  void testApply(String pluginId) {
    // Given
    Project project = TestUtils.createProject();

    // When
    project.getGradle().getPluginManager().apply("dev.lajoscseppento.ruthless");
    project.getPlugins().apply(pluginId);

    // Then
    assertThat(project.getPluginManager().hasPlugin("java")).isTrue();
  }
}
