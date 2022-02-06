package dev.lajoscseppento.ruthless.plugin.impl;

import dev.lajoscseppento.ruthless.plugin.RuthlessExtension;
import lombok.NonNull;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.JavaPlugin;

public class RuthlessExtensionImpl implements RuthlessExtension {
  private final Project project;
  private final Logger logger;

  RuthlessExtensionImpl(@NonNull Project project) {
    this.project = project;
    this.logger = project.getLogger();
  }

  @Override
  public void lombok() {
    logger.info("[ruthless] Adding lombok to {}", project);

    DependencyHandler deps = project.getDependencies();
    String dep = "org.projectlombok:lombok";
    deps.add(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME, dep);
    deps.add(JavaPlugin.ANNOTATION_PROCESSOR_CONFIGURATION_NAME, dep);
    deps.add(JavaPlugin.TEST_COMPILE_ONLY_CONFIGURATION_NAME, dep);
    deps.add(JavaPlugin.TEST_ANNOTATION_PROCESSOR_CONFIGURATION_NAME, dep);
  }
}
