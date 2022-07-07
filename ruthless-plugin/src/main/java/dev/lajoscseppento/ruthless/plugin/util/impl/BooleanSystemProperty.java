package dev.lajoscseppento.ruthless.plugin.util.impl;

import lombok.NonNull;

public class BooleanSystemProperty extends SystemProperty<Boolean> {

  public BooleanSystemProperty(@NonNull String key, boolean defaultValue) {
    super(key, defaultValue);
  }

  public boolean get() {
    return super.getValueOrDefault();
  }

  @Override
  protected Boolean parse(@NonNull String value) {
    switch (value.trim().toLowerCase()) {
      case "true":
      case "1":
        return true;

      case "false":
      case "0":
        return false;

      default:
        throw new IllegalArgumentException(
            "Cannot parse value of " + getKey() + " as boolean: " + value);
    }
  }
}
