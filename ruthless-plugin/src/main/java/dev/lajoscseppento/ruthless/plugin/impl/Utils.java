package dev.lajoscseppento.ruthless.plugin.impl;

import javax.annotation.Nullable;
import lombok.experimental.UtilityClass;

/** The must-have blob. */
@UtilityClass
public class Utils {
  private final String UNSPECIFIED = "unspecified";

  public boolean isUnspecified(Object value) {
    if (value == null) {
      return true;
    } else {
      String valueStr = value.toString().trim();
      return valueStr.isEmpty() || UNSPECIFIED.equals(valueStr);
    }
  }

  @Nullable
  public String trimToNull(String value) {
    if (value == null) {
      return null;
    } else {
      String str = value.trim();
      return str.isEmpty() ? null : str;
    }
  }
}
