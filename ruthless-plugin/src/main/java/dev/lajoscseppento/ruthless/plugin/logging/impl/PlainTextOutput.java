package dev.lajoscseppento.ruthless.plugin.logging.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.gradle.internal.logging.text.AbstractLineChoppingStyledTextOutput;

/**
 * {@link org.gradle.internal.logging.text.StyledTextOutput} implementation which writes the output
 * to {@link BuildLogWriter} as plain text.
 */
@RequiredArgsConstructor
class PlainTextOutput extends AbstractLineChoppingStyledTextOutput {
  @NonNull private BuildLogWriter buildLogWriter;

  @Override
  protected void doStyleChange(Style style) {
    // Ignore styles
  }

  @Override
  protected void doLineText(CharSequence text) {
    buildLogWriter.print(text);
  }

  @Override
  protected void doEndLine(CharSequence endOfLine) {
    buildLogWriter.println();
  }
}
