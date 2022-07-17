package dev.lajoscseppento.ruthless.plugin.logging.impl;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import lombok.Getter;
import lombok.NonNull;
import org.gradle.api.GradleException;

/**
 * Writer for build logs. The build log may be closed and opened during writing several times,
 * because new events may or may not arrive after the first and definite sign of closing the file.
 *
 * <ul>
 *   <li>Opens the file upon object creation, truncating it if already exists
 *   <li>Flushes output on each <code>print*</code> call
 *   <li>Reopens the file on any <code>print*</code> close if it was previously closed. Make sure to
 *       always call {@link #close()} after the last <code>print*</code> call to avoid leaks.
 * </ul>
 */
class BuildLogWriter {
  private final RuthlessLogger logger;
  @Getter private final Path file;
  private volatile PrintWriter writer;
  private final Object lock = new Object();

  public BuildLogWriter(@NonNull Path file) {
    this.logger = RuthlessLogger.create(getClass(), "ruthless-logging");
    this.file = file.toAbsolutePath();
    this.writer = null;

    ensureOpen(false);
  }

  private void ensureOpen(boolean append) {
    synchronized (lock) {
      if (writer == null) {
        logger.debug("Opening build log writer: {} (append={})", file, append);
        try {
          writer =
              new PrintWriter(
                  new OutputStreamWriter(
                      new FileOutputStream(file.toFile(), append), StandardCharsets.UTF_8),
                  false);
        } catch (Exception ex) {
          String msg = String.format("Failed to create writer for %s: %s", file, ex.getMessage());
          throw new GradleException(msg, ex);
        }
      }
    }
  }

  public void close() {
    synchronized (lock) {
      if (writer != null) {
        logger.debug("Closing build log writer: {}", file);
        writer.close();
        writer = null;
      }
    }
  }

  public void print(Object obj) {
    synchronized (lock) {
      ensureOpen(true);
      writer.print(obj);
      writer.flush();
    }
  }

  public void println() {
    synchronized (lock) {
      ensureOpen(true);
      writer.println();
      writer.flush();
    }
  }

  public void println(Object obj) {
    synchronized (lock) {
      ensureOpen(true);
      writer.println(obj);
      writer.flush();
    }
  }

  public void printf(String format, Object... args) {
    synchronized (lock) {
      ensureOpen(true);
      writer.printf(format, args);
      writer.flush();
    }
  }

  public void printStackTrace(Throwable throwable) {
    synchronized (lock) {
      ensureOpen(true);
      throwable.printStackTrace(writer);
      writer.flush();
    }
  }
}
