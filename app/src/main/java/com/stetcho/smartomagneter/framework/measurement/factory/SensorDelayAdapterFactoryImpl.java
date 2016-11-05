package com.stetcho.smartomagneter.framework.measurement.factory;

import android.content.Context;

import com.stetcho.smartomagneter.domain.measurement.AbstractMagneticSensorManager;
import com.stetcho.smartomagneter.domain.measurement.factory.SensorDelayAdapterFactory;
import com.stetcho.smartomagneter.domain.measurement.factory.SensorDelayListFactory;
import com.stetcho.smartomagneter.framework.measurement.adapter.SensorDelayAdapter;

/**
 * Factory for creating {@link SensorDelayAdapter}
 */
public class SensorDelayAdapterFactoryImpl implements SensorDelayAdapterFactory {
    private final Context mContext;
    private final SensorDelayListFactory mSensorDelayListFactory;
    private final AbstractMagneticSensorManager mMagneticSensorManager;

    public SensorDelayAdapterFactoryImpl(Context context,
                                         SensorDelayListFactory sensorDelayListFactory,
                                         AbstractMagneticSensorManager magneticSensorManager) {
        mContext = context;
        mSensorDelayListFactory = sensorDelayListFactory;
        mMagneticSensorManager = magneticSensorManager;
    }

    public SensorDelayAdapter create() {
        return new SensorDelayAdapter(mContext,
                mSensorDelayListFactory.create(mMagneticSensorManager.getMinSensorDelay(),
                        mMagneticSensorManager.getMaxSensorDelay()));
    }
}