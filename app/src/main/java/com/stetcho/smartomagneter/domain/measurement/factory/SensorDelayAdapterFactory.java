package com.stetcho.smartomagneter.domain.measurement.factory;

import com.stetcho.smartomagneter.framework.measurement.adapter.SensorDelayAdapter;

/**
 * Factory for creating {@link SensorDelayAdapter}
 */
public interface SensorDelayAdapterFactory {
    SensorDelayAdapter create();
}
