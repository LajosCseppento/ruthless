package dev.lajoscseppento.ruthless.plugin.impl;

import io.spring.gradle.dependencymanagement.DependencyManagementPlugin;
import java.util.Arrays;
import java.util.List;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.springframework.boot.gradle.plugin.SpringBootPlugin;

public class RuthlessSpringBootBasePlugin extends AbstractProjectPlugin {
  @Override
  protected List<Class<? extends Plugin<Project>>> requiredPlugins() {
    return Arrays.asList(
        JavaBasePlugin.class, SpringBootPlugin.class, DependencyManagementPlugin.class);
  }

  @Override
  public void apply() {
    dependencies.add(
        JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME,
        "org.springframework.boot:spring-boot-starter-test");
  }
}
