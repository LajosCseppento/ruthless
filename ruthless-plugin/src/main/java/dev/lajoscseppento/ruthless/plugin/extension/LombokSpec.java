package dev.lajoscseppento.ruthless.plugin.extension;

import org.gradle.api.provider.Property;

/** Interface for <code>ruthless.lombok</code>. */
public interface LombokSpec {
  Property<Boolean> getEnabled();
}
