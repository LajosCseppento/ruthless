package dev.lajoscseppento.ruthless.plugin.configuration.impl;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/** Represents a GA. */
@Builder
@Data
@Jacksonized
public class GroupIdArtifactId {
  private final String groupId;
  private final String artifactId;

  /**
   * Formats the GA as dependency notation.
   *
   * @return the GA as dependency notation
   */
  public String toDependencyNotation() {
    return String.format("%s:%s", groupId, artifactId);
  }
}
