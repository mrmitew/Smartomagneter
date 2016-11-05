package com.stetcho.smartomagneter.framework.measurement.factory;

import android.content.Context;

import com.stetcho.smartomagneter.R;
import com.stetcho.smartomagneter.domain.formatter.StringFormatter;
import com.stetcho.smartomagneter.domain.measurement.factory.SensorDelayListFactory;
import com.stetcho.smartomagneter.framework.measurement.model.SensorDelayModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for creating a list with {@link SensorDelayModel}
 */
public class SensorDelayListFactoryImpl implements SensorDelayListFactory {
    private final Context mContext;
    private final StringFormatter mFormatter;

    public SensorDelayListFactoryImpl(Context context, StringFormatter formatter) {
        mContext = context;
        mFormatter = formatter;
    }

    public List<SensorDelayModel> create(int minDelay, int maxDelay) {
        ArrayList<SensorDelayModel> sensorDelayModelArrayList = new ArrayList<>(4);
        sensorDelayModelArrayList.add(
                new SensorDelayModel(
                        mContext.getString(R.string.delay_fastest_1s, mFormatter.format(minDelay)),
                        minDelay));
        sensorDelayModelArrayList.add(new SensorDelayModel(mContext.getString(R.string.delay_game), 20000));
        sensorDelayModelArrayList.add(new SensorDelayModel(mContext.getString(R.string.delay_ui), 60000));
        sensorDelayModelArrayList.add(new SensorDelayModel(mContext.getString(R.string.delay_normal), 200000));
        sensorDelayModelArrayList.add(
                new SensorDelayModel(
                        mContext.getString(R.string.delay_slowest_1s, mFormatter.format(maxDelay)),
                        maxDelay));
        return sensorDelayModelArrayList;
    }
}