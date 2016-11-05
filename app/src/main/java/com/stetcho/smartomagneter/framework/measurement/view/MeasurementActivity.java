package com.stetcho.smartomagneter.framework.measurement.view;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.stetcho.smartomagneter.R;
import com.stetcho.smartomagneter.adapter.measurement.presenter.MeasurementPresenter;
import com.stetcho.smartomagneter.adapter.measurement.presenter.MeasurementPresenterImpl;
import com.stetcho.smartomagneter.domain.formatter.StringFormatter;
import com.stetcho.smartomagneter.domain.measurement.AbstractMagneticSensorManager;
import com.stetcho.smartomagneter.domain.measurement.factory.SensorDelayAdapterFactory;
import com.stetcho.smartomagneter.domain.measurement.factory.SensorDelayListFactory;
import com.stetcho.smartomagneter.domain.measurement.formatter.SensorDataNumberFormatterImpl;
import com.stetcho.smartomagneter.domain.measurement.formatter.SensorDelayFormatterImpl;
import com.stetcho.smartomagneter.domain.repository.StringsRepositoryGateway;
import com.stetcho.smartomagneter.framework.measurement.adapter.SensorDelayAdapter;
import com.stetcho.smartomagneter.framework.measurement.factory.SensorDelayAdapterFactoryImpl;
import com.stetcho.smartomagneter.framework.measurement.factory.SensorDelayListFactoryImpl;
import com.stetcho.smartomagneter.framework.measurement.model.JavaMagneticSensorManager;
import com.stetcho.smartomagneter.framework.measurement.model.NativeMagneticSensorManager;
import com.stetcho.smartomagneter.framework.measurement.model.SensorDelayModel;
import com.stetcho.smartomagneter.framework.repository.StringsRepositoryGatewayImpl;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import butterknife.OnLongClick;

