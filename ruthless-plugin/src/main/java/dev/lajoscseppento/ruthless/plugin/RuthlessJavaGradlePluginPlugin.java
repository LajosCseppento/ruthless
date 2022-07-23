package dev.lajoscseppento.ruthless.plugin;

import dev.lajoscseppento.ruthless.plugin.impl.AbstractProjectPlugin;
import dev.lajoscseppento.ruthless.plugin.impl.RuthlessJavaBasePlugin;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import org.gradle.api.GradleException;
import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.jvm.JvmTestSuite;
import org.gradle.api.plugins.jvm.JvmTestSuiteTarget;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.testing.Test;
import org.gradle.language.base.plugins.LifecycleBasePlugin;
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension;
import org.gradle.plugin.devel.plugins.JavaGradlePluginPlugin;
import org.gradle.testing.base.TestingExtension;
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension;
import org.gradle.testing.jacoco.tasks.JacocoReport;
import pl.droidsonroids.gradle.jacoco.testkit.JacocoTestKitExtension;

public class RuthlessJavaGradlePluginPlugin extends AbstractProjectPlugin {

  private static final String TESTING_EXTENSION_NAME = "testing";

  private GradlePluginDevelopmentExtension gradlePlugin;

  @Override
  protected List<Class<? extends Plugin<Project>>> requiredPlugins() {
    return Arrays.asList(RuthlessJavaBasePlugin.class, JavaGradlePluginPlugin.class);
  }

  @Override
  protected void apply() {
    repositories.gradlePluginPortal();

    gradlePlugin = (GradlePluginDevelopmentExtension) extensions.getByName("gradlePlugin");
    TestingExtension testing = (TestingExtension) extensions.getByName(TESTING_EXTENSION_NAME);

    NamedDomainObjectProvider<JvmTestSuite> functionalTestSuite =
        testing
            .getSuites()
            .register("functionalTest", JvmTestSuite.class, this::configureFunctionalTestSuite);

    tasks
        .named(LifecycleBasePlugin.CHECK_TASK_NAME)
        .configure(checkTask -> checkTask.dependsOn(functionalTestSuite));

    configureTestKitCoverage();
  }

  private void configureFunctionalTestSuite(@NonNull JvmTestSuite suite) {
    gradlePlugin.testSourceSets(suite.getSources());

    suite.getTargets().all(this::configureFunctionalTestSuiteTarget);
  }

  private void configureFunctionalTestSuiteTarget(@NonNull JvmTestSuiteTarget target) {
    target
        .getTestTask()
        .configure(testTask -> testTask.shouldRunAfter(tasks.named(JavaPlugin.TEST_TASK_NAME)));
  }

  private void configureTestKitCoverage() {
    TaskProvider<Test> functionalTest = tasks.named("functionalTest", Test.class);
    TaskProvider<JacocoReport> jacocoTestReport =
        tasks.named("jacocoTestReport", JacocoReport.class);

    functionalTest.configure(
        task -> {
          task.finalizedBy(jacocoTestReport);

          // See https://github.com/koral--/jacoco-gradle-testkit-plugin/issues/9
          task.doLast(
              t -> {
                JacocoTaskExtension jacoco =
                    (JacocoTaskExtension) t.getExtensions().getByName("jacoco");
                File jacocoTestExec = jacoco.getDestinationFile();

                int delayMs = 1000;
                int intervalMs = 200;
                int maxRetries = 50;
                int retries = 0;

                // Linux
                sleepMs(delayMs);

                // Windows
                while (!(jacocoTestExec.exists() && jacocoTestExec.renameTo(jacocoTestExec))) {
                  if (retries >= maxRetries) {
                    int waitTime = delayMs + intervalMs * retries;
                    throw new GradleException(
                        jacocoTestExec + " is not ready, waited at least " + waitTime + " ms");
                  }

                  retries++;
                  logger.info(
                      "Waiting {} ms for {} to be ready, try #{}...",
                      intervalMs,
                      jacocoTestExec,
                      retries);
                  sleepMs(intervalMs);
                }

                logger.info("{} is ready", jacocoTestExec);
              });
        });

    jacocoTestReport.configure(
        t -> {
          t.dependsOn(functionalTest);
          t.getExecutionData()
              .from(project.getBuildDir().getAbsolutePath() + "/jacoco/functionalTest.exec");
        });

    tasks
        .named("compileFunctionalTestJava")
        .configure(t -> t.dependsOn("generateJacocoFunctionalTestKitProperties"));

    // TODO cast 2nd arg
    ((JacocoTestKitExtension) extensions.getByName("jacocoTestKit"))
        .applyTo("functionalTestImplementation", (TaskProvider) functionalTest);
  }

  private void sleepMs(long timeout) {
    try {
      TimeUnit.MILLISECONDS.sleep(timeout);
    } catch (InterruptedException ex) {
      logger.warn("Sleep interrupted");
      Thread.currentThread().interrupt();
    }
  }
}
