package com.stetcho.smartomagneter.domain.measurement;

import com.stetcho.smartomagneter.domain.measurement.listener.OnSensorEventListener;

/**
 * Abstract class used by java and native implementation of working with the Android's SensorManager
 */
public abstract class AbstractMagneticSensorManager {
    public static final int DEFAULT_SENSOR_DELAY =
            android.hardware.SensorManager.SENSOR_DELAY_UI;

    public abstract void setListener(OnSensorEventListener onSensorEventListener);
    public abstract void registerSensorListener(int delay);
    public abstract void registerSensorListener();
    public abstract void unregisterSensorListener();
    public abstract void setSensorDelay(int delay);
    public abstract int getMinSensorDelay();
    public abstract int getMaxSensorDelay();
    public abstract int getDefaultSensorDelay();
}