public class MeasurementActivity extends AppCompatActivity
        implements MeasurementPresenter.View {
    private static final String TAG = MeasurementActivity.class.getSimpleName();

    /*
     * Views
     */
    @BindView(R.id.vg_root)
    LinearLayout mVgRoot;

    @BindView(R.id.tv_magnetic_field_strength)
    TextView mTvMagneticFieldStrength;

    @BindView(R.id.sp_sensor_delay)
    Spinner mSpSensorDelay;

    @BindView(R.id.tv_accuracy)
    TextView mTvAccuracy;

    @BindView(R.id.tv_calibration_needed)
    TextView mTvCalibrationNeeded;

    @BindView(R.id.tv_magnetometer_data_x)
    TextView mTvMagnetometerDataX;

    @BindView(R.id.tv_magnetometer_data_y)
    TextView mTvMagnetometerDataY;

    @BindView(R.id.tv_magnetometer_data_z)
    TextView mTvMagnetometerDataZ;

    @BindView(R.id.tv_sensor_manager_implementation)
    TextView mTvSensorManagerImplementation;

    /*
     * Dependencies
     */
    private MeasurementPresenter mMeasurementPresenter;
    private StringsRepositoryGateway mStringsResources;
    private StringFormatter mSensorDelayNumberFormatter;
    private AbstractMagneticSensorManager mJavaMagneticSensorManager;
    private AbstractMagneticSensorManager mNativeMagneticSensorManager;
    private SensorDelayListFactory mSensorDelayListFactory;
    private SensorDelayAdapterFactory mSensorDelayAdapterFactory;
    private AbstractMagneticSensorManager mCurrentMagneticSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Make it full screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);
        injectDependencies();
    }

    /**
     * Construction and injection of dependencies
     * TODO: If we were to use a Dependency Injection (DI) framework would have been much better
     */
    private void injectDependencies() {
        /**
         * Inject all views with Butterknife
         */
        ButterKnife.bind(this);

        /**
         * Construct dependencies for {@link AbstractMagneticSensorManager}
         */
        final SensorManager androidSensorManager =
                (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor magneticFieldSensor =
                androidSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        /**
         * Construct the dependencies for {@link MeasurementPresenterImpl}
         */
        mJavaMagneticSensorManager =
                new JavaMagneticSensorManager(androidSensorManager, magneticFieldSensor);
        mNativeMagneticSensorManager = new NativeMagneticSensorManager();
        SensorDataNumberFormatterImpl sensorDataNumberFormatter =
                new SensorDataNumberFormatterImpl();
        mCurrentMagneticSensorManager = mJavaMagneticSensorManager;

        /**
         * Construct the presenter {@link MeasurementPresenterImpl}
         */
        mMeasurementPresenter = new MeasurementPresenterImpl(mJavaMagneticSensorManager,
                sensorDataNumberFormatter);

        /**
         * Construct dependencies for the activity
         */
        mSensorDelayNumberFormatter = new SensorDelayFormatterImpl();
        mStringsResources = new StringsRepositoryGatewayImpl(this);
        mSensorDelayListFactory =
                new SensorDelayListFactoryImpl(this, mSensorDelayNumberFormatter);
        mSensorDelayAdapterFactory =
                new SensorDelayAdapterFactoryImpl(this, mSensorDelayListFactory,
                        mCurrentMagneticSensorManager);

        onPresenterCreated();
        onDependenciesInjected();
    }

    private void onDependenciesInjected() {
        mSpSensorDelay.setAdapter(mSensorDelayAdapterFactory.create());
        // FIXME: This relies on that the index is the same as the number defined in the Android SDK
        // Pure coincidence. Prone to mistakes.
        mSpSensorDelay.setSelection(AbstractMagneticSensorManager.DEFAULT_SENSOR_DELAY);
        // That's the default
        updateSensorManagerImplementationToJavaUi();
    }

    private void onPresenterCreated() {
        mMeasurementPresenter.setView(this);
    }

    @Override
    public void setNewVector(final float[] vector) {
        mTvMagnetometerDataX.setText(String.valueOf(vector[0]));
        mTvMagnetometerDataY.setText(String.valueOf(vector[1]));
        mTvMagnetometerDataZ.setText(String.valueOf(vector[2]));
    }

    @Override
    public void setAccuracy(final int accuracy) {
        mTvAccuracy.setText(mStringsResources.getAccuracyText(accuracy));
    }

    @Override
    public void setNewMagneticStrength(final float magneticFieldStrength) {
        mTvMagneticFieldStrength.setText(getString(R.string.magnetic_field_strength_1s,
                String.valueOf(magneticFieldStrength)));
    }

    @Override
    public void setNeedsCalibration() {
        mTvCalibrationNeeded.setVisibility(View.VISIBLE);
    }

    @Override
    public void setNoCalibrationNeeded() {
        mTvCalibrationNeeded.setVisibility(View.GONE);
    }


    @OnItemSelected(R.id.sp_sensor_delay)
    protected void onSensorDelayOptionSelected(int index) {
        mMeasurementPresenter.setSensorDelay(
                ((SensorDelayAdapter) mSpSensorDelay.getAdapter())
                        .getItem(index)
                        .getDelay());
    }

    @Override
    protected void onResume() {
        super.onResume();
        int delay = AbstractMagneticSensorManager.DEFAULT_SENSOR_DELAY;
        if (mSpSensorDelay != null) {
            if (mSpSensorDelay.getCount() > 0) {
                delay = ((SensorDelayModel) mSpSensorDelay.getSelectedItem()).getDelay();
            }
        }
        mMeasurementPresenter.registerSensorEvents(delay);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMeasurementPresenter.unregisterSensorEvents();
    }

    @OnLongClick(R.id.vg_root)
    protected boolean switchSensorManagerImplementation() {
        // Normally if we want to make our view completely passive, we should delegate all events
        // to the presenter. This is probably the only time I don't want to do it since
        // I didn't like the idea of having the presenter knowing of all possible implementations of
        // the AbstractMagneticSensorManager
        if (mMeasurementPresenter.getMagneticSensorManager() instanceof JavaMagneticSensorManager) {
            setMagneticSensorManagerToNative();
        } else {
            setMagneticSensorManagerToJava();
        }
        return true;
    }

    @Override
    public void setMagneticSensorManagerToJava() {
        mCurrentMagneticSensorManager = mJavaMagneticSensorManager;
        mMeasurementPresenter.unregisterSensorEvents();
        mMeasurementPresenter.setMagneticSensorManager(mJavaMagneticSensorManager);
        mMeasurementPresenter.setSensorDelay(
                ((SensorDelayModel) mSpSensorDelay.getSelectedItem()).getDelay());

        updateSensorManagerImplementationToJavaUi();

        Snackbar.make(mVgRoot,
                getString(R.string.sensor_manager_implementation_changed_to_1s,
                        getString(R.string.sensor_manager_type_java)),
                Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void setMagneticSensorManagerToNative() {
        mCurrentMagneticSensorManager = mNativeMagneticSensorManager;
        mMeasurementPresenter.unregisterSensorEvents();
        mMeasurementPresenter.setMagneticSensorManager(mNativeMagneticSensorManager);
        mMeasurementPresenter.setSensorDelay(
                ((SensorDelayModel) mSpSensorDelay.getSelectedItem()).getDelay());

        updateSensorManagerImplementationToNativeUi();

        Snackbar.make(mVgRoot,
                getString(R.string.sensor_manager_implementation_changed_to_1s,
                        getString(R.string.sensor_manager_type_native)),
                Snackbar.LENGTH_SHORT)
                .show();
    }

    private void updateSensorManagerImplementationToJavaUi() {
        mTvSensorManagerImplementation.setText(
                getString(R.string.java_sensor_manager_implementation));
        mTvSensorManagerImplementation.setTextColor(getResources().getColor(R.color.black));
    }

    private void updateSensorManagerImplementationToNativeUi() {
        mTvSensorManagerImplementation.setText(
                getString(R.string.native_sensor_manager_implementation));
        mTvSensorManagerImplementation.setTextColor(getResources().getColor(R.color.colorAccent));
    }
}
