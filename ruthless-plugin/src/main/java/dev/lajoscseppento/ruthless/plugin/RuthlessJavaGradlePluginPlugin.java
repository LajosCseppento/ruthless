package dev.lajoscseppento.ruthless.plugin;

import dev.lajoscseppento.ruthless.plugin.impl.AbstractProjectPlugin;
import dev.lajoscseppento.ruthless.plugin.impl.RuthlessJavaBasePlugin;
import java.util.Arrays;
import java.util.List;
import lombok.NonNull;
import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.jvm.JvmTestSuite;
import org.gradle.api.plugins.jvm.JvmTestSuiteTarget;
import org.gradle.language.base.plugins.LifecycleBasePlugin;
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension;
import org.gradle.plugin.devel.plugins.JavaGradlePluginPlugin;
import org.gradle.testing.base.TestingExtension;

/** Ruthless convention plugin for Gradle plugins. */
public class RuthlessJavaGradlePluginPlugin extends AbstractProjectPlugin {
  private GradlePluginDevelopmentExtension gradlePlugin;

  @Override
  protected List<Class<? extends Plugin<Project>>> requiredPlugins() {
    return Arrays.asList(RuthlessJavaBasePlugin.class, JavaGradlePluginPlugin.class);
  }

  @Override
  protected void apply() {
    gradlePlugin = (GradlePluginDevelopmentExtension) extensions.getByName("gradlePlugin");
    TestingExtension testing = (TestingExtension) extensions.getByName("testing");

    NamedDomainObjectProvider<JvmTestSuite> functionalTestSuite =
        testing
            .getSuites()
            .register("functionalTest", JvmTestSuite.class, this::configureFunctionalTestSuite);

    tasks
        .named(LifecycleBasePlugin.CHECK_TASK_NAME)
        .configure(checkTask -> checkTask.dependsOn(functionalTestSuite));
  }

  private void configureFunctionalTestSuite(@NonNull JvmTestSuite functionalTest) {
    gradlePlugin.testSourceSets(functionalTest.getSources());

    functionalTest.getTargets().all(this::configureFunctionalTestSuiteTarget);
  }

  private void configureFunctionalTestSuiteTarget(@NonNull JvmTestSuiteTarget target) {
    target
        .getTestTask()
        .configure(testTask -> testTask.shouldRunAfter(tasks.named(JavaPlugin.TEST_TASK_NAME)));
  }
}
