package com.stetcho.smartomagneter.domain.measurement.formatter;

import com.stetcho.smartomagneter.domain.formatter.StringFormatter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Class used to format the various sensor's delays/rates
 */
public class SensorDelayFormatterImpl implements StringFormatter {
    private final DecimalFormat mDecimalFormat;
    private DecimalFormatSymbols mDecimalFormatSymbols = new DecimalFormatSymbols();

    public SensorDelayFormatterImpl() {
        mDecimalFormatSymbols.setDecimalSeparator(',');
        mDecimalFormat = (DecimalFormat) DecimalFormat.getInstance();
        mDecimalFormat.setDecimalFormatSymbols(mDecimalFormatSymbols);
    }

    @Override
    public String format(final int value) {
        return mDecimalFormat.format(value);
    }

    @Override
    public String format(final float value) {
        return mDecimalFormat.format(value);
    }

    @Override
    public String format(final double value) {
        return mDecimalFormat.format(value);
    }
}
