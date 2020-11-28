package dev.lajoscseppento.ruthless.demo.javagradleplugin;

import static org.assertj.core.api.Assertions.assertThat;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

class GreetingPluginTest {
  @Test
  void test() {
    // Given
    Project project = ProjectBuilder.builder().build();

    // When
    project.getPlugins().apply("dev.lajoscseppento.ruthless.demo.java-gradle-plugin");

    // Then
    assertThat(project.getTasks().findByName("greeting")).isNotNull();
  }
}
