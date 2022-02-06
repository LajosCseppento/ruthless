package dev.lajoscseppento.ruthless.plugin;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;
import org.gradle.api.Project;
import org.gradle.api.internal.project.DefaultProject;
import org.gradle.api.provider.Provider;
import org.gradle.build.event.BuildEventsListenerRegistry;
import org.gradle.internal.service.DefaultServiceRegistry;
import org.gradle.internal.service.scopes.ProjectScopeServices;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.tooling.events.OperationCompletionListener;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RuthlessPluginTest {
  @ParameterizedTest
  @ValueSource(
      strings = {
        "dev.lajoscseppento.ruthless.java-application",
        "dev.lajoscseppento.ruthless.java-gradle-plugin",
        "dev.lajoscseppento.ruthless.java-library",
        "dev.lajoscseppento.ruthless.spring-boot-application",
        "dev.lajoscseppento.ruthless.spring-boot-library"
      })
  void testApply(String pluginId) {
    // Given
    Project project = ProjectBuilder.builder().build();
    addFakeService(project);
    project.setGroup("test");
    project.setVersion("0.0.0-SNAPSHOT");

    // When
    project.getGradle().getPluginManager().apply("dev.lajoscseppento.ruthless");
    project.getPlugins().apply(pluginId);

    // Then
    assertThat(project.getPluginManager().hasPlugin("java")).isTrue();
  }

  // Similar issue as reported in:
  // - https://github.com/gradle/gradle/issues/17783
  // - https://github.com/gradle/gradle/issues/16774
  //
  // Workaround from https://issuetracker.google.com/issues/193859160#comment2
  private static void addFakeService(Project project) {
    try {
      ProjectScopeServices gss = (ProjectScopeServices) ((DefaultProject) project).getServices();

      Field state = ProjectScopeServices.class.getSuperclass().getDeclaredField("state");
      state.setAccessible(true);
      AtomicReference<Object> stateValue = (AtomicReference<Object>) state.get(gss);
      Class<?> enumClass = Class.forName(DefaultServiceRegistry.class.getName() + "$State");
      stateValue.set(enumClass.getEnumConstants()[0]);

      // add service and set state so that future mutations are not allowed
      gss.add(BuildEventsListenerRegistry.class, new FakeBuildEventsListenerRegistry());
      stateValue.set(enumClass.getEnumConstants()[1]);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  static class FakeBuildEventsListenerRegistry implements BuildEventsListenerRegistry {
    @Override
    public void onTaskCompletion(Provider<? extends OperationCompletionListener> provider) {}
  }
}
