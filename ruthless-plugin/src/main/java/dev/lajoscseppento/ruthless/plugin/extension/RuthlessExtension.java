package dev.lajoscseppento.ruthless.plugin.extension;

import org.gradle.api.Action;

/** Interface for the <code>ruthless</code> extension. */
public interface RuthlessExtension {
  /**
   * Returns the <code>ruthless.lombok</code> spec.
   *
   * @return the <code>ruthless.lombok</code> spec
   */
  LombokSpec getLombok();

  /**
   * Configures the <code>ruthless.lombok</code> spec.
   *
   * @param action the configuration action
   */
  void lombok(Action<? super LombokSpec> action);
}
