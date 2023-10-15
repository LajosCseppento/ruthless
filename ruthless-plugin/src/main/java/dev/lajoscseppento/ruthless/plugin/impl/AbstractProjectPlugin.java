package dev.lajoscseppento.ruthless.plugin.impl;

import dev.lajoscseppento.ruthless.plugin.configuration.impl.GroupIdArtifactId;
import dev.lajoscseppento.ruthless.plugin.logging.RuthlessLogger;
import java.util.Collections;
import java.util.List;
import lombok.NonNull;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskContainer;

/**
 * Base class to approximate buildscript DSL and facilitate writing cleaner plugin code.
 *
 * <p>Note that some fields might be null during runtime if the plugin which declares it is not
 * applied.
 *
 * <p>Note that fields are populated when {@link #apply()} is called, and they are not updated
 * later.
 */
public abstract class AbstractProjectPlugin implements Plugin<Project> {
  /** Corresponding {@link Project} object. */
  protected Project project;

  /** Corresponding {@link Gradle} object. */
  protected Gradle gradle;

  /** Ruthless logger. */
  protected RuthlessLogger logger;

  /** Quick access to <code>project.configurations</code>. */
  protected ConfigurationContainer configurations;

  /** Quick access to <code>project.dependencies</code>. */
  protected DependencyHandler dependencies;

  /** Quick access to <code>project.extensions</code>. */
  protected ExtensionContainer extensions;

  /** Quick access to <code>project.repositories</code>. */
  protected RepositoryHandler repositories;

  /** Quick access to <code>project.tasks</code>. */
  protected TaskContainer tasks;

  /** Quick access to <code>project.java</code>. */
  protected JavaPluginExtension java;

  /** Quick access to <code>project.sourceSets</code>. */
  protected SourceSetContainer sourceSets;

  @Override
  public final void apply(@NonNull Project project) {
    this.project = project;
    gradle = project.getGradle();
    logger = RuthlessLogger.create(project.getLogger(), "ruthless");

    for (Class<?> requiredPlugin : requiredPlugins()) {
      project.getPluginManager().apply(requiredPlugin);
    }

    logger.info("Applying {} to {}", getClass().getSimpleName(), project);

    configurations = project.getConfigurations();
    dependencies = project.getDependencies();
    extensions = project.getExtensions();
    repositories = project.getRepositories();
    tasks = project.getTasks();

    java = (JavaPluginExtension) extensions.findByName("java");
    sourceSets = (SourceSetContainer) extensions.findByName("sourceSets");

    apply();
  }

  /**
   * Returns the list of plugins which should be applied before executing this plugin's logic.
   *
   * @return the list of plugins which should be applied before executing this plugin's logic
   */
  protected List<Class<? extends Plugin<Project>>> requiredPlugins() {
    return Collections.emptyList();
  }

  /** Plugin logic. */
  protected void apply() {
    // default no-op
  }

  /**
   * Declares dependencies.
   *
   * @param configurationName the configuration name
   * @param dependenciesToDeclare the dependencies to declare
   */
  protected final void declareDependencies(
      String configurationName, List<GroupIdArtifactId> dependenciesToDeclare) {
    for (GroupIdArtifactId dependency : dependenciesToDeclare) {
      String dep = dependency.toDependencyNotation();
      logger.info("Declaring dependency {} on {} of {}", dep, configurationName, project);
      dependencies.add(configurationName, dep);
    }
  }

  /**
   * Declares platform dependencies.
   *
   * @param configurationName the configuration name
   * @param platformDependenciesToDeclare the platform dependencies to declare
   */
  protected final void declarePlatformDependencies(
      String configurationName, List<GroupIdArtifactId> platformDependenciesToDeclare) {
    for (GroupIdArtifactId dependency : platformDependenciesToDeclare) {
      String dep = dependency.toDependencyNotation();
      logger.info("Declaring platform dependency {} on {} of {}", dep, configurationName, project);
      dependencies.add(configurationName, dependencies.platform(dep));
    }
  }
}
