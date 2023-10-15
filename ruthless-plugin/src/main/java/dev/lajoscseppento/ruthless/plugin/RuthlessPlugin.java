package dev.lajoscseppento.ruthless.plugin;

import dev.lajoscseppento.ruthless.plugin.impl.RuthlessBasePlugin;
import dev.lajoscseppento.ruthless.plugin.impl.RuthlessGradlePlugin;
import dev.lajoscseppento.ruthless.plugin.impl.RuthlessSettingsPlugin;
import lombok.NonNull;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.initialization.Settings;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.plugins.PluginAware;

/**
 * Meta-plugin which can be applied to any {@link PluginAware object} and applies the appropriate
 * plugin. This allows to re-use a plugin id for different Gradle components.
 */
public class RuthlessPlugin implements Plugin<PluginAware> {
  @Override
  public void apply(@NonNull PluginAware target) {
    if (target instanceof Project) {
      target.getPlugins().apply(RuthlessBasePlugin.class);
    } else if (target instanceof Settings) {
      target.getPlugins().apply(RuthlessSettingsPlugin.class);
    } else if (target instanceof Gradle) {
      target.getPlugins().apply(RuthlessGradlePlugin.class);
    } else {
      throw new AssertionError("Unsupported target class: " + target.getClass().getName());
    }
  }
}
