package dev.lajoscseppento.ruthless.plugin.impl;

import dev.lajoscseppento.gradle.plugin.common.GradleVersion;
import dev.lajoscseppento.ruthless.plugin.configuration.impl.RuthlessConfiguration;
import dev.lajoscseppento.ruthless.plugin.logging.impl.RuthlessLoggingPlugin;
import lombok.NonNull;
import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;
import org.gradle.api.invocation.Gradle;

public class RuthlessSettingsPlugin implements Plugin<Settings> {
  @Override
  public void apply(@NonNull Settings settings) {
    GradleVersion.of(settings)
        .requireAtLeast(RuthlessConfiguration.INSTANCE.getMinimumGradleVersion());

    settings.getPluginManager().apply(RuthlessLoggingPlugin.class);

    Gradle gradle = settings.getGradle();
    gradle.getPluginManager().apply(RuthlessGradlePlugin.class);
    gradle.allprojects(project -> project.getPluginManager().apply(RuthlessBasePlugin.class));
  }
}
