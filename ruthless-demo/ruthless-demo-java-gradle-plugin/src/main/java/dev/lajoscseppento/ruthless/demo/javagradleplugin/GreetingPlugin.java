package dev.lajoscseppento.ruthless.demo.javagradleplugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/** Mock plugin, inspired by Gradle init. */
public class GreetingPlugin implements Plugin<Project> {
  public void apply(Project project) {
    project
        .getTasks()
        .register(
            "greeting",
            task ->
                task.doLast(
                    s ->
                        project
                            .getLogger()
                            .quiet("Greetings from ruthless-demo-java-gradle-plugin!")));
  }
}
