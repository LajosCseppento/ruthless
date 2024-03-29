package dev.lajoscseppento.ruthless.plugin;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RuthlessPluginFunctionalTest {
  @TempDir Path projectDir;

  @BeforeEach
  void setUp() {
    FunctionalTestUtils.copyDemoProject(projectDir);
  }

  @Test
  void testBuild() {
    // Given
    GradleRunner runner =
        GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withArguments("build")
            .withProjectDir(projectDir.toFile());

    // When
    BuildResult result = runner.build();

    // Then
    assertThat(result.getOutput())
        .contains("Task :ruthless-demo-java-application:build")
        .contains("Task :ruthless-demo-java-gradle-plugin:build")
        .contains("Task :ruthless-demo-java-library:build")
        .contains("Task :ruthless-demo-spring-boot-application:build")
        .contains("Task :ruthless-demo-spring-boot-library:build");
  }

  @Test
  void testAssembleWithRuthlessLoggerDebug() {
    // Given
    GradleRunner runner =
        GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withArguments("--system-prop", "ruthless.logging.logger.*.debug=true", "assemble")
            .withProjectDir(projectDir.toFile());

    // When
    BuildResult result = runner.build();

    // Then
    assertThat(result.getOutput())
        .contains("[INFO] [ruthless] Applying RuthlessBasePlugin to root project 'ruthless-demo'")
        .contains(
            "[DEBUG] [ruthless] Declaring org.projectlombok:lombok on configuration ':ruthless-demo-java-library:compileOnly'");
  }

  @Test
  void testRunDryRun() {
    // Given
    GradleRunner runner =
        GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withArguments("run", "--dry-run")
            .withProjectDir(projectDir.toFile());

    // When
    BuildResult result = runner.build();

    // Then
    assertThat(result.getOutput())
        .contains(":ruthless-demo-java-application:run SKIPPED")
        .contains(":ruthless-demo-spring-boot-application:run SKIPPED");
  }

  @Test
  void testBuildFailsWithTooOldGradleVersion() {
    // Given
    GradleRunner runner =
        GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withGradleVersion("7.6")
            .withArguments("build")
            .withProjectDir(projectDir.toFile());

    // When
    BuildResult result = runner.buildAndFail();

    // Then
    assertThat(result.getOutput())
        .containsPattern("Gradle version .+ is too old, please use .+ at least");
  }
}
