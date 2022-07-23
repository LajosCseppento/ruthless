package dev.lajoscseppento.ruthless.demo.javagradleplugin;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.io.MoreFiles;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import lombok.NonNull;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GreetingPluginFunctionalTest {
  @TempDir Path projectDir;

  @Test
  void test() {
    // Given
    writeFile("settings.gradle.kts", "");
    writeFile(
        "build.gradle.kts",
        "plugins { id(\"dev.lajoscseppento.ruthless.demo.java-gradle-plugin\") }");

    // When
    BuildResult result =
        GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withArguments("greeting")
            .withProjectDir(projectDir.toFile())
            .build();

    // Then
    assertThat(result.getOutput()).contains("Greetings from ruthless-demo-java-gradle-plugin!");
  }

  private void writeFile(@NonNull String relativePath, @NonNull String content) {
    try {
      Path file = projectDir.resolve(relativePath);
      MoreFiles.asCharSink(file, StandardCharsets.UTF_8).write(content);
    } catch (Exception ex) {
      throw new AssertionError("Failed to write file: " + relativePath, ex);
    }
  }
}
