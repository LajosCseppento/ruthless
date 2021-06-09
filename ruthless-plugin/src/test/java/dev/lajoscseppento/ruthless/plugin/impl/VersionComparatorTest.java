package dev.lajoscseppento.ruthless.plugin.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class VersionComparatorTest {
  private static VersionComparator cmp;

  @BeforeAll
  static void setUpClass() {
    cmp = new VersionComparator();
  }

  @ParameterizedTest
  @CsvSource({
    "1.0,1.1",
    "1.0,1.0.1",
    "1.2.3,1.2.4",
    "1.2,1.2.3",
    "1.2.a,1.2.b",
    "1.2.0,1.2.b",
    "1.a.0,1.a.1"
  })
  void testNotEqual(String o1, String o2) {
    assertThat(cmp.compare(o1, o2)).isNegative();
    assertThat(cmp.compare(o2, o1)).isPositive();
  }

  @ParameterizedTest
  @CsvSource({"1.0,1.0", "1.2.3,1.2.3", "1.0.1,1.00.01", "a,a", "a.b,a.b"})
  void testEqual(String o1, String o2) {
    assertThat(cmp.compare(o1, o2)).isZero();
    assertThat(cmp.compare(o2, o1)).isZero();
  }
}
