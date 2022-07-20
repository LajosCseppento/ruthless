package dev.lajoscseppento.ruthless.plugin.extension.impl;

import dev.lajoscseppento.ruthless.plugin.extension.LombokSpec;
import dev.lajoscseppento.ruthless.plugin.logging.RuthlessLogger;
import lombok.Getter;
import lombok.NonNull;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.provider.Property;

class LombokSpecImpl implements LombokSpec {
  private static final String LOMBOK_DEPENDENCY = "org.projectlombok:lombok";

  private final RuthlessLogger logger;
  private final Dependency lombokDependency;
  @Getter private final Property<Boolean> enabled;

  LombokSpecImpl(@NonNull Project project) {
    this.logger = RuthlessLogger.create(project.getLogger(), "ruthless-lombok");
    this.lombokDependency = project.getDependencies().create(LOMBOK_DEPENDENCY);
    this.enabled = project.getObjects().property(boolean.class).convention(true);

    project
        .getConfigurations()
        .withType(
            Configuration.class,
            configuration -> {
              String configurationName = configuration.getName();
              if (configurationName.equals(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME)
                  || configurationName.equals(JavaPlugin.ANNOTATION_PROCESSOR_CONFIGURATION_NAME)
                  || configurationName.equals(JavaPlugin.TEST_COMPILE_ONLY_CONFIGURATION_NAME)
                  || configurationName.equals(
                      JavaPlugin.TEST_ANNOTATION_PROCESSOR_CONFIGURATION_NAME)
                  || configurationName.endsWith("TestCompileOnly")
                  || configurationName.endsWith("TestAnnotationProcessor")) {
                declareDependency(configuration);
              }
            });
  }

  private void declareDependency(@NonNull Configuration configuration) {
    configuration
        .getDependencies()
        .addLater(
            enabled.map(
                value -> {
                  enabled.finalizeValue();

                  if (Boolean.TRUE.equals(value)) {
                    if (logger.isDebugEnabled()) {
                      logger.debug("Declaring {} on {}", LOMBOK_DEPENDENCY, configuration);
                    }

                    return lombokDependency;
                  } else {
                    return null;
                  }
                }));
  }
}
