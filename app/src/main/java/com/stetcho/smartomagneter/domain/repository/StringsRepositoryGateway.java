package com.stetcho.smartomagneter.domain.repository;

/**
 * Repository for strings
 */
public interface StringsRepositoryGateway {
    String getString(final int resourceId, Object... formatArgs);
    String getString(int resourceId);
    String getAccuracyText(int accuracy);
}
