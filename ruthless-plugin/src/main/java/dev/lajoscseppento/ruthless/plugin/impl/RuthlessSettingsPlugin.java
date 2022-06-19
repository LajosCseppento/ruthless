package dev.lajoscseppento.ruthless.plugin.impl;

import dev.lajoscseppento.gradle.plugin.common.GradleVersion;
import dev.lajoscseppento.ruthless.plugin.configuration.impl.RuthlessConfiguration;
import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;

public class RuthlessSettingsPlugin implements Plugin<Settings> {
  @Override
  public void apply(Settings settings) {
    GradleVersion.of(settings)
        .requireAtLeast(RuthlessConfiguration.INSTANCE.getMinimumGradleVersion());

    settings.getGradle().getPluginManager().apply(RuthlessGradlePlugin.class);

    settings
        .getGradle()
        .allprojects(project -> project.getPluginManager().apply(RuthlessBasePlugin.class));
  }
}
