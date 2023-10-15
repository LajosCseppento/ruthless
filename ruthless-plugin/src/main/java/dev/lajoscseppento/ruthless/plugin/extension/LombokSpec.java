package dev.lajoscseppento.ruthless.plugin.extension;

import org.gradle.api.provider.Property;

/** Interface for <code>ruthless.lombok</code>. */
public interface LombokSpec {
  /**
   * Returns the <code>ruthless.lombok.enabled</code> property.
   *
   * @return the <code>ruthless.lombok.enabled</code> property
   */
  Property<Boolean> getEnabled();
}
