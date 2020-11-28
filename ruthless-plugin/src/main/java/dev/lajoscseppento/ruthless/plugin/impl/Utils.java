package dev.lajoscseppento.ruthless.plugin.impl;

import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import lombok.experimental.UtilityClass;

/** The must-have blob. */
@UtilityClass
class Utils {
  private final String UNSPECIFIED = "unspecified";

  final SortedMap<String, String> DEFAULT_DEPENDENCIES;

  static {
    // Default dependencies
    try (InputStream in = Utils.class.getResourceAsStream("/default-dependencies.xml")) {
      Properties properties = new Properties();
      properties.loadFromXML(in);

      SortedMap<String, String> defaultDependencies = new TreeMap<>();
      for (String key : properties.stringPropertyNames()) {
        defaultDependencies.put(key, properties.getProperty(key));
      }

      DEFAULT_DEPENDENCIES = Collections.unmodifiableSortedMap(defaultDependencies);
    } catch (Exception ex) {
      throw new AssertionError("Failed to load default dependencies: " + ex.getMessage(), ex);
    }
  }

  static boolean isUnspecified(Object value) {
    return value == null || value.toString().isBlank() || UNSPECIFIED.equals(value.toString());
  }
}
