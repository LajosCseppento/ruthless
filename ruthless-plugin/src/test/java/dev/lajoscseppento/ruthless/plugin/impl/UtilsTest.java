package dev.lajoscseppento.ruthless.plugin.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class UtilsTest {
  @Test
  void testIsUnspecifiedReturnsTrueIfNull() {
    assertThat(Utils.isUnspecified(null)).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "  ", "  unspecified  "})
  void testIsUnspecifiedReturnsTrue(String value) {
    assertThat(Utils.isUnspecified(value)).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {"1.2.3", "something else"})
  void testIsUnspecifiedReturnsFalse(String value) {
    assertThat(Utils.isUnspecified(value)).isFalse();
  }
}
