package dev.lajoscseppento.ruthless.plugin.logging.impl;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.NonNull;
import org.gradle.BuildResult;
import org.gradle.api.Task;
import org.gradle.api.execution.TaskExecutionAdapter;
import org.gradle.api.execution.TaskExecutionListener;
import org.gradle.api.initialization.Settings;
import org.gradle.api.internal.DocumentationRegistry;
import org.gradle.api.internal.GradleInternal;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.logging.LoggingOutput;
import org.gradle.api.provider.Property;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;
import org.gradle.api.tasks.TaskState;
import org.gradle.initialization.BuildClientMetaData;
import org.gradle.internal.buildevents.BuildExceptionReporter;
import org.gradle.internal.exceptions.LocationAwareException;
import org.gradle.internal.logging.LoggingOutputInternal;
import org.gradle.internal.logging.events.*;
import org.gradle.internal.logging.format.PrettyPrefixedLogHeaderFormatter;
import org.gradle.internal.logging.services.DefaultStyledTextOutputFactory;
import org.gradle.internal.logging.sink.GroupingProgressLogEventGenerator;
import org.gradle.internal.logging.slf4j.OutputEventListenerBackedLoggerContext;
import org.gradle.internal.logging.text.StyledTextOutput;
import org.gradle.internal.logging.text.StyledTextOutputFactory;
import org.gradle.internal.time.Clock;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

