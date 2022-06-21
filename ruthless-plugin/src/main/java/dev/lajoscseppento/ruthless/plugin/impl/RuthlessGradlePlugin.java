package dev.lajoscseppento.ruthless.plugin.impl;

import lombok.NonNull;
import org.gradle.api.Plugin;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.invocation.Gradle;

public class RuthlessGradlePlugin implements Plugin<Gradle> {
  @Override
  public void apply(@NonNull Gradle gradle) {
    gradle.settingsEvaluated(
        settings -> settings.getPluginManager().apply(RuthlessSettingsPlugin.class));

    // For spotless
    gradle.allprojects(
        project -> {
          RepositoryHandler repositories = project.getBuildscript().getRepositories();
          repositories.add(repositories.mavenCentral());
        });
  }
}
