package com.stetcho.smartomagneter.framework.repository;

import android.content.Context;
import android.hardware.SensorManager;

import com.stetcho.smartomagneter.R;
import com.stetcho.smartomagneter.domain.repository.StringsRepositoryGateway;

/**
 * The concrete implementation of the {@link StringsRepositoryGateway} 
 */
public class StringsRepositoryGatewayImpl implements StringsRepositoryGateway {
    private final Context mContext;

    public StringsRepositoryGatewayImpl(final Context context) {
        mContext = context;
    }

    @Override
    public String getString(final int resourceId) {
        return mContext.getString(resourceId);
    }

    @Override
    public String getString(final int resourceId, Object... formatArgs) {
        return mContext.getString(resourceId, formatArgs);
    }

    @Override
    public String getAccuracyText(int accuracy) {
        String accuracyText;
        switch (accuracy) {
            case SensorManager.SENSOR_STATUS_UNRELIABLE:
                accuracyText = getString(R.string.unreliable_accuracy);
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                accuracyText = getString(R.string.high_accuracy);
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                accuracyText = getString(R.string.medium_accuracy);
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                accuracyText = getString(R.string.low_accuracy);
                break;
            default:
                accuracyText = getString(R.string.unknown_accuracy);
        }
        return accuracyText;
    }
}
