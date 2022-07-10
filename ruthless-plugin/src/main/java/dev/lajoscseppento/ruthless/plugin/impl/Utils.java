package dev.lajoscseppento.ruthless.plugin.impl;

import lombok.experimental.UtilityClass;

/** The must-have blob. */
@UtilityClass
class Utils {
  private final String UNSPECIFIED = "unspecified";

  boolean isUnspecified(Object value) {
    if (value == null) {
      return true;
    } else {
      String valueStr = value.toString().trim();
      return valueStr.isEmpty() || UNSPECIFIED.equals(valueStr);
    }
  }
}
