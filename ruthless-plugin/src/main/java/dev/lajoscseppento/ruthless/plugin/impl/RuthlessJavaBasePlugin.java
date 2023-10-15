package dev.lajoscseppento.ruthless.plugin.impl;

import com.diffplug.gradle.spotless.SpotlessExtension;
import com.diffplug.gradle.spotless.SpotlessPlugin;
import dev.lajoscseppento.gradle.plugin.common.impl.Utils;
import dev.lajoscseppento.ruthless.plugin.configuration.impl.GroupIdArtifactIdVersion;
import dev.lajoscseppento.ruthless.plugin.configuration.impl.RuthlessConfiguration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.DependencyResolveDetails;
import org.gradle.api.artifacts.ModuleVersionSelector;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.publish.PublicationContainer;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.VariantVersionMappingStrategy;
import org.gradle.api.publish.VersionMappingStrategy;
import org.gradle.api.publish.ivy.IvyPublication;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.plugins.PublishingPlugin;
import org.gradle.api.tasks.testing.Test;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.gradle.testing.jacoco.plugins.JacocoPlugin;
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension;
import org.gradle.testing.jacoco.tasks.JacocoReport;

/** Ruthless convention plugin for Java projects. */
public class RuthlessJavaBasePlugin extends AbstractProjectPlugin {

  private static final JavaLanguageVersionSystemProperty JAVA_LANGUAGE_VERSION =
      new JavaLanguageVersionSystemProperty();

  @Override
  protected List<Class<? extends Plugin<Project>>> requiredPlugins() {
    return Arrays.asList(
        RuthlessBasePlugin.class, JavaPlugin.class, JacocoPlugin.class, SpotlessPlugin.class);
  }

  @Override
  public void apply() {
    repositories.add(repositories.mavenCentral());

    configureToolchain();
    java.withJavadocJar();
    java.withSourcesJar();

    configureBoms();
    configureDefaultDependencyResolution();
    configureTest();
    configurePublishing();
    configureSpotless();
  }

  private void configureToolchain() {
    JavaLanguageVersion version = JAVA_LANGUAGE_VERSION.get();

    logger.info("Setting Java toolchain language version to {} on {}", version, project);
    java.getToolchain().getLanguageVersion().set(version);
  }

  private void configureDefaultDependencyResolution() {
    // BOM is too mainstream and would need a separate project
    configurations.all(
        configuration ->
            configuration
                .getResolutionStrategy()
                .eachDependency(dep -> resolveDefaultDependency(configuration, dep)));
  }

  private void configureBoms() {
    List<String> javaConfigs =
        Arrays.asList(
            JavaPlugin.API_CONFIGURATION_NAME,
            JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME,
            JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME,
            "functionalTestImplementation");

    configurations.all(
        configuration -> {
          String name = configuration.getName();
          if (javaConfigs.contains(name)) {
            declarePlatformDependencies(
                name, RuthlessConfiguration.INSTANCE.getPlatformDependencies());
          }
        });
  }

  private void resolveDefaultDependency(
      @NonNull Configuration configuration, @NonNull DependencyResolveDetails dep) {
    ModuleVersionSelector requested = dep.getRequested();

    if (Utils.isUnspecified(requested.getVersion())) {
      Optional<String> defaultVersionOpt =
          RuthlessConfiguration.INSTANCE.getDefaultDependencies().stream()
              .filter(d -> d.matches(requested.getGroup(), requested.getName()))
              .map(GroupIdArtifactIdVersion::getVersion)
              .findAny();

      if (defaultVersionOpt.isPresent()) {
        String defaultVersion = defaultVersionOpt.get();
        logger.info(
            "Defaulting {}:{} to {} on {}",
            requested.getGroup(),
            requested.getName(),
            defaultVersion,
            configuration);
        dep.useVersion(defaultVersion);
      }
    }
  }

  private void configureTest() {
    List<String> javaTestConfigs =
        Arrays.asList(
            JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME, "functionalTestImplementation");

    configurations.all(
        configuration -> {
          String name = configuration.getName();
          if (javaTestConfigs.contains(name)) {
            declareDependencies(name, RuthlessConfiguration.INSTANCE.getTestDependencies());
          }
        });

    tasks.withType(Test.class, Test::useJUnitPlatform);

    JacocoPluginExtension jacoco = project.getExtensions().getByType(JacocoPluginExtension.class);
    jacoco.setToolVersion(RuthlessConfiguration.INSTANCE.getJacocoVersion());

    JacocoReport jacocoTestReportTask = (JacocoReport) tasks.getByName("jacocoTestReport");
    jacocoTestReportTask.getReports().getXml().getRequired().set(true);

    Task testTask = tasks.getByName(JavaPlugin.TEST_TASK_NAME);
    testTask.finalizedBy(jacocoTestReportTask);
    jacocoTestReportTask.dependsOn(testTask);
  }

  private void configurePublishing() {
    project
        .getPlugins()
        .withType(
            PublishingPlugin.class,
            publishingPlugin -> {
              PublishingExtension publishing =
                  (PublishingExtension) extensions.getByName("publishing");
              PublicationContainer publications = publishing.getPublications();

              publications.withType(
                  IvyPublication.class,
                  ivyPublication ->
                      ivyPublication.versionMapping(
                          this::configurePublishingVersionMappingStrategy));

              publications.withType(
                  MavenPublication.class,
                  mavenPublication ->
                      mavenPublication.versionMapping(
                          this::configurePublishingVersionMappingStrategy));
            });
  }

  private void configurePublishingVersionMappingStrategy(@NonNull VersionMappingStrategy strategy) {
    strategy.allVariants(VariantVersionMappingStrategy::fromResolutionResult);
  }

  private void configureSpotless() {
    SpotlessExtension spotless = (SpotlessExtension) extensions.getByName("spotless");

    spotless.java(
        java -> {
          java.removeUnusedImports();
          java.googleJavaFormat();
        });
  }
}
