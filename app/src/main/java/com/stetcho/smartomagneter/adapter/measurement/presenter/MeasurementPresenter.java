package com.stetcho.smartomagneter.adapter.measurement.presenter;

import com.stetcho.smartomagneter.domain.measurement.AbstractMagneticSensorManager;

/**
 * Interface that the concrete measurement presenter {@link MeasurementPresenterImpl} will implement
 */
public interface MeasurementPresenter {
    /**
     * Interface for the view that will be represented by this presenter - e.g. Activity
     */
    interface View {
        void setNewVector(float[] vector);
        void setAccuracy(int accuracy);
        void setNewMagneticStrength(float magneticFieldStrength);
        void setNeedsCalibration();
        void setNoCalibrationNeeded();
        void setMagneticSensorManagerToJava();
        void setMagneticSensorManagerToNative();
    }
    void unregisterSensorEvents();
    void registerSensorEvents(int delay);
    void setView(MeasurementPresenter.View view);
    void setSensorDelay(int level);
    float getMagneticFieldStrength(float[] vector);
    AbstractMagneticSensorManager getMagneticSensorManager();
    void setMagneticSensorManager(AbstractMagneticSensorManager magneticSensorManager);
}
