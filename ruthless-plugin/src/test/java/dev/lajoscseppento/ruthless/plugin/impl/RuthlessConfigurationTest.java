package dev.lajoscseppento.ruthless.plugin.impl;

import static org.assertj.core.api.Assertions.assertThat;

import dev.lajoscseppento.ruthless.plugin.configuration.impl.RuthlessConfiguration;
import org.junit.jupiter.api.Test;

class RuthlessConfigurationTest {
  @Test
  void testParse() {
    assertThat(RuthlessConfiguration.INSTANCE.toString()).isNotEmpty();
  }
}
