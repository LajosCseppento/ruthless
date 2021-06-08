package dev.lajoscseppento.ruthless.plugin.impl;

import java.util.Objects;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;

public class RuthlessSettingsPlugin implements Plugin<Settings> {
  @Override
  public void apply(Settings settings) {
    checkGradleVersion(settings.getGradle().getGradleVersion());

    settings.getGradle().getPluginManager().apply(RuthlessGradlePlugin.class);

    settings
        .getGradle()
        .allprojects(project -> project.getPluginManager().apply(RuthlessBasePlugin.class));
  }

  private static void checkGradleVersion(String gradleVersion) {
    Objects.requireNonNull(gradleVersion, "gradleVersion");

    int major;
    int minor;

    try {
      String[] parts = gradleVersion.split("[.]");
      major = Integer.parseInt(parts[0]);
      minor = Integer.parseInt(parts[1]);
    } catch (Exception ex) {
      throw new GradleException("Failed to parse Gradle version: " + gradleVersion);
    }

    if (major < 7 || major == 7 && minor < 0) {
      throw new GradleException(
          "Gradle version is too old, please use 7.0 at least. Detected version: " + gradleVersion);
    }
  }
}
