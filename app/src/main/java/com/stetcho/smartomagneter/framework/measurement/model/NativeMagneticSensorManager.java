package com.stetcho.smartomagneter.framework.measurement.model;

import com.stetcho.smartomagneter.domain.measurement.AbstractMagneticSensorManager;
import com.stetcho.smartomagneter.domain.measurement.listener.OnSensorEventListener;

/**
 * Implementation of {@link AbstractMagneticSensorManager} for working with Android's SensorManager
 * via native library (magnetic_sensor_manager-lib).
 */
public class NativeMagneticSensorManager extends AbstractMagneticSensorManager
        implements OnSensorEventListener {
    /**
     * Load the native library on application startup
     */
    static {
        System.loadLibrary("magnetic_sensor_manager-lib");
    }

    private OnSensorEventListener mOnSensorEventListener;

    /**
     * Native methods that are implemented by the native library, which is packaged with the
     * application.
     */
    private native void nativeRegisterListener();
    private native void nativeRegisterListenerWithDelay(int delay);
    private native void nativeUnregisterListener();
    private native int nativeGetDefaultSensorDelay();
    private native int nativeGetMinSensorDelay();
    private native int nativeGetMaxSensorDelay();
    private native int nativeGetCurrentSensorDelay();
    private native int nativeSetSensorDelay(int delay);

    public NativeMagneticSensorManager() {
    }

    @Override
    public void registerSensorListener(final int delay) {
        nativeRegisterListenerWithDelay(delay);
    }

    @Override
    public void registerSensorListener() {
        nativeRegisterListener();
    }

    @Override
    public void unregisterSensorListener() {
        nativeUnregisterListener();
    }

    @Override
    public void setSensorDelay(final int delay) {
        nativeSetSensorDelay(delay);
    }

    @Override
    public void setListener(final OnSensorEventListener onSensorEventListener) {
        mOnSensorEventListener = onSensorEventListener;
    }

    @Override
    public int getMinSensorDelay() {
        return nativeGetMinSensorDelay();
    }

    @Override
    public int getMaxSensorDelay() {
        return nativeGetMaxSensorDelay();
    }

    @Override
    public int getDefaultSensorDelay() {
        return nativeGetDefaultSensorDelay();
    }

    @Override
    public void onSensorDataChanged(final float[] vector) {
        if (mOnSensorEventListener == null) return;
        mOnSensorEventListener.onSensorDataChanged(vector);
    }

    @Override
    public void onAccuracyChanged(final int accuracy) {
        if (mOnSensorEventListener == null) return;
        mOnSensorEventListener.onAccuracyChanged(accuracy);
    }

    @Override
    public void onNoCalibrationNeeded() {
        if (mOnSensorEventListener == null) return;
        mOnSensorEventListener.onNoCalibrationNeeded();
    }

    @Override
    public void onCalibrationNeeded() {
        if (mOnSensorEventListener == null) return;
        mOnSensorEventListener.onCalibrationNeeded();
    }
}
