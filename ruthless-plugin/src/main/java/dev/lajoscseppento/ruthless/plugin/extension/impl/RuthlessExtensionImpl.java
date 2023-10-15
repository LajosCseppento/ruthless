package dev.lajoscseppento.ruthless.plugin.extension.impl;

import dev.lajoscseppento.ruthless.plugin.extension.LombokSpec;
import dev.lajoscseppento.ruthless.plugin.extension.RuthlessExtension;
import lombok.Getter;
import lombok.NonNull;
import org.gradle.api.Action;
import org.gradle.api.Project;

/** Implementation of {@link RuthlessExtension}. */
public class RuthlessExtensionImpl implements RuthlessExtension {
  @Getter private final LombokSpec lombok;

  /**
   * Constructor.
   *
   * @param project the {@link Project} object
   */
  public RuthlessExtensionImpl(@NonNull Project project) {
    this.lombok = new LombokSpecImpl(project);
  }

  @Override
  public void lombok(@NonNull Action<? super LombokSpec> action) {
    action.execute(lombok);
  }
}
