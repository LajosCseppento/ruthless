package dev.lajoscseppento.ruthless.demo.javalibrary;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Value
public class Item {
  @NonNull String name;
  @NonNull String description;
}
