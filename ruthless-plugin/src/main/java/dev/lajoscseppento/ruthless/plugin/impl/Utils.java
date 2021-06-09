package dev.lajoscseppento.ruthless.plugin.impl;

import lombok.experimental.UtilityClass;

/** The must-have blob. */
@UtilityClass
class Utils {
  private final String UNSPECIFIED = "unspecified";

  boolean isUnspecified(Object value) {
    return value == null || value.toString().isBlank() || UNSPECIFIED.equals(value.toString());
  }
}
