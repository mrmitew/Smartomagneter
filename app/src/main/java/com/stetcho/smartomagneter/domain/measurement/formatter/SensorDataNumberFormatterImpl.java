package com.stetcho.smartomagneter.domain.measurement.formatter;

import com.stetcho.smartomagneter.domain.formatter.NumberFormatter;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Class used to format the numeric values received by the sensor manager
 */
public class SensorDataNumberFormatterImpl implements NumberFormatter {
    private static final NumberFormat sNumberFormat = new DecimalFormat("#.##");

    public float format(float value) {
        return Float.valueOf(sNumberFormat.format(value));
    }

    @Override
    public int format(final int value) {
        return Integer.valueOf(sNumberFormat.format(value));
    }

    @Override
    public double format(final double value) {
        return Double.valueOf(sNumberFormat.format(value));
    }
}
