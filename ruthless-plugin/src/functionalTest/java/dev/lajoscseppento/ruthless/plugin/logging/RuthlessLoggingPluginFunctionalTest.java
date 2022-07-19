package dev.lajoscseppento.ruthless.plugin.logging;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.DeleteDelta;
import com.github.difflib.patch.InsertDelta;
import dev.lajoscseppento.ruthless.plugin.FunctionalTestUtils;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.SoftAssertions;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.io.TempDir;

class RuthlessLoggingPluginFunctionalTest {
  @TempDir Path projectDir;
  private TestInfo testInfo;
  private Path buildLogFile;

  @BeforeEach
  void setUp(@NonNull TestInfo testInfo) {
    FunctionalTestUtils.copyDemoProject(projectDir);
    this.testInfo = testInfo;
    buildLogFile = projectDir.resolve("build.log");
  }

  @Test
  void testAssembleLog() {
    test("assemble", "log");
  }

  @Test
  void testNoBuildCacheCleanAssembleLog() {
    test("--no-build-cache", "clean", "assemble", "log");
  }

  @Test
  void testLogWithRuthlessAllLoggerDebug() {
    test("--system-prop", "ruthless.logging.logger.*.debug=true", "log");
  }

  @Test
  void testLogWithRuthlessLoggingLoggerDebug() {
    test("--system-prop", "ruthless.logging.logger.ruthless-logging.debug=true", "log");
  }

  @Test
  void testRuthlessLoggingPluginDisabled() {
    // Given
    GradleRunner runner =
        createRunner("--system-prop", "ruthless.logging.plugin.enabled=false", "log");

    // When
    runner.build();

    // Then
    assertThat(buildLogFile).doesNotExist();
  }

  @Test
  void testLogWithInfo() {
    test("--info", "log");
  }

  @Test
  void testLogWithDebug() {
    test("--debug", "log");
  }

  @Test
  void testFail() {
    testFailure("fail");
  }

  @Test
  void testFailWithInfo() {
    testFailure("--info", "fail");
  }

  private void test(@NonNull String... arguments) {
    test(false, arguments);
  }

  private void testFailure(@NonNull String... arguments) {
    test(true, arguments);
  }

  private void test(boolean expectedToFail, @NonNull String... arguments) {
    // Given
    boolean debug = Arrays.asList(arguments).contains("--debug");
    GradleRunner runner = createRunner(arguments);

    // When
    BuildResult result = expectedToFail ? runner.buildAndFail() : runner.build();

    // Then
    String buildOutput = result.getOutput();
    String buildLog = FunctionalTestUtils.readString(buildLogFile);

    List<String> filteredBuildOutputLines =
        Arrays.stream(buildOutput.split("\r?\n", -1))
            .filter(new OutputFilter(debug))
            .collect(Collectors.toList());
    List<String> buildLogLines = Arrays.asList(buildLog.split("\r?\n", -1));
    List<String> filteredBuildLogLines =
        buildLogLines.stream().filter(new OutputFilter(debug)).collect(Collectors.toList());

    List<AbstractDelta<String>> deltas = diff(filteredBuildOutputLines, filteredBuildLogLines);

    if (!deltas.isEmpty()) {
      Path troubleshootDir =
          saveOutput(buildOutput, filteredBuildOutputLines, filteredBuildLogLines);

      SoftAssertions softly = new SoftAssertions();
      softly.fail("Arguments: " + String.join(" ", arguments));
      softly.fail(
          "Deltas:\n  "
              + deltas.stream().map(Object::toString).collect(Collectors.joining("\n  ")));
      softly.fail("Output and log copied to " + troubleshootDir);
      // Just to show both the output and the recorded log for troubleshooting purposes
      softly.assertThat(buildOutput).isEqualToNormalizingNewlines(buildLog);
      softly.assertAll();
    }

    if (debug) {
      assertThat(buildLogLines)
          .contains("   Debug level logging will leak security sensitive information!");
    }
  }

  // Very handy for development and troubleshooting failing tests
  private Path saveOutput(
      @NonNull String buildOutput,
      @NonNull List<String> filteredBuildOutputLines,
      @NonNull List<String> filteredBuildLogLines) {
    Path troubleshootDir =
        Paths.get(
                String.format(
                    "build/tmp/functionalTest-output/%s/%s",
                    getClass().getSimpleName(), testInfo.getDisplayName()))
            .toAbsolutePath();

    try {
      Files.createDirectories(troubleshootDir);

      Files.write(
          troubleshootDir.resolve("build.out"), buildOutput.getBytes(StandardCharsets.UTF_8));
      Files.write(troubleshootDir.resolve("build.out.filtered"), filteredBuildOutputLines);

      Files.copy(
          buildLogFile, troubleshootDir.resolve("build.log"), StandardCopyOption.REPLACE_EXISTING);
      Files.write(troubleshootDir.resolve("build.log.filtered"), filteredBuildLogLines);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }

    return troubleshootDir;
  }

