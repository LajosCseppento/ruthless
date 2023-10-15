package dev.lajoscseppento.ruthless.plugin.logging;

import dev.lajoscseppento.gradle.plugin.common.property.BooleanSystemProperty;
import dev.lajoscseppento.ruthless.plugin.logging.impl.LogRecordingService;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;
import org.gradle.api.invocation.Gradle;

/**
 * The Ruthless Logging Plugin applies logging conventions to the Gradle build:
 *
 * <ul>
 *   <li>{@link LogRecordingService}: records the build output to file
 * </ul>
 *
 * <p>The plugin can be disabled via the <code>
 * ruthless.logging.plugin.enabled=false</code> system property.
 */
public class RuthlessLoggingPlugin implements Plugin<Settings> {

  private static final BooleanSystemProperty enabled =
      new BooleanSystemProperty("ruthless.logging.plugin.enabled", true);

  // See https://github.com/LajosCseppento/ruthless/issues/43
  private static final Map<UUID, Settings> settingsById = new ConcurrentHashMap<>();

  private RuthlessLogger logger;
  private Settings settings;
  private Gradle gradle;

  @Override
  public void apply(Settings settings) {
    this.settings = settings;
    gradle = settings.getGradle();
    logger = RuthlessLogger.create(getClass());

    if (enabled.get()) {
      configureFileLogging();
    } else {
      logger.info("Ruthless logging disabled");
    }
  }

  private void configureFileLogging() {
    logger.info("Configuring file logging");

    UUID id = UUID.randomUUID();
    settingsById.put(id, settings);

    gradle
        .getSharedServices()
        .registerIfAbsent(
            "ruthless-logging",
            LogRecordingService.class,
            spec -> spec.getParameters().getId().set(id))
        .get();
  }

  /**
   * Returns the {@link Settings} instance for the given ID.
   *
   * @param id the ID
   * @return the {@link Settings} instance
   */
  public static Settings findSettingsById(@NonNull UUID id) {
    return settingsById.get(id);
  }
}