// TODO class comment
// TODO document that the advantage if this over .sh / CMD solution is to keep the rich console
// output
public abstract class LogRecordingService
    implements BuildService<LogRecordingService.Parameters>, AutoCloseable {
  private static final String BUILD_LOG_FILE_NAME = "build.log";
  private final RuthlessLogger logger;
  private final Settings settings;
  private final Gradle gradle;
  private final LoggingOutputInternal loggingOutputInternal;
  private final OutputEventListenerBackedLoggerContext loggerContext;

  private final BuildLogWriter buildLogWriter;
  private StyledTextOutput renderer = null;
  private final SimpleDateFormat debugDateFormat =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
  private OutputEventListener outputEventListener = null;

  @SuppressWarnings("deprecation")
  private TaskExecutionListener taskExecutionListener = null;

  private final Object lock = new Object();

  public LogRecordingService() {
    UUID id = getParameters().getId().get();
    logger = RuthlessLogger.create(getClass(), "ruthless-logging");
    settings = RuthlessLoggingPlugin.findSettingsById(id);

    logger.debug("Creating {}", getClass().getSimpleName());

    if (settings == null) {
      logger.warn(
          "Settings not found, cannot save build output to file. See https://github.com/LajosCseppento/ruthless/issues/43 for more details");
      gradle = null;
    } else {
      gradle = settings.getGradle();
    }

    loggingOutputInternal = findLoggingOutputInternal();
    loggerContext = findOutputEventListenerBackedLoggerContext();

    logger.info("Initialising build output recording");

    buildLogWriter = createBuildLogWriter();

    if (buildLogWriter != null) {
      try {
        renderer = new PlainTextOutput(buildLogWriter);

        outputEventListener =
            new GroupingProgressLogEventGenerator(
                this::onOutput, new PrettyPrefixedLogHeaderFormatter(), false);
        taskExecutionListener =
            new TaskExecutionAdapter() {
              @Override
              public void afterExecute(@NonNull Task task, @NonNull TaskState state) {
                onTaskExecuted(task, state);
              }
            };

        addListeners();
        writeSecurityWarningIfDebug();
        logger.quiet(
            "Started log recording at {}, target: {}",
            ZonedDateTime.now().withNano(0),
            buildLogWriter.getFile());
      } catch (Exception ex) {
        handleException("Failed to initialise file logging", ex);
      }
    }

    logger.debug("Created {}", getClass().getSimpleName());
  }

  private LoggingOutputInternal findLoggingOutputInternal() {
    if (gradle instanceof GradleInternal) {
      Object loggingOutput = ((GradleInternal) gradle).getServices().find(LoggingOutput.class);

      if (loggingOutput instanceof LoggingOutputInternal) {
        logger.debug("Found LoggingOutputInternal");
        return (LoggingOutputInternal) loggingOutput;
      } else {
        logger.warn("LoggingOutputInternal not found, cannot save build output to file");
      }
    } else {
      logger.warn("GradleInternal not found, cannot save build output to file");
    }

    return null;
  }

  private OutputEventListenerBackedLoggerContext findOutputEventListenerBackedLoggerContext() {
    ILoggerFactory iLoggerFactory = LoggerFactory.getILoggerFactory();
    if (iLoggerFactory instanceof OutputEventListenerBackedLoggerContext) {
      logger.debug("Found OutputEventListenerBackedLoggerContext");
      return (OutputEventListenerBackedLoggerContext) iLoggerFactory;
    } else {
      logger.warn(
          "OutputEventListenerBackedLoggerContext not found, cannot save build output to file");
      return null;
    }
  }

  private BuildLogWriter createBuildLogWriter() {
    if (settings == null || loggingOutputInternal == null || loggerContext == null) {
      return null;
    }

    try {
      Path logFile = settings.getRootDir().toPath().resolve(BUILD_LOG_FILE_NAME);
      return new BuildLogWriter(logFile);
    } catch (Exception ex) {
      handleException("Failed to create build log writer", ex);
    }

    return null;
  }

  @SuppressWarnings("deprecation")
  private void addListeners() {
    synchronized (lock) {
      loggingOutputInternal.addOutputEventListener(outputEventListener);
      // No alternative in Gradle 7.4, see https://github.com/LajosCseppento/ruthless/issues/43
      gradle.getTaskGraph().addTaskExecutionListener(taskExecutionListener);
      gradle.buildFinished(this::onBuildResult);
    }
  }

  @SuppressWarnings("deprecation")
  private void removeListeners() {
    synchronized (lock) {
      if (taskExecutionListener != null) {
        gradle.getTaskGraph().removeTaskExecutionListener(taskExecutionListener);
      }
      if (outputEventListener != null) {
        loggingOutputInternal.removeOutputEventListener(outputEventListener);
      }
    }
  }

  @Override
  public void close() {
    synchronized (lock) {
      logger.info("Stopping build output recording");
      removeListeners();

      logger.debug("Stopping {}", getClass().getSimpleName());

      if (buildLogWriter != null) {
        buildLogWriter.close();
      }

      logger.debug("Stopped {}", getClass().getSimpleName());
    }
  }

  private void writeSecurityWarningIfDebug() {
    if (loggerContext.getLevel() == LogLevel.DEBUG) {
      // See org.gradle.launcher.cli.DebugLoggerWarningAction.WARNING_MESSAGE_BODY
      buildLogWriter.println();
      buildLogWriter.println();
      buildLogWriter.println(
          "#############################################################################");
      buildLogWriter.println(
          "   WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING");
      buildLogWriter.println();
      buildLogWriter.println("   Debug level logging will leak security sensitive information!");
      buildLogWriter.println();
      buildLogWriter.printf(
          "   %s%n",
          new DocumentationRegistry().getDocumentationFor("logging", "sec:debug_security"));
      buildLogWriter.println(
          "#############################################################################");
    }
  }

  private void handleException(String message, Exception exception) {
    logger.warn(
        "{}: {} (please re-run the build with --info for more details)",
        message,
        exception.getMessage());
    logger.info("{}: {}", message, exception.getMessage(), exception);
  }

  private void onOutput(OutputEvent event) {
    synchronized (lock) {
      if (loggerContext.getLevel() == LogLevel.DEBUG && event instanceof CategorisedOutputEvent) {
        CategorisedOutputEvent categorisedOutputEvent = (CategorisedOutputEvent) event;
        buildLogWriter.printf(
            "%s [%s] [%s] ",
            debugDateFormat.format(categorisedOutputEvent.getTimestamp()),
            categorisedOutputEvent.getLogLevel(),
            categorisedOutputEvent.getCategory());
      }

      if (event instanceof LogLevelChangeEvent) {
        buildLogWriter.printf(
            "Log level changed to %s%n", ((LogLevelChangeEvent) event).getNewLogLevel());
      } else if (event instanceof RenderableOutputEvent) {
        ((RenderableOutputEvent) event).render(renderer);
      } else if (event instanceof EndOutputEvent) {
        // End of output, nothing to do
      } else {
        buildLogWriter.printf("Unsure how to record [%s] %s%n", event.getClass(), event);
        buildLogWriter.println(
            "Please report at https://github.com/LajosCseppento/ruthless/issues/new");
      }
    }
  }

  private void onTaskExecuted(Task task, TaskState state) {
    // It could filter on state.getDidWork() to avoid logging tasks twice if they produce log
    // messages. However, then it might not log all tasks to file.

    synchronized (lock) {
      if (state.getSkipMessage() == null) {
        buildLogWriter.printf("> Task %s%n", task.getPath());
      } else {
        buildLogWriter.printf("> Task %s %s%n", task.getPath(), state.getSkipMessage());
      }
    }
  }

  // Note: there is no guarantee that this method is called at all in Gradle 7.4.2
  private void onBuildResult(@NonNull BuildResult buildResult) {
    synchronized (lock) {
      Throwable failure = buildResult.getFailure();

      if (failure != null) {
        if (gradle instanceof GradleInternal) {
          GradleInternal gradleInternal = (GradleInternal) gradle;
          Clock clock = (Clock) gradleInternal.getServices().find(Clock.class);
          BuildClientMetaData buildClientMetaData =
              (BuildClientMetaData) gradleInternal.getServices().find(BuildClientMetaData.class);

          if (clock != null && buildClientMetaData != null) {
            StyledTextOutputFactory textOutputFactory =
                new DefaultStyledTextOutputFactory(this::onOutput, clock);

            new BuildExceptionReporter(
                    textOutputFactory, gradle.getStartParameter(), buildClientMetaData)
                .buildFinished(buildResult);
          } else {
            onFailureWithoutGradleInternal(failure);
          }
        } else {
          onFailureWithoutGradleInternal(failure);
        }
      }

      buildLogWriter.println();
      buildLogWriter.printf(
          "%S %s%n", buildResult.getAction(), failure == null ? "SUCCESSFUL" : "FAILED");

      // Gradle shows task statistics, but we ignore them from the file

      writeSecurityWarningIfDebug();

      buildLogWriter.close();
    }
  }

  private void onFailureWithoutGradleInternal(Throwable failure) {
    if (failure instanceof LocationAwareException && failure.getCause() != null) {
      failure = failure.getCause();
    }

    buildLogWriter.println();
    buildLogWriter.printStackTrace(failure);
    buildLogWriter.println();
  }

  public interface Parameters extends BuildServiceParameters {
    Property<UUID> getId();
  }
}
