package com.stetcho.smartomagneter.framework.measurement.model;

/**
 * Model that represents a delay/rate
 */
public class SensorDelayModel {
    private String mTitle;
    private int mDelay;

    public String getTitle() {
        return mTitle;
    }

    public int getDelay() {
        return mDelay;
    }

    public SensorDelayModel(final String title, final int delay) {
        this.mTitle = title;
        this.mDelay = delay;
    }
}
