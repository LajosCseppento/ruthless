package dev.lajoscseppento.ruthless.plugin.configuration.impl;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Builder
@Data
@Jacksonized
public class GroupIdArtifactId {
  private final String groupId;
  private final String artifactId;

  public String toDependencyNotation() {
    return String.format("%s:%s", groupId, artifactId);
  }
}
