package com.stetcho.smartomagneter.domain.formatter;

/**
 * Interface to format numbers - e.g. for precision
 */
public interface NumberFormatter {
    float format(float value);
    int format(int value);
    double format(double value);
}
