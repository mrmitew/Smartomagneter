package com.stetcho.smartomagneter.framework.measurement.model;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;

import com.stetcho.smartomagneter.domain.measurement.AbstractMagneticSensorManager;
import com.stetcho.smartomagneter.domain.measurement.listener.OnSensorEventListener;

/**
 * Java implementation for working with Android's SensorManager
 */
public class JavaMagneticSensorManager extends AbstractMagneticSensorManager
        implements SensorEventListener {

    private OnSensorEventListener mOnSensorEventListener;

    /*
     * Dependencies
     */
    private final SensorManager mSensorManager;
    private final Sensor mMagneticSensor;

    public JavaMagneticSensorManager(SensorManager sensorManager, Sensor sensor) {
        mSensorManager = sensorManager;
        mMagneticSensor = sensor;
    }

    @Override
    public void registerSensorListener(final int delay) {
        switch (delay) {
            case 10000:
            case 0:
                mSensorManager.registerListener(this, mMagneticSensor,
                        android.hardware.SensorManager.SENSOR_DELAY_FASTEST);
                break;

            case android.hardware.SensorManager.SENSOR_DELAY_GAME:
            case 20000:
                mSensorManager.registerListener(this, mMagneticSensor,
                        android.hardware.SensorManager.SENSOR_DELAY_GAME);
                break;

            case android.hardware.SensorManager.SENSOR_DELAY_UI:
            case 60000:
            case 66667:
                mSensorManager.registerListener(this, mMagneticSensor,
                        android.hardware.SensorManager.SENSOR_DELAY_UI);
                break;

            case android.hardware.SensorManager.SENSOR_DELAY_NORMAL:
            case 200000:
                mSensorManager.registerListener(this, mMagneticSensor,
                        android.hardware.SensorManager.SENSOR_DELAY_NORMAL);
                break;

            default:
                mSensorManager.registerListener(this, mMagneticSensor, getMaxSensorDelay());
        }
    }

    @Override
    public void registerSensorListener() {
        mSensorManager.registerListener(this, mMagneticSensor, DEFAULT_SENSOR_DELAY);
    }

    @Override
    public void unregisterSensorListener() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void setSensorDelay(int delay) {
        unregisterSensorListener();
        registerSensorListener(delay);
    }

    @Override
    public void setListener(final OnSensorEventListener onSensorEventListener) {
        mOnSensorEventListener = onSensorEventListener;
    }

    @Override
    public int getMinSensorDelay() {
        return mMagneticSensor.getMinDelay();
    }

    @Override
    public int getMaxSensorDelay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return mMagneticSensor.getMaxDelay();
        }
        return SensorManager.SENSOR_DELAY_NORMAL;
    }

    @Override
    public int getDefaultSensorDelay() {
        return DEFAULT_SENSOR_DELAY;
    }

    @Override
    public void onSensorChanged(final SensorEvent sensorEvent) {
        if (mOnSensorEventListener == null) return;
        mOnSensorEventListener.onSensorDataChanged(sensorEvent.values);
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
        if (mOnSensorEventListener == null) return;

        mOnSensorEventListener.onAccuracyChanged(accuracy);

        // Require user to always calibrate if the accuracy is not high
        if (accuracy == SensorManager.SENSOR_STATUS_ACCURACY_HIGH) {
            mOnSensorEventListener.onNoCalibrationNeeded();
        } else {
            mOnSensorEventListener.onCalibrationNeeded();
        }
    }
}
