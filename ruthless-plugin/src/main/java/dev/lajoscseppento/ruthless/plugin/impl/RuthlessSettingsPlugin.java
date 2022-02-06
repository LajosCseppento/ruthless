package dev.lajoscseppento.ruthless.plugin.impl;

import dev.lajoscseppento.ruthless.plugin.configuration.impl.RuthlessConfiguration;
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
    String minimumGradleVersion = RuthlessConfiguration.INSTANCE.getMinimumGradleVersion();
    int cmp = new VersionComparator().compare(minimumGradleVersion, gradleVersion);

    if (cmp > 0) {
      String msg =
          String.format(
              "Gradle version %s is too old, please use %s at least.",
              gradleVersion, minimumGradleVersion);
      throw new GradleException(msg);
    }
  }
}
