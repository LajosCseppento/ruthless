package dev.lajoscseppento.ruthless.plugin;

import dev.lajoscseppento.ruthless.plugin.impl.AbstractProjectPlugin;
import dev.lajoscseppento.ruthless.plugin.impl.RuthlessSpringBootBasePlugin;
import java.util.Arrays;
import java.util.List;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.jvm.tasks.Jar;
import org.springframework.boot.gradle.tasks.bundling.BootJar;

public class RuthlessSpringBootLibraryPlugin extends AbstractProjectPlugin {
  @Override
  protected List<Class<? extends Plugin<Project>>> requiredPlugins() {
    return Arrays.asList(RuthlessJavaLibraryPlugin.class, RuthlessSpringBootBasePlugin.class);
  }

  @Override
  public void apply() {
    ((Jar) tasks.getByName("jar")).setEnabled(true);
    ((BootJar) tasks.getByName("bootJar")).setEnabled(false);
  }
}
