package dev.lajoscseppento.ruthless.gradle.plugin.test;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import java.util.Scanner;
import lombok.experimental.UtilityClass;
import org.gradle.api.GradleException;
import org.gradle.testkit.runner.GradleRunner;

@UtilityClass
public class GradleRunnerWithCoverage {
//
//  public static GradleRunner create() {
//
//    Properties jacocoTestKitProperties;
//    try (InputStream is =
//        GradleRunnerWithCoverage.class.getResourceAsStream("/testkit-gradle.properties")) {
//      jacocoTestKitProperties = new Properties();
//      jacocoTestKitProperties.load(is);
//    } catch (Exception ex) {
//      throw new GradleException("Failed to read Gradle properties for Test Kit execution", ex);
//    }
//
//    GradleRunner runner = GradleRunner.create();
//    jacocoTestKitProperties.forEach((key, value) -> {
//      runner.withArguments()
//          runner.with
//    });
//
//    try {
//      // Set up JaCoCo coverage for Gradle TestKit tests
//
//      Path gradleProperties = targetDirectory.resolve("gradle.properties");
//      try (BufferedWriter writer =
//          Files.newBufferedWriter(
//              gradleProperties, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
//        writer.append("\n");
//        writer.append("# Coverage for Gradle TestKit tests");
//        writer.append(jacocoTestKitProperties);
//      }
//    } catch (Exception ex) {
//      throw new RuntimeException("Failed to copy demo project to " + targetDirectory, ex);
//    }
//  }
}
