package dev.lajoscseppento.ruthless.plugin.impl;

import dev.lajoscseppento.gradle.plugin.common.impl.Utils;
import dev.lajoscseppento.ruthless.plugin.RuthlessExtension;
import org.gradle.api.GradleException;

public class RuthlessBasePlugin extends AbstractProjectPlugin {
  @Override
  public void apply() {
    if (!gradle.getPlugins().hasPlugin(RuthlessGradlePlugin.class)) {
      throw new GradleException(
          "Please apply ruthless in settings.gradle[.kts] for the full experience");
    }

    logger.info("[ruthless] Adding extension to {}", project);
    configureExtension();

    if (project.equals(project.getRootProject())) {
      logger.info("[ruthless] Configuring sonar on {}", project);
      configureSonar();
    }

    project.afterEvaluate(proj -> afterEvaluate());
  }

  private void configureExtension() {
    RuthlessExtension ruthless = new RuthlessExtensionImpl(project);
    extensions.add(RuthlessExtension.class, "ruthless", ruthless);
  }

  private void configureSonar() {
    // TODO
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
