package dev.lajoscseppento.ruthless.plugin;

import dev.lajoscseppento.ruthless.plugin.impl.AbstractProjectPlugin;
import dev.lajoscseppento.ruthless.plugin.impl.RuthlessJavaBasePlugin;
import java.util.Arrays;
import java.util.List;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.testing.Test;
import org.gradle.language.base.plugins.LifecycleBasePlugin;
import org.gradle.plugin.devel.plugins.JavaGradlePluginPlugin;

public class RuthlessJavaGradlePluginPlugin extends AbstractProjectPlugin {
  @Override
  protected List<Class<? extends Plugin<Project>>> requiredPlugins() {
    return Arrays.asList(RuthlessJavaBasePlugin.class, JavaGradlePluginPlugin.class);
  }

  @Override
  protected void apply() {
    SourceSet functionalTestSourceSet = sourceSets.create("functionalTest");
    gradlePlugin.testSourceSets(functionalTestSourceSet);

    Test functionalTest =
        tasks.create(
            "functionalTest",
            Test.class,
            task -> {
              task.setTestClassesDirs(functionalTestSourceSet.getOutput().getClassesDirs());
              task.setClasspath(functionalTestSourceSet.getRuntimeClasspath());
            });

    tasks.getByName(LifecycleBasePlugin.CHECK_TASK_NAME).dependsOn(functionalTest);
  }
}
