package dev.lajoscseppento.ruthless.plugin.impl;

import dev.lajoscseppento.gradle.plugin.common.impl.Utils;
import dev.lajoscseppento.ruthless.plugin.extension.RuthlessExtension;
import dev.lajoscseppento.ruthless.plugin.extension.impl.RuthlessExtensionImpl;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.language.base.plugins.LifecycleBasePlugin;
import org.sonarqube.gradle.SonarQubeExtension;
import org.sonarqube.gradle.SonarQubePlugin;

public class RuthlessBasePlugin extends AbstractProjectPlugin {
  @Override
  public void apply() {
    if (!gradle.getPlugins().hasPlugin(RuthlessGradlePlugin.class)) {
      throw new GradleException(
          "Please apply ruthless in settings.gradle[.kts] for the full experience");
    }

    logger.info("Adding extension to {}", project);
    configureExtension();

    logger.info("Configuring Sonar Analysis on {}", project);
    configureSonar();

    project.afterEvaluate(proj -> afterEvaluate());
  }

  private void configureExtension() {
    RuthlessExtension ruthless = new RuthlessExtensionImpl(project);
    extensions.add(RuthlessExtension.class, "ruthless", ruthless);
  }

  @Override
  protected List<Class<? extends Plugin<Project>>> requiredPlugins() {
    return Arrays.asList(BasePlugin.class);
  }

  private void configureSonar() {
    if (project.equals(project.getRootProject())) {
      project.getPluginManager().apply(SonarQubePlugin.class);
    }

    TaskProvider<Task> sonarqubeTask =
        project.getRootProject().getTasks().named(SonarQubeExtension.SONARQUBE_TASK_NAME);

    tasks.withType(
        Task.class,
        task -> {
          if (task.getName().equals(LifecycleBasePlugin.CHECK_TASK_NAME)) {
            sonarqubeTask.configure(sqTask -> sqTask.dependsOn(task));
          }
        });

    logger.lifecycle("B");
    logger.lifecycle("{}", sourceSets);
    if (sourceSets != null) {
      // TODO this is null, since it's context there is no java plugin yet
      SonarQubeExtension sonarqube =
          (SonarQubeExtension)
              project.getExtensions().getByName(SonarQubeExtension.SONARQUBE_EXTENSION_NAME);

      logger.lifecycle("A");
      sonarqube.properties(
          sqProperties -> {
            logger.lifecycle("Z");
            Collection<?> sonarTests =
                (Collection<?>) sqProperties.getProperties().get("sonar.tests");
            List<Object> newSonarTests = new ArrayList<>(sonarTests);

            sourceSets.withType(
                SourceSet.class,
                sourceSet -> {
                  logger.lifecycle(sourceSet.getName());
                  if (sourceSet.getName().endsWith("Test")) {
                    sourceSet.getAllSource().getSrcDirs().stream()
                        .filter(File::exists)
                        .forEach(newSonarTests::add);
                  }
                });

            if (sonarTests.size() != newSonarTests.size()) {
              sqProperties.property("sonar.tests", newSonarTests);
              logger.lifecycle("{}",newSonarTests);
            }
          });
    }
  }

  private void afterEvaluate() {
    if (Utils.isUnspecified(project.getGroup())) {
      String msg =
          String.format(
              "Group of %s is unspecified - you might want to put it in gradle.properties",
              project);
      throw new GradleException(msg);
    } else if (Utils.isUnspecified(project.getVersion())) {
      String msg =
          String.format(
              "Version of %s is unspecified - you might want to put it in gradle.properties",
              project);
      throw new GradleException(msg);
    }
  }
}
