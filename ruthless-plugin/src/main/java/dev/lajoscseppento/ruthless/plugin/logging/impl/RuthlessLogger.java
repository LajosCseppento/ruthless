package dev.lajoscseppento.ruthless.plugin.logging.impl;

import dev.lajoscseppento.gradle.plugin.common.impl.Utils;
import dev.lajoscseppento.gradle.plugin.common.property.BooleanSystemProperty;
import javax.annotation.Nullable;
import lombok.NonNull;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

/**
 * Wrapper for Gradle {@link Logger}.
 *
 * <ul>
 *   <li>Provides capability to prefix all log messages.
 *   <li>Provides capability to elevate DEBUG and INFO logs to LIFECYCLE on-demand with the <code>
 *       ruthless.logging.logger.*.debug</code> system property for all {@link RuthlessLogger}
 *       instances.
 *   <li>Provides capability to elevate DEBUG and INFO logs to LIFECYCLE on-demand with the <code>
 *       ruthless.logging.logger.PREFIX.debug</code> system property for {@link RuthlessLogger}
 *       instances with the specified <code>PREFIX</code>.
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

  public static RuthlessLogger create(@NonNull Class<?> cls) {
    return create(cls, null);
  }

  public static RuthlessLogger create(@NonNull Class<?> cls, @Nullable String prefix) {
    return create(Logging.getLogger(cls), prefix);
  }

  public static RuthlessLogger create(@NonNull Logger delegate) {
    return create(delegate, null);
  }

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

  public boolean isDebugEnabled() {
    return debug || delegate.isDebugEnabled();
  }

  public boolean isInfoEnabled() {
    return debug || delegate.isInfoEnabled();
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Logging Methods Per Log Level ///////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////

  public void debug(String message) {
    log(LogLevel.DEBUG, message);
  }

  public void debug(String format, Object... arguments) {
    log(LogLevel.DEBUG, format, arguments);
  }

  public void debug(String msg, Throwable throwable) {
    log(LogLevel.DEBUG, msg, throwable);
  }

  public void error(String message) {
    log(LogLevel.ERROR, message);
  }

  public void error(String format, Object... arguments) {
    log(LogLevel.ERROR, format, arguments);
  }

  public void error(String msg, Throwable throwable) {
    log(LogLevel.ERROR, msg, throwable);
  }

  public void info(String message) {
    log(LogLevel.INFO, message);
  }

  public void info(String format, Object... arguments) {
    log(LogLevel.INFO, format, arguments);
  }

  public void info(String msg, Throwable throwable) {
    log(LogLevel.INFO, msg, throwable);
  }

  public void lifecycle(String message) {
    log(LogLevel.LIFECYCLE, message);
  }

  public void lifecycle(String format, Object... arguments) {
    log(LogLevel.LIFECYCLE, format, arguments);
  }

  public void lifecycle(String msg, Throwable throwable) {
    log(LogLevel.LIFECYCLE, msg, throwable);
  }

  public void quiet(String message) {
    log(LogLevel.QUIET, message);
  }

  public void quiet(String format, Object... arguments) {
    log(LogLevel.QUIET, format, arguments);
  }

  public void quiet(String msg, Throwable throwable) {
    log(LogLevel.QUIET, msg, throwable);
  }

  public void warn(String message) {
    log(LogLevel.WARN, message);
  }

  public void warn(String format, Object... arguments) {
    log(LogLevel.WARN, format, arguments);
  }

  public void warn(String msg, Throwable throwable) {
    log(LogLevel.WARN, msg, throwable);
  }
}
