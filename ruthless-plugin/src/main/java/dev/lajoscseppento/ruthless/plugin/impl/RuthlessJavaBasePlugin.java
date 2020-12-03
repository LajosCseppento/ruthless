package dev.lajoscseppento.ruthless.plugin.impl;

import com.diffplug.gradle.spotless.SpotlessExtension;
import com.diffplug.gradle.spotless.SpotlessPlugin;
import java.util.Arrays;
import java.util.List;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.DependencyResolveDetails;
import org.gradle.api.artifacts.ModuleVersionSelector;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.testing.Test;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.gradle.testing.jacoco.plugins.JacocoPlugin;
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension;

public class RuthlessJavaBasePlugin extends AbstractProjectPlugin {

  @Override
  protected List<Class<? extends Plugin<Project>>> requiredPlugins() {
    return Arrays.asList(
        RuthlessBasePlugin.class, JavaPlugin.class, JacocoPlugin.class, SpotlessPlugin.class);
  }

  @Override
  public void apply() {
    repositories.add(repositories.mavenCentral());

    java.getToolchain().getLanguageVersion().set(JavaLanguageVersion.of(11));

    configureBoms();
    configureDefaultDependencyResolution();
    configureTest();
    configureSpotless();
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
          if (javaConfigs.contains(configuration.getName())) {
            dependencies.add(
                configuration.getName(),
                dependencies.platform("com.fasterxml.jackson:jackson-bom"));
            dependencies.add(configuration.getName(), dependencies.platform("org.junit:junit-bom"));
          }
        });
  }

  private void resolveDefaultDependency(Configuration configuration, DependencyResolveDetails dep) {
    // TODO use constraints?
    // https://docs.gradle.org/current/userguide/single_versions.html#sec:declaring_without_version
    ModuleVersionSelector requested = dep.getRequested();

    if (Utils.isUnspecified(requested.getVersion())) {
      String requestedGA = String.format("%s:%s", requested.getGroup(), requested.getName());
      String defaultVersion = Utils.DEFAULT_DEPENDENCIES.get(requestedGA);

      if (defaultVersion != null) {
        logger.info(
            "[ruthless] Defaulting {} to {} on {}", requestedGA, defaultVersion, configuration);
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
            dependencies.add(name, "org.junit.jupiter:junit-jupiter");
            dependencies.add(name, "org.assertj:assertj-core");
          }
        });

    tasks.withType(Test.class, Test::useJUnitPlatform);

    JacocoPluginExtension jacoco = project.getExtensions().getByType(JacocoPluginExtension.class);
    jacoco.setToolVersion("0.8.6");

    Task jacocoTestReportTask = tasks.getByName("jacocoTestReport");
    Task testTask = tasks.getByName(JavaPlugin.TEST_TASK_NAME);
    testTask.finalizedBy(jacocoTestReportTask);
    jacocoTestReportTask.dependsOn(testTask);
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
