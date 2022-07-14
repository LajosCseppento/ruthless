package dev.lajoscseppento.ruthless.plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.gradle.internal.impldep.org.eclipse.jgit.annotations.NonNull;

@UtilityClass
public class FunctionalTestUtils {
  public void copyDemoProject(@NonNull Path targetDirectory) {
    Path demoDir = Paths.get("../ruthless-demo").toAbsolutePath().normalize();
    try {
      FileUtils.copyDirectory(
          demoDir.toFile(), targetDirectory.toFile(), FunctionalTestUtils::shouldCopy, false);

      // Set up JaCoCo coverage for Gradle TestKit tests
      String extra;
      try (InputStream is =
          FunctionalTestUtils.class.getResourceAsStream("/testkit-gradle.properties")) {
        extra = new Scanner(is).useDelimiter("\\A").next();
      }

      Path gradleProperties = targetDirectory.resolve("gradle.properties");
      try (BufferedWriter writer =
          Files.newBufferedWriter(
              gradleProperties, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
        writer.append("\n");
        writer.append("# Coverage for Gradle TestKit tests");
        writer.append(extra);
      }
    } catch (Exception ex) {
      throw new RuntimeException("Failed to copy demo project to " + targetDirectory, ex);
    }
  }

  private boolean shouldCopy(@NonNull File file) {
    return file.isDirectory()
        || FilenameUtils.isExtension(file.getName(), "factories", "java", "kts", "properties");
  }
}
