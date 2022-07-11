package dev.lajoscseppento.ruthless.plugin;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.gradle.api.Project;
import org.gradle.api.internal.project.DefaultProject;
import org.gradle.api.provider.Provider;
import org.gradle.build.event.BuildEventsListenerRegistry;
import org.gradle.internal.service.DefaultServiceRegistry;
import org.gradle.internal.service.scopes.ProjectScopeServices;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.tooling.events.OperationCompletionListener;

@UtilityClass
@SuppressWarnings("UnstableApiUsage")
public class TestUtils {
  public Project createProject() {
    Project project = ProjectBuilder.builder().build();
    TestUtils.addFakeServices(project);
    project.setGroup("test");
    project.setVersion("0.0.0-SNAPSHOT");
    return project;
  }

  // Similar issue as reported in:
  // - https://github.com/gradle/gradle/issues/17783
  // - https://github.com/gradle/gradle/issues/16774
  //
  // Workaround from https://issuetracker.google.com/issues/193859160#comment2
  private void addFakeServices(@NonNull Project project) {
    try {
      ProjectScopeServices gss = (ProjectScopeServices) ((DefaultProject) project).getServices();

      Field state = ProjectScopeServices.class.getSuperclass().getDeclaredField("state");
      state.setAccessible(true);
      @SuppressWarnings("unchecked")
      AtomicReference<Object> stateValue = (AtomicReference<Object>) state.get(gss);
      Class<?> enumClass = Class.forName(DefaultServiceRegistry.class.getName() + "$State");
      stateValue.set(enumClass.getEnumConstants()[0]);

      // Add service and set state so that future mutations are not allowed
      gss.add(BuildEventsListenerRegistry.class, new FakeBuildEventsListenerRegistry());
      stateValue.set(enumClass.getEnumConstants()[1]);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  private final class FakeBuildEventsListenerRegistry implements BuildEventsListenerRegistry {
    @Override
    public void onTaskCompletion(Provider<? extends OperationCompletionListener> provider) {}
  }
}
