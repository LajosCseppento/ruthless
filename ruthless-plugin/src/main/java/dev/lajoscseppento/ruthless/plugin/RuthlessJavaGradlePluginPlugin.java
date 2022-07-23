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

    configureJacocoCoverage();
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

  private void configureJacocoCoverage() {
    //// Set up JaCoCo coverage for Gradle TestKit tests
    //    val functionalTest = tasks.named("functionalTest")
    //    val jacocoTestReport = tasks.named("jacocoTestReport")
    //
    //    functionalTest.configure {
    //      finalizedBy(jacocoTestReport)
    //
    //      // See https://github.com/koral--/jacoco-gradle-testkit-plugin/issues/9
    //      doLast {
    //        val jacocoTestExec =
    // checkNotNull(extensions.getByType(JacocoTaskExtension::class).destinationFile)
    //        val delayMs = 1000L
    //        val intervalMs = 200L
    //        val maxRetries = 50
    //        var retries = 0
    //
    //        TimeUnit.MILLISECONDS.sleep(delayMs) // Linux
    //
    //        while (!(jacocoTestExec.exists() && jacocoTestExec.renameTo(jacocoTestExec))) { //
    // Windows
    //          if (retries >= maxRetries) {
    //            val waitTime = delayMs + intervalMs * retries
    //            throw GradleException("$jacocoTestExec.name is not ready, waited at least
    // $waitTime ms")
    //          }
    //
    //          retries++
    //          logger.info("Waiting $intervalMs ms for $jacocoTestExec to be ready, try
    // #$retries...")
    //          TimeUnit.MILLISECONDS.sleep(intervalMs)
    //        }
    //
    //        logger.info("$jacocoTestExec is ready")
    //      }
    //    }
    //
    //    jacocoTestReport.configure {
    //      dependsOn(functionalTest)
    //      (this as JacocoReport).executionData.from(buildDir.absolutePath +
    // "/jacoco/functionalTest.exec")
    //    }
    //
    //    tasks.named("compileFunctionalTestJava").configure {
    //      dependsOn("generateJacocoFunctionalTestKitProperties")
    //    }
    //
    //    jacocoTestKit {
    //      applyTo("functionalTestImplementation", functionalTest)
    //    }
  }
}