  private GradleRunner createRunner(@NonNull String... arguments) {
    return GradleRunner.create()
        .withPluginClasspath()
        .withArguments(arguments)
        .withProjectDir(projectDir.toFile());
  }

  private static List<AbstractDelta<String>> diff(
      @NonNull List<String> buildOutputLines, @NonNull List<String> buildLogLines) {
    List<AbstractDelta<String>> deltas =
        DiffUtils.diff(buildOutputLines, buildLogLines).getDeltas();
    return filterDeltas(deltas);
  }

  private static List<AbstractDelta<String>> filterDeltas(
      @NonNull List<AbstractDelta<String>> deltas) {
    return deltas.stream()
        .filter(RuthlessLoggingPluginFunctionalTest::keepDelta)
        .collect(Collectors.toList());
  }

  private static boolean keepDelta(@NonNull AbstractDelta<String> delta) {
    SafeListView<String> source = new SafeListView<>(delta.getSource().getLines(), "");
    SafeListView<String> target = new SafeListView<>(delta.getTarget().getLines(), "");

    // ---- CHANGE -----
    // BUILD SUCCESSFUL in 345ms
    // 6 actionable tasks: 6 executed
    // =>
    // BUILD SUCCESSFUL
    if (source.size() == 2
        && source.get(0).startsWith("BUILD SUCCESSFUL in")
        && source.get(1).contains("actionable task")
        && target.size() == 1
        && target.get(0).equals("BUILD SUCCESSFUL")) {
      return false;
    }

    // ---- CHANGE -----
    // BUILD FAILED in 4s
    // 6 actionable tasks: 6 executed
    // =>
    // BUILD FAILED
    if (source.size() == 2
        && source.get(0).startsWith("BUILD FAILED in")
        && source.get(1).contains("actionable task")
        && target.size() == 1
        && target.get(0).equals("BUILD FAILED")) {
      return false;
    }

    // ---- DELETE -----
    // [DEBUG] [ruthless-logging] Stopping LogRecordingService$Inject
    // [DEBUG] [ruthless-logging] Marking build log writer as closing: [...]/build.log
    // [DEBUG] [ruthless-logging] Stopped LogRecordingService$Inject
    // [DEBUG] [ruthless-logging] Closing build log writer: [...]/build.log
    if (source.size() >= 2
        && source.get(0).startsWith("[DEBUG] [ruthless-logging] Stopping LogRecordingService")
        && source
            .get(source.size() - 1)
            .startsWith("[DEBUG] [ruthless-logging] Closing build log writer")
        && target.isEmpty()) {
      return false;
    }

    // Extra task executed lines
    if (delta instanceof InsertDelta
        && source.stream().allMatch(line -> line.matches("^ Task :\\s+$"))) {
      return false;
    }

    // Extra empty lines
    if (delta instanceof InsertDelta && source.stream().allMatch(String::isEmpty)) {
      return false;
    }

    // Missing empty lines
    if (delta instanceof DeleteDelta && source.stream().allMatch(String::isEmpty)) {
      return false;
    }

    return true;
  }

  @RequiredArgsConstructor
  private static class OutputFilter implements Predicate<String> {
    private final boolean debug;
    private boolean inRecordedSection = false;

    @Override
    public boolean test(String line) {
      // Skip noise:
      // VCS Checkout Cache (...) removing files not accessed on or after ...
      // VCS Checkout Cache ...) cleanup deleted 0 files/directories.
      // VCS Checkout Cache (...) cleaned up in 0.0 secs.
      if (line.startsWith("VCS Checkout Cache")) {
        return false;
      }

      // Skip noise:
      // dependencies-accessors (...) removing files not accessed on or after ...
      // dependencies-accessors (...) cleanup deleted 0 files/directories.
      // dependencies-accessors (...) cleaned up in 0.0 secs.
      if (line.startsWith("dependencies-accessors")) {
        return false;
      }

      // Keep only the recorded section
      if (line.contains("[ruthless-logging] Started log recording at")) {
        inRecordedSection = true;
      }

      if (debug && line.contains("Stopping build output recording")) {
        inRecordedSection = false;
      }

      return inRecordedSection;
    }
  }

  @RequiredArgsConstructor
  private static final class SafeListView<T> {
    @NonNull private final List<T> delegate;
    @NonNull private final T defaultValue;

    public boolean isEmpty() {
      return delegate.isEmpty();
    }

    public T get(int index) {
      if (index < 0 || index >= size()) {
        return defaultValue;
      } else {
        T value = delegate.get(index);
        return value == null ? defaultValue : value;
      }
    }

    public int size() {
      return delegate.size();
    }

    public Stream<T> stream() {
      return delegate.stream();
    }
  }
}
