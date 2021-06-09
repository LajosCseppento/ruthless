package dev.lajoscseppento.ruthless.plugin.configuration.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/** Ruthless configuration root. */
@Builder
@Data
@Jacksonized
public final class RuthlessConfiguration {
  public static final RuthlessConfiguration INSTANCE;

  static {
    // Configuration
    try {
      ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
      INSTANCE =
              mapper.readValue(
                      RuthlessConfiguration.class.getResource("/configuration.yml"),
                      RuthlessConfiguration.class);
    } catch (Exception ex) {
      throw new AssertionError("Failed to load configuration: " + ex.getMessage(), ex);
    }
  }

  private final String minimumGradleVersion;
  private final String jacocoVersion;
  private final List<GroupIdArtifactIdVersion> defaultDependencies;
  private final List<GroupIdArtifactId> platformDependencies;
  private final List<GroupIdArtifactId> testDependencies;
  private final List<GroupIdArtifactId> springTestDependencies;
  private final List<GroupIdArtifactIdVersion> gradlePlugins;
}
