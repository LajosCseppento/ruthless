package dev.lajoscseppento.ruthless.plugin.configuration.impl;

import java.util.Objects;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/** Represents a GAV. */
@Builder
@Data
@Jacksonized
public class GroupIdArtifactIdVersion {
  private final String groupId;
  private final String artifactId;
  private final String version;

  /**
   * Checks if the GAV matches the given GA.
   *
   * @param matchGroupId the group ID to match
   * @param matchArtifactId the artifact ID to match
   * @return true if the GAV matches the given GA
   */
  public boolean matches(String matchGroupId, String matchArtifactId) {
    return Objects.equals(groupId, matchGroupId) && Objects.equals(artifactId, matchArtifactId);
  }

  /**
   * Formats the GAV as dependency notation.
   *
   * @return the GAV as dependency notation
   */
  public String toDependencyNotation() {
    return String.format("%s:%s:%s", groupId, artifactId, version);
  }
}
