package dev.lajoscseppento.ruthless.plugin.logging;

import dev.lajoscseppento.gradle.plugin.common.impl.Utils;
import dev.lajoscseppento.gradle.plugin.common.property.BooleanSystemProperty;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

/**
 * Wrapper for Gradle {@link Logger}.
 *
 * <ul>
 *   <li>Provides capability to prefix all log messages.
 *   <li>Provides capability to elevate DEBUG and INFO logs to LIFECYCLE on-demand for all {@link
 *       RuthlessLogger} instances using the <code>ruthless.logging.logger.*.debug=true</code>
 *       system property.
 *   <li>Provides capability to elevate DEBUG and INFO logs to LIFECYCLE on-demand for selected
 *       {@link RuthlessLogger} instances using the <code>ruthless.logging.logger.PREFIX.debug=true
 *       </code> system property, where <code>PREFIX</code> denotes the desired prefix to be
 *       elevated.
 * </ul>
 */
public class RuthlessLogger {
  private static final String MSG_ONE_PREFIX = "[{}] {}";
  private static final String MSG_TWO_PREFIXES = "[{}] [{}] {}";
  private static final String FMT_ONE_PREFIX = "[{}] ";
  private static final String FMT_TWO_PREFIXES = "[{}] [{}] ";

  private final Logger delegate;
  @Nullable private final String prefix;
  private final boolean debug;

  private RuthlessLogger(@NonNull Logger delegate, String prefix, boolean debug) {
    this.delegate = delegate;
    this.prefix = Utils.trimToNull(prefix);
    this.debug = debug;
  }

  /**
   * Creates a new {@link RuthlessLogger} instance.
   *
   * @param cls the class to create the logger for
   * @return the new {@link RuthlessLogger} instance
   */
  public static RuthlessLogger create(@NonNull Class<?> cls) {
    return create(cls, null);
  }

  /**
   * Creates a new {@link RuthlessLogger} instance.
   *
   * @param cls the class to create the logger for
   * @param prefix the prefix to use for all log messages
   * @return the new {@link RuthlessLogger} instance
   */
  public static RuthlessLogger create(@NonNull Class<?> cls, @Nullable String prefix) {
    return create(Logging.getLogger(cls), prefix);
  }

  /**
   * Creates a new {@link RuthlessLogger} instance.
   *
   * @param delegate the delegate {@link Logger} to use
   * @return the new {@link RuthlessLogger} instance
   */
  public static RuthlessLogger create(@NonNull Logger delegate) {
    return create(delegate, null);
  }

  /**
   * Creates a new {@link RuthlessLogger} instance.
   *
   * @param delegate the delegate {@link Logger} to use
   * @param prefix the prefix to use for all log messages
   * @return the new {@link RuthlessLogger} instance
   */
  public static RuthlessLogger create(@NonNull Logger delegate, @Nullable String prefix) {
    return new RuthlessLogger(delegate, prefix, isDebug(prefix));
  }

  private static boolean isDebug(String prefix) {
    // Enables debug for RuthlessLogger instances, which will trigger all log messages on a
    // lifecycle, so info and debug logs are visible even if the Gradle log level is higher. This is
    // useful for development.
    boolean debug =
        new BooleanSystemProperty("ruthless.logging.logger." + prefix + ".debug", false).get();

    if (debug) {
      return true;
    } else {
      return new BooleanSystemProperty("ruthless.logging.logger.*.debug", false).get();
    }
  }

  private void log(LogLevel level, String message) {
    if (debug && (level == LogLevel.DEBUG || level == LogLevel.INFO)) {
      if (hasPrefix()) {
        delegate.log(LogLevel.LIFECYCLE, MSG_TWO_PREFIXES, level, prefix, message);
      } else {
        delegate.log(LogLevel.LIFECYCLE, MSG_ONE_PREFIX, level, message);
      }
    } else if (delegate.isEnabled(level)) {
      if (hasPrefix()) {
        delegate.log(level, MSG_ONE_PREFIX, prefix, message);
      } else {
        delegate.log(level, message);
      }
    }
  }

  private void log(LogLevel level, String format, Object... arguments) {
    if (shouldElevate(level)) {
      if (hasPrefix()) {
        delegate.log(
            LogLevel.LIFECYCLE, FMT_TWO_PREFIXES + format, addFirst(arguments, level, prefix));
      } else {
        delegate.log(LogLevel.LIFECYCLE, FMT_ONE_PREFIX + format, addFirst(arguments, level));
      }
    } else if (delegate.isEnabled(level)) {
      if (hasPrefix()) {
        delegate.log(level, FMT_ONE_PREFIX + format, addFirst(arguments, prefix));
      } else {
        delegate.log(level, format, arguments);
      }
    }
  }

  private void log(LogLevel level, String message, Throwable throwable) {
    if (shouldElevate(level)) {
      if (hasPrefix()) {
        delegate.log(LogLevel.LIFECYCLE, MSG_TWO_PREFIXES, level, prefix, message, throwable);
      } else {
        delegate.log(LogLevel.LIFECYCLE, MSG_ONE_PREFIX, level, message, throwable);
      }
    } else if (delegate.isEnabled(level)) {
      if (hasPrefix()) {
        delegate.log(level, MSG_ONE_PREFIX, prefix, message, throwable);
      } else {
        delegate.log(level, message, throwable);
      }
    }
  }

