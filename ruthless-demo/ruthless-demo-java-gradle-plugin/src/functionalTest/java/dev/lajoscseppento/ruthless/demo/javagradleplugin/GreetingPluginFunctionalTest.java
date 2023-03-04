package dev.lajoscseppento.ruthless.demo.javagradleplugin;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.io.MoreFiles;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
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

    // TODO
//    ectory.toFile(), FunctionalTestUtils::shouldCopy, false);
//
//    // Set up JaCoCo coverage for Gradle TestKit tests
//    String jacocoTestKitProperties;
//    try (InputStream is =
//        FunctionalTestUtils.class.getResourceAsStream("/testkit-gradle.properties")) {
//      jacocoTestKitProperties = new Scanner(is).useDelimiter("\\A").next();
//    }
//
//    Path gradleProperties = targetDirectory.resolve("gradle.properties");
//    try (BufferedWriter writer =
//        Files.newBufferedWriter(
//            gradleProperties, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
//      writer.append("\n");
//      writer.append("# Coverage for Gradle TestKit tests");
//      writer.append(jacocoTestKitProperties);
//    }
//  } catch (Exception ex) {
//    throw new RuntimeException("Failed to copy demo project to " + targetDirectory, ex);
//  }

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
