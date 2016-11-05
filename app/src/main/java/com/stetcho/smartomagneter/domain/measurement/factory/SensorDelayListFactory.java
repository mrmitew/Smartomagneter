package com.stetcho.smartomagneter.domain.measurement.factory;

import com.stetcho.smartomagneter.framework.measurement.model.SensorDelayModel;

import java.util.List;

/**
 * Factory for creating list with {@link SensorDelayModel}
 */
public interface SensorDelayListFactory {
    List<SensorDelayModel> create(int minDelay, int maxDelay);
}
