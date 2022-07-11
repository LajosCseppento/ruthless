package dev.lajoscseppento.ruthless.plugin.util.impl;

import java.util.function.Supplier;
import lombok.NonNull;

public abstract class ObjectSystemProperty<T> extends SystemProperty<T> {

  protected ObjectSystemProperty(@NonNull String key, T defaultValue) {
    super(key, defaultValue);
  }

  protected ObjectSystemProperty(@NonNull String key, @NonNull Supplier<T> defaultValueSupplier) {
    super(key, defaultValueSupplier);
  }

  public T get() {
    return super.getValueOrDefault();
  }
}
