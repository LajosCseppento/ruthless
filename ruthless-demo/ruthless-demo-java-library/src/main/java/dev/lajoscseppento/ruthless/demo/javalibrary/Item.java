package dev.lajoscseppento.ruthless.demo.javalibrary;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/** Represents an item. */
@Jacksonized
@Builder
@Value
public class Item {
  @NonNull String name;
  @NonNull String description;
}
