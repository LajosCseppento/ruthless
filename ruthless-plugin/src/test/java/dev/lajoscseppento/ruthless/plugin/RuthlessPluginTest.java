package dev.lajoscseppento.ruthless.plugin;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RuthlessPluginTest {
  @ParameterizedTest
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
    Project project = ProjectBuilder.builder().build();
    project.setGroup("test");
    project.setVersion("0.0.0-SNAPSHOT");

    // When
    project.getGradle().getPluginManager().apply("dev.lajoscseppento.ruthless");
    project.getPlugins().apply(pluginId);

    // Then
    assertTrue(project.getPluginManager().hasPlugin("java"));
  }
}
