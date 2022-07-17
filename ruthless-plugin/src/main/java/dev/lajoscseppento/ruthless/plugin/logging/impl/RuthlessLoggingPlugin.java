package dev.lajoscseppento.ruthless.plugin.logging.impl;

import dev.lajoscseppento.gradle.plugin.common.property.BooleanSystemProperty;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;
import org.gradle.api.invocation.Gradle;

// TODO maybe it could be a separate plugin
// TODO class comment
public class RuthlessLoggingPlugin implements Plugin<Settings> {

  private static final BooleanSystemProperty enabled =
      new BooleanSystemProperty("ruthless.logging.enabled", true);

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

  public static Settings findSettingsById(@NonNull UUID id) {
    return settingsById.get(id);
  }
}