  private boolean hasPrefix() {
    return prefix != null;
  }

  private boolean shouldElevate(LogLevel level) {
    return debug && (level == LogLevel.DEBUG || level == LogLevel.INFO);
  }

  // May return one of the parameters if no new array needed to be created
  private Object[] addFirst(Object[] array, Object... elements) {
    if (array == null || array.length == 0) {
      return elements;
    } else if (elements == null || elements.length == 0) {
      return array;
    }

    Object[] newArray = new Object[array.length + elements.length];
    System.arraycopy(elements, 0, newArray, 0, elements.length);
    System.arraycopy(array, 0, newArray, elements.length, array.length);

    return newArray;
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Logging Enabled Methods /////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////

  /**
   * Checks if the debug log level is enabled.
   *
   * @return true if the debug log level is enabled
   */
  public boolean isDebugEnabled() {
    return debug || delegate.isDebugEnabled();
  }

  /**
   * Checks if the info log level is enabled.
   *
   * @return true if the info log level is enabled
   */
  public boolean isInfoEnabled() {
    return debug || delegate.isInfoEnabled();
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Logging Methods Per Log Level ///////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////

  /**
   * Logs a message at the debug log level.
   *
   * @param message the message to log
   */
  public void debug(String message) {
    log(LogLevel.DEBUG, message);
  }

  /**
   * Logs a message at the debug log level.
   *
   * @param format the format string
   * @param arguments the arguments referenced by the format specifiers in the format string
   */
  public void debug(String format, Object... arguments) {
    log(LogLevel.DEBUG, format, arguments);
  }

  /**
   * Logs a message at the debug log level.
   *
   * @param msg the message to log
   * @param throwable the exception to log
   */
  public void debug(String msg, Throwable throwable) {
    log(LogLevel.DEBUG, msg, throwable);
  }

  /**
   * Logs a message at the error log level.
   *
   * @param message the message to log
   */
  public void error(String message) {
    log(LogLevel.ERROR, message);
  }

  /**
   * Logs a message at the error log level.
   *
   * @param format the format string
   * @param arguments the arguments referenced by the format specifiers in the format string
   */
  public void error(String format, Object... arguments) {
    log(LogLevel.ERROR, format, arguments);
  }

  /**
   * Logs a message at the error log level.
   *
   * @param msg the message to log
   * @param throwable the exception to log
   */
  public void error(String msg, Throwable throwable) {
    log(LogLevel.ERROR, msg, throwable);
  }

  /**
   * Logs a message at the info log level.
   *
   * @param message the message to log
   */
  public void info(String message) {
    log(LogLevel.INFO, message);
  }

  /**
   * Logs a message at the info log level.
   *
   * @param format the format string
   * @param arguments the arguments referenced by the format specifiers in the format string
   */
  public void info(String format, Object... arguments) {
    log(LogLevel.INFO, format, arguments);
  }

  /**
   * Logs a message at the info log level.
   *
   * @param msg the message to log
   * @param throwable the exception to log
   */
  public void info(String msg, Throwable throwable) {
    log(LogLevel.INFO, msg, throwable);
  }

  /**
   * Logs a message at the lifecycle log level.
   *
   * @param message the message to log
   */
  public void lifecycle(String message) {
    log(LogLevel.LIFECYCLE, message);
  }

  /**
   * Logs a message at the lifecycle log level.
   *
   * @param format the format string
   * @param arguments the arguments referenced by the format specifiers in the format string
   */
  public void lifecycle(String format, Object... arguments) {
    log(LogLevel.LIFECYCLE, format, arguments);
  }

  /**
   * Logs a message at the lifecycle log level.
   *
   * @param msg the message to log
   * @param throwable the exception to log
   */
  public void lifecycle(String msg, Throwable throwable) {
    log(LogLevel.LIFECYCLE, msg, throwable);
  }

  /**
   * Logs a message at the quiet log level.
   *
   * @param message the message to log
   */
  public void quiet(String message) {
    log(LogLevel.QUIET, message);
  }

  /**
   * Logs a message at the quiet log level.
   *
   * @param format the format string
   * @param arguments the arguments referenced by the format specifiers in the format string
   */
  public void quiet(String format, Object... arguments) {
    log(LogLevel.QUIET, format, arguments);
  }

  /**
   * Logs a message at the quiet log level.
   *
   * @param msg the message to log
   * @param throwable the exception to log
   */
  public void quiet(String msg, Throwable throwable) {
    log(LogLevel.QUIET, msg, throwable);
  }

  /**
   * Logs a message at the warn log level.
   *
   * @param message the message to log
   */
  public void warn(String message) {
    log(LogLevel.WARN, message);
  }

  /**
   * Logs a message at the warn log level.
   *
   * @param format the format string
   * @param arguments the arguments referenced by the format specifiers in the format string
   */
  public void warn(String format, Object... arguments) {
    log(LogLevel.WARN, format, arguments);
  }

  /**
   * Logs a message at the warn log level.
   *
   * @param msg the message to log
   * @param throwable the exception to log
   */
  public void warn(String msg, Throwable throwable) {
    log(LogLevel.WARN, msg, throwable);
  }
}
