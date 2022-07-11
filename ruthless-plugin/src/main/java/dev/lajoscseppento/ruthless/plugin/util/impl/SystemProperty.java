package dev.lajoscseppento.ruthless.plugin.util.impl;

import dev.lajoscseppento.ruthless.plugin.impl.Utils;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.NonNull;

public abstract class SystemProperty<T> {
  @Getter private final String key;
  private final T defaultValue;
  private final Supplier<T> defaultValueSupplier;

  protected SystemProperty(@NonNull String key, T defaultValue) {
    this.key = key;
    this.defaultValue = defaultValue;
    this.defaultValueSupplier = null;
  }

  protected SystemProperty(@NonNull String key, @NonNull Supplier<T> defaultValueSupplier) {
    this.key = key;
    this.defaultValue = null;
    this.defaultValueSupplier = defaultValueSupplier;
  }

  protected T getValueOrDefault() {
    String value = Utils.trimToNull(System.getProperty(key));

    if (value == null) {
      return defaultValueSupplier == null ? defaultValue : defaultValueSupplier.get();
    } else {
      return parse(value);
    }
  }

  protected abstract T parse(@NonNull String value);
}
