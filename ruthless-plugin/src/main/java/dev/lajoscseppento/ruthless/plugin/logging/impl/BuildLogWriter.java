package dev.lajoscseppento.ruthless.plugin.logging.impl;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.NonNull;
import org.gradle.api.GradleException;

/**
 * Writer for build logs. Build log can be closed and opened during writing several times, because
 * new events may or may not arrive after the first and definite sign of closing the file.
 *
 * <ul>
 *   <li>Opens the file upon object creation, truncating it if already exists
 *   <li>Auto-flushes output on each <code>print*</code> call
 *   <li>Auto-closes the file if {@link #closing()} was called, delayed from the last activity
 *   <li>Auto-reopens the file on each <code>print*</code> call in append mode if {@link #closing()}
 *       was called (and then auto-closes again)
 * </ul>
 */
class BuildLogWriter {
  // Close with delay to avoid too frequent closing and repoening
  private static final long CLOSE_DELAY_MS = 200;
  public static final int BACKGROUND_TICK_MS = 100;

  private final RuthlessLogger logger;
  @Getter
  private final Path file;
  private volatile PrintWriter writer;

  private final ScheduledExecutorService closingService;
  private volatile long lastActivityAt;
  private volatile boolean closing;
  private final Object lock = new Object();

  public BuildLogWriter(@NonNull Path file) {
    this.logger = RuthlessLogger.create(getClass(), "ruthless-logging");
    this.file = file.toAbsolutePath();
    this.writer = null;

    this.lastActivityAt = System.currentTimeMillis();
    this.closing = false;
    this.closingService = Executors.newSingleThreadScheduledExecutor();
    this.closingService.scheduleAtFixedRate(
        this::closeIfOpenAndInactive, 0, BACKGROUND_TICK_MS, TimeUnit.MILLISECONDS);

    ensureOpen(false);
  }

  private void ensureOpen(boolean append) {
    synchronized (lock) {
      if (writer == null) {
        logger.debug("Opening build log writer: {} (append={})", file, append);
        try {
          writer =
              new PrintWriter(
                  new FileOutputStream(file.toFile(), append), false, StandardCharsets.UTF_8);
          lastActivityAt = System.currentTimeMillis();
        } catch (Exception ex) {
          String msg = String.format("Failed to create writer for %s: %s", file, ex.getMessage());
          throw new GradleException(msg, ex);
        }
      }
    }
  }

  private void closeIfOpenAndInactive() {
    synchronized (lock) {
      if (!closing) {
        return;
      }

      long now = System.currentTimeMillis();

      if (now - lastActivityAt >= CLOSE_DELAY_MS) {
        closeIfOpen();

        lastActivityAt = now;
      }
    }
  }

  private void closeIfOpen() {
    synchronized (lock) {
      if (writer != null) {
        logger.debug("Closing build log writer: {}", file);
        writer.close();
        writer = null;
      }

      lastActivityAt = System.currentTimeMillis();
    }
  }

  public void closing() {
    synchronized (lock) {
      if (!closing) {
        logger.debug("Marking build log writer as closing: {}", file);
        closing = true;
      }
    }
  }

  public void closeNowIfOpen() {
    closing();
    closeIfOpen();
  }

  public void print(Object obj) {
    synchronized (lock) {
      ensureOpen(true);
      writer.print(obj);
      writer.flush();
      lastActivityAt = System.currentTimeMillis();
    }
  }

  public void println() {
    synchronized (lock) {
      ensureOpen(true);
      writer.println();
      writer.flush();
      lastActivityAt = System.currentTimeMillis();
    }
  }

  public void println(Object obj) {
    synchronized (lock) {
      ensureOpen(true);
      writer.println(obj);
      writer.flush();
      lastActivityAt = System.currentTimeMillis();
    }
  }

  public void printf(String format, Object... args) {
    synchronized (lock) {
      ensureOpen(true);
      writer.printf(format, args);
      writer.flush();
      lastActivityAt = System.currentTimeMillis();
    }
  }

  public void printStackTrace(Throwable throwable) {
    synchronized (lock) {
      ensureOpen(true);
      throwable.printStackTrace(writer);
      writer.flush();
      lastActivityAt = System.currentTimeMillis();
    }
  }
}
