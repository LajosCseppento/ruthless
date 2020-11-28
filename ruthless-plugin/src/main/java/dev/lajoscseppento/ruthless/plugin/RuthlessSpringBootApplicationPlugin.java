package dev.lajoscseppento.ruthless.plugin;

import dev.lajoscseppento.ruthless.plugin.impl.AbstractProjectPlugin;
import dev.lajoscseppento.ruthless.plugin.impl.RuthlessSpringBootBasePlugin;
import java.util.Arrays;
import java.util.List;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class RuthlessSpringBootApplicationPlugin extends AbstractProjectPlugin {
  @Override
  protected List<Class<? extends Plugin<Project>>> requiredPlugins() {
    return Arrays.asList(RuthlessJavaApplicationPlugin.class, RuthlessSpringBootBasePlugin.class);
  }
}
