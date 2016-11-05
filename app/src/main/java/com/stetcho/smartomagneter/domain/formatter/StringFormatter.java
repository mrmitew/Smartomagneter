package com.stetcho.smartomagneter.domain.formatter;

/**
 * Interface for formatting numeric values as strings
 */
public interface StringFormatter {
    String format(int value);
    String format(float value);
    String format(double value);
}
