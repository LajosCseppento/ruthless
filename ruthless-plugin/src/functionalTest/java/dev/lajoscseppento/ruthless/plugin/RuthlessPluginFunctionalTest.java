package dev.lajoscseppento.ruthless.plugin;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RuthlessPluginFunctionalTest {
  @TempDir Path projectDir;

  @BeforeEach
  void setUp() throws Exception {
    Path demoDir = Paths.get("../ruthless-demo").toAbsolutePath().normalize();
    FileUtils.copyDirectory(
        demoDir.toFile(), projectDir.toFile(), RuthlessPluginFunctionalTest::shouldCopy, false);
  }

  private static boolean shouldCopy(File file) {
    return file.isDirectory()
        || FilenameUtils.isExtension(file.getName(), "factories", "java", "kts", "properties");
  }

  @Test
  void testBuild() {
    // Given
    GradleRunner runner =
        GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withArguments("build", "--scan", "--no-build-cache")
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
            .withGradleVersion("7.4.1")
            .withArguments("build")
            .withProjectDir(projectDir.toFile());

    // When
    BuildResult result = runner.buildAndFail();

    // Then
    assertThat(result.getOutput())
        .containsPattern("Gradle version .+ is too old, please use .+ at least");
  }
}
