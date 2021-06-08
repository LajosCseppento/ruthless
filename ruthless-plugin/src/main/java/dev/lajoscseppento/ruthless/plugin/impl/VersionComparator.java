package dev.lajoscseppento.ruthless.plugin.impl;

import java.util.Comparator;
import java.util.Objects;

public class VersionComparator implements Comparator<String> {
  @Override
  public int compare(String o1, String o2) {
    Objects.requireNonNull(o1, "o1");
    Objects.requireNonNull(o2, "o2");

    String[] p1 = o1.split("\\.");
    String[] p2 = o2.split("\\.");

    for (int i = 0; i < Math.min(p1.length, p2.length); i++) {
      int partResult = cmp(p1[i], p2[i]);

      if (partResult != 0) {
        return partResult;
      }
    }

    return Integer.compare(p1.length, p2.length);
  }

  private static final int cmp(String p1, String p2) {
    try {
      return Integer.compare(Integer.parseInt(p1), Integer.parseInt(p2));
    } catch (NumberFormatException ex) {
      return p1.compareTo(p2);
    }
  }
}
