package com.stetcho.smartomagneter.domain.measurement.listener;

/**
 * Interface for classes that listen for sensor events
 */
public interface OnSensorEventListener {
    void onSensorDataChanged(float[] vector);
    void onAccuracyChanged(int accuracy);
    void onNoCalibrationNeeded();
    void onCalibrationNeeded();
}
