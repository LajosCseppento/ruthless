package dev.lajoscseppento.ruthless.plugin;

import dev.lajoscseppento.ruthless.plugin.impl.AbstractProjectPlugin;
import dev.lajoscseppento.ruthless.plugin.impl.RuthlessSpringBootBasePlugin;
import java.util.Arrays;
import java.util.List;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.springframework.boot.gradle.plugin.SpringBootPlugin;

public class RuthlessSpringBootLibraryPlugin extends AbstractProjectPlugin {
  @Override
  protected List<Class<? extends Plugin<Project>>> requiredPlugins() {
    return Arrays.asList(RuthlessJavaLibraryPlugin.class, RuthlessSpringBootBasePlugin.class);
  }

  @Override
  public void apply() {
    tasks.getByName(JavaPlugin.JAR_TASK_NAME).setEnabled(true);
    tasks.getByName(SpringBootPlugin.BOOT_JAR_TASK_NAME).setEnabled(false);
  }
}
