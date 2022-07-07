package dev.lajoscseppento.ruthless.plugin.util.impl;

import javax.annotation.Nullable;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class SystemProperty<T> {
  @Getter @NonNull private final String key;
  @Nullable private final T defaultValue;

  protected T getValueOrDefault() {
    String value = System.getProperty(key);
    return value == null ? defaultValue : parse(value);
  }

  protected abstract T parse(@NonNull String value);
}
