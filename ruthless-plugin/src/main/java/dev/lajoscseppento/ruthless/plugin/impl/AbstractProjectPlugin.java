package dev.lajoscseppento.ruthless.plugin.impl;

import dev.lajoscseppento.ruthless.plugin.configuration.impl.GroupIdArtifactId;
import java.util.Collections;
import java.util.List;
import lombok.NonNull;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension;

/**
 * Base class to approximate buildscript DSL and facilitate writing cleaner plugin code.
 *
 * <p>Note that some fields might be null during runtime if the plugin which declares it is not
 * applied.
 *
 * <p>Note that fields are populated before {@link #apply()} is called and they are not updated
 * later.
 */
public abstract class AbstractProjectPlugin implements Plugin<Project> {
  protected Project project;
  protected Logger logger;

  protected ConfigurationContainer configurations;
  protected DependencyHandler dependencies;
  protected ExtensionContainer extensions;
  protected Gradle gradle;
  protected RepositoryHandler repositories;
  protected TaskContainer tasks;

  protected GradlePluginDevelopmentExtension gradlePlugin;
  protected JavaPluginExtension java;
  protected SourceSetContainer sourceSets;
  protected PublishingExtension publishing;

  @Override
  public final void apply(@NonNull Project project) {
    this.project = project;
    logger = project.getLogger();

    for (Class<?> requiredPlugin : requiredPlugins()) {
      project.getPluginManager().apply(requiredPlugin);
    }

    logger.info("[ruthless] Applying {} to {}", getClass().getSimpleName(), project);

    configurations = project.getConfigurations();
    dependencies = project.getDependencies();
    extensions = project.getExtensions();
    gradle = project.getGradle();
    repositories = project.getRepositories();
    tasks = project.getTasks();

    gradlePlugin = (GradlePluginDevelopmentExtension) extensions.findByName("gradlePlugin");
    java = (JavaPluginExtension) extensions.findByName("java");
    sourceSets = (SourceSetContainer) extensions.findByName("sourceSets");
    publishing = (PublishingExtension) extensions.findByName("publishing");

    apply();
  }

  /** @return list of plugins which should be applied before executing this plugin's logic */
  protected List<Class<? extends Plugin<Project>>> requiredPlugins() {
    return Collections.emptyList();
  }

  /** Plugin logic. */
  protected void apply() {
    // default no-op
  }

  protected final void declareDependencies(
      String configurationName, List<GroupIdArtifactId> dependenciesToDeclare) {
    for (GroupIdArtifactId dependency : dependenciesToDeclare) {
      dependencies.add(configurationName, dependency.toDependencyNotation());
    }
  }

  protected final void declarePlatformDependencies(
      String configurationName, List<GroupIdArtifactId> platformDependenciesToDeclare) {
    for (GroupIdArtifactId dependency : platformDependenciesToDeclare) {
      dependencies.add(configurationName, dependencies.platform(dependency.toDependencyNotation()));
    }
  }
}
