package dev.lajoscseppento.ruthless.plugin.extension;

import org.gradle.api.Action;

/** Interface for the <code>ruthless</code> extension. */
public interface RuthlessExtension {
  LombokSpec getLombok();

  void lombok(Action<? super LombokSpec> action);
}
