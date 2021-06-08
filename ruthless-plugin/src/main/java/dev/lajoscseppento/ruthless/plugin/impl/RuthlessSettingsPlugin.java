package dev.lajoscseppento.ruthless.plugin.impl;

import dev.lajoscseppento.ruthless.plugin.configuration.impl.RuthlessConfiguration;
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

    String minimumGradleVersion = RuthlessConfiguration.INSTANCE.getMinimumGradleVersion();
    int cmp = new VersionComparator().compare(minimumGradleVersion, gradleVersion);

    if (cmp > 0) {
      String msg =
          String.format(
              "Gradle version is too old, please use %s at least. Detected version: %s",
              minimumGradleVersion, gradleVersion);
      throw new GradleException(msg);
    }
  }
}
