package dev.lajoscseppento.ruthless.plugin.impl;

import dev.lajoscseppento.ruthless.plugin.RuthlessExtension;
import dev.lajoscseppento.ruthless.plugin.logging.impl.RuthlessLogger;
import lombok.NonNull;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;

public class RuthlessExtensionImpl implements RuthlessExtension {
  private static final String LOMBOK_DEPENDENCY = "org.projectlombok:lombok";
  private final Project project;
  private final RuthlessLogger logger;

  RuthlessExtensionImpl(@NonNull Project project) {
    this.project = project;
    this.logger = RuthlessLogger.create(project.getLogger(), "ruthless");
  }

  @Override
  public void lombok() {
    logger.info("Adding lombok to {}", project);

    declare(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME);
    declare(JavaPlugin.ANNOTATION_PROCESSOR_CONFIGURATION_NAME);
    declare(JavaPlugin.TEST_COMPILE_ONLY_CONFIGURATION_NAME);
    declare(JavaPlugin.TEST_ANNOTATION_PROCESSOR_CONFIGURATION_NAME);
  }

  private void declare(@NonNull String configurationName) {
    if (logger.isDebugEnabled()) {
      logger.debug(
          "Declaring {} on {}",
          LOMBOK_DEPENDENCY,
          project.getConfigurations().getByName(configurationName));
    }

    project.getDependencies().add(configurationName, LOMBOK_DEPENDENCY);
  }
}
