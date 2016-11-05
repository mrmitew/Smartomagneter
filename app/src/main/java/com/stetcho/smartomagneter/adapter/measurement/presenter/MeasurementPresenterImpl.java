package com.stetcho.smartomagneter.adapter.measurement.presenter;

import com.stetcho.smartomagneter.domain.formatter.NumberFormatter;
import com.stetcho.smartomagneter.domain.measurement.AbstractMagneticSensorManager;
import com.stetcho.smartomagneter.domain.measurement.formatter.SensorDataNumberFormatterImpl;
import com.stetcho.smartomagneter.domain.measurement.listener.OnSensorEventListener;
import com.stetcho.smartomagneter.framework.measurement.model.JavaMagneticSensorManager;

public class MeasurementPresenterImpl implements MeasurementPresenter, OnSensorEventListener {
    private AbstractMagneticSensorManager mMagneticSensorManager;
    private NumberFormatter mSensorDataNumberFormatter;
    private View mView;

    @Override
    public AbstractMagneticSensorManager getMagneticSensorManager() {
        return mMagneticSensorManager;
    }

    @Override
    public void setMagneticSensorManager(final AbstractMagneticSensorManager magneticSensorManager) {
        if (mMagneticSensorManager != null) {
            // If we are switching the magnetic sensor manager implementation with another one at
            // runtime, let's remove the listener
            mMagneticSensorManager.setListener(null);
        }
        mMagneticSensorManager = magneticSensorManager;
        mMagneticSensorManager.setListener(this);
    }

    @Override
    public void setView(final MeasurementPresenter.View view) {
        mView = view;
    }

    public MeasurementPresenterImpl(final AbstractMagneticSensorManager magneticSensorManager,
                                    final NumberFormatter sensorDataNumberFormatter) {
        mMagneticSensorManager = magneticSensorManager;
        mSensorDataNumberFormatter = sensorDataNumberFormatter;
        mMagneticSensorManager.setListener(this);
    }

    @Override
    public void unregisterSensorEvents() {
        mMagneticSensorManager.unregisterSensorListener();
    }

    @Override
    public void registerSensorEvents(final int delay) {
        setSensorDelay(delay);
    }

    @Override
    public void setSensorDelay(final int level) {
        mMagneticSensorManager.setSensorDelay(level);
    }

    @Override
    public float getMagneticFieldStrength(final float[] vector) {
        return (float) Math.sqrt((vector[0] * vector[0]) +
                (vector[1] * vector[1]) +
                (vector[2] * vector[2]));
    }

    @Override
    public void onSensorDataChanged(final float[] vector) {
        if (mView == null) return;
        mView.setNewMagneticStrength(
                mSensorDataNumberFormatter.format(getMagneticFieldStrength(vector)));
        vector[0] = mSensorDataNumberFormatter.format(vector[0]);
        vector[1] = mSensorDataNumberFormatter.format(vector[1]);
        vector[2] = mSensorDataNumberFormatter.format(vector[2]);
        mView.setNewVector(vector);
    }

    @Override
    public void onAccuracyChanged(final int accuracy) {
        if (mView == null) return;
        mView.setAccuracy(accuracy);
    }

    @Override
    public void onNoCalibrationNeeded() {
        if (mView == null) return;
        mView.setNoCalibrationNeeded();
    }

    @Override
    public void onCalibrationNeeded() {
        if (mView == null) return;
        mView.setNeedsCalibration();
    }
}
