package dev.lajoscseppento.ruthless.plugin.impl;

import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;

public class RuthlessSettingsPlugin implements Plugin<Settings> {
  @Override
  public void apply(Settings settings) {
    settings.getGradle().getPluginManager().apply(RuthlessGradlePlugin.class);

    settings
        .getGradle()
        .allprojects(project -> project.getPluginManager().apply(RuthlessBasePlugin.class));
  }
}
