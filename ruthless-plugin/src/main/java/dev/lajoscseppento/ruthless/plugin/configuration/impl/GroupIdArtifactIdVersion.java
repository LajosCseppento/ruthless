package dev.lajoscseppento.ruthless.plugin.configuration.impl;

import java.util.Objects;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Builder
@Data
@Jacksonized
public class GroupIdArtifactIdVersion {
  private final String groupId;
  private final String artifactId;
  private final String version;

  public boolean matches(String matchGroupId, String matchArtifactId) {
    return Objects.equals(groupId, matchGroupId) && Objects.equals(artifactId, matchArtifactId);
  }

  public String toDependencyNotation() {
    return String.format("%s:%s:%s", groupId, artifactId, version);
  }
}
