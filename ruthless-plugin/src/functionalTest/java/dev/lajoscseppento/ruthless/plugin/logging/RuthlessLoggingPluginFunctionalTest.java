package dev.lajoscseppento.ruthless.plugin.logging;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.DeleteDelta;
import com.github.difflib.patch.InsertDelta;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.assertj.core.api.SoftAssertions;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RuthlessLoggingPluginFunctionalTest {
  @TempDir Path projectDir;

  @BeforeEach
  void setUp() throws IOException {
    Path demoDir = Paths.get("../ruthless-demo").toAbsolutePath().normalize();
    FileUtils.copyDirectory(
        demoDir.toFile(),
        projectDir.toFile(),
        RuthlessLoggingPluginFunctionalTest::shouldCopy,
        false);
  }

  private static boolean shouldCopy(@NonNull File file) {
    return file.isDirectory()
        || FilenameUtils.isExtension(file.getName(), "factories", "java", "kts", "properties");
  }

  @Test
  void testAssembleLog() throws Exception {
    test("assemble", "log");
  }

  @Test
  void testNoBuildCacheCleanAssembleLog() throws Exception {
    test("--no-build-cache", "clean", "assemble", "log");
  }

  @Test
  void testLogWithRuthlessAllLoggerDebug() throws Exception {
    test("--system-prop", "ruthless.logging.logger.*.debug=true", "log");
  }

  @Test
  void testLogWithRuthlessLoggingLoggerDebug() throws Exception {
    test("--system-prop", "ruthless.logging.logger.ruthless-logging.debug=true", "log");
  }

  @Test
  void testRuthlessLoggingDisabled() throws Exception {
    // Given
    GradleRunner runner = createRunner("--system-prop", "ruthless.logging.enabled=false", "log");

    // When
    runner.build();

    // Then
    assertThat(projectDir.resolve("build.log")).doesNotExist();
  }

  @Test
  void testLogWithInfo() throws Exception {
    test("--info", "log");
  }

  @Test
  void testLogWithDebug() throws Exception {
    test("--debug", "log");
  }

  @Test
  void testFail() throws Exception {
    testFailure("fail");
  }

  @Test
  void testFailWithInfo() throws Exception {
    testFailure("--info", "fail");
  }

  @Test
  void testFailWithDebug() throws Exception {
    testFailure("--debug", "fail");
  }

  private void test(@NonNull String... arguments) throws Exception {
    test(false, arguments);
  }

  private void testFailure(@NonNull String... arguments) throws Exception {
    test(true, arguments);
  }

  private void test(boolean expectedToFail, @NonNull String... arguments) throws Exception {
    // Given
    GradleRunner runner = createRunner(arguments);

    // When
    BuildResult result = expectedToFail ? runner.buildAndFail() : runner.build();

    // Then
    String buildOutput = result.getOutput();
    String buildLog = Files.readString(projectDir.resolve("build.log"));

    List<String> buildOutputLines =
        buildOutput.lines().filter(new FilterBeforeRecording()).collect(Collectors.toList());
    List<String> buildLogLines =
        buildLog.lines().filter(new FilterBeforeRecording()).collect(Collectors.toList());

    List<AbstractDelta<String>> deltas = diff(buildOutputLines, buildLogLines);

    if (!deltas.isEmpty()) {
      SoftAssertions softly = new SoftAssertions();
      softly.fail("Arguments: " + String.join(" ", arguments));
      softly.fail(
          "Deltas:\n  "
              + deltas.stream().map(Object::toString).collect(Collectors.joining("\n  ")));
      // Just to show both the output and the recorded log for troubleshooting purposes
      softly.assertThat(buildOutput).isEqualToNormalizingNewlines(buildLog);
      softly.assertAll();
    }
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

  private static class FilterBeforeRecording implements Predicate<String> {
    private boolean keep = false;

    @Override
    public boolean test(String line) {
      if (line.contains("[ruthless-logging] Started log recording at")) {
        keep = true;
      }

      return keep;
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
