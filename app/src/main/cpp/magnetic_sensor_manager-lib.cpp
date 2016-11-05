#include <jni.h>
#include <string>
#include "logutils.h"
#include <android/sensor.h>

#define BUFFER_SIZE 100
#define VERBOSE_LOGGING false

/**
 * These constants and the corresponding values were taken from:
 * https://android.googlesource.com/platform/frameworks/base/+/android-7.1.0_r4/core/java/android/hardware/SensorManager.java
 */
#define SENSOR_DELAY_FASTEST 0
#define SENSOR_DELAY_GAME 20000
#define SENSOR_DELAY_UI 66667
#define SENSOR_DELAY_NORMAL 200000
#define DEFAULT_SENSOR_DELAY SENSOR_DELAY_UI

extern "C" {

ASensorEventQueue *gSensorEventQueue;
JavaVM *gpJvm = NULL;
jclass gClsNativeSensorManager;
jobject gObjNativeSensorManager;
bool gListenForSensorDataEvents = false;

void free_memory(JNIEnv *env);
JNIEnv *getEnv();

int gIntSensorStatus;
int gIntCurrentSensorDelay;

/**
 * This method is called when the native library is loaded
 */
jint JNI_OnLoad(JavaVM *pJavaVm, void *reserved) {
    // Store a pointer to the JavaVM instance
    gpJvm = pJavaVm;
    gIntSensorStatus = 92;
    return JNI_VERSION_1_6;
}

/**
 * Called when the class loader containing the native library is garbage collected.
 * We'll free any memory that was or might be previously allocated
 */
void JNI_OnUnload(JavaVM *pJavaVm, void *reserved) {
    free_memory(getEnv());
}

/**
 * Returns JNI interface pointer to access Java VM features
 */
JNIEnv *getEnv() {
    JNIEnv *env;
    // Get JNI interface pointer if the current thread is already attached to the VM
    int status = gpJvm->GetEnv((void **) &env, JNI_VERSION_1_6);
    if (status < 0) {
        // Attach the current thread to the Java VM and return a JNI interface pointer in the env arg
        status = gpJvm->AttachCurrentThread(&env, NULL);
        if (status < 0) {
            return NULL;
        }
    }
    return env;
}

/**
 * @return int 1 to continue listening for callbacks; 0 to stop listening for callbacks.
 */
static int get_sensor_events(int fd, int events, void *data) {
    ASensorEvent event;
    while (ASensorEventQueue_getEvents(gSensorEventQueue, &event, 1) > 0 &&
           gListenForSensorDataEvents) {
        if (event.type == ASENSOR_TYPE_MAGNETIC_FIELD) {
//            if (VERBOSE_LOGGING) {
//                LOGV("f(x,y,z,t): %f %f %f %lld", event.magnetic.x, event.magnetic.y,
//                     event.magnetic.z, event.timestamp);
//            }

            JNIEnv *env = getEnv();

            // Create a java float array
            jfloatArray jfVector = env->NewFloatArray(3);
            if (jfVector != NULL) { // Check if there was enough memory to allocate the array
                if (float *ptr = env->GetFloatArrayElements(jfVector, NULL)) {
                    // Store the vector elements into the array
                    ptr[0] = (jfloat) event.magnetic.x;
                    ptr[1] = (jfloat) event.magnetic.y;
                    ptr[2] = (jfloat) event.magnetic.z;
                    // Free memory
                    env->ReleaseFloatArrayElements(jfVector, ptr, JNI_COMMIT);
                }
            }

            // Get the method id of NativeMagnetometerManager#onSensorDataChanged, so we can call it
            // In order to do that, we need the name of the method, its parameters and return type
            // (F)V denotes that accepts one argument of type float[] and it returns a void
            jmethodID onSensorDataChangedId = env->GetMethodID(gClsNativeSensorManager,
                                                               "onSensorDataChanged", "([F)V");

            // Call the method and pass the magneticStrength value as an argument
            // Not that the native method name depends on the type of java method we want to call
            // In this case is CallVoidMethod since we are calling a method returning a void
            env->CallVoidMethod(gObjNativeSensorManager, onSensorDataChangedId, jfVector);

            // Get the accuracy
            int status = (int) event.magnetic.status;
            if (gIntSensorStatus != status) {
                // Accuracy has changed
                LOGD("Sensor accuracy has changed");

                // Set the current sensor status
                gIntSensorStatus = status;

                jmethodID onAccuracyChangedId = env->GetMethodID(gClsNativeSensorManager,
                                                                 "onAccuracyChanged", "(I)V");
                env->CallVoidMethod(gObjNativeSensorManager, onAccuracyChangedId,
                                    status);

                if (status != ASENSOR_STATUS_ACCURACY_HIGH) {
                    jmethodID onCalibrationNeededId = env->GetMethodID(gClsNativeSensorManager,
                                                                       "onCalibrationNeeded",
                                                                       "()V");
                    env->CallVoidMethod(gObjNativeSensorManager, onCalibrationNeededId, status);
                } else {
                    LOGD("Sensor accuracy got great");
                    jmethodID onNoCalibrationNeededId = env->GetMethodID(gClsNativeSensorManager,
                                                                         "onNoCalibrationNeeded",
                                                                         "()V");
                    env->CallVoidMethod(gObjNativeSensorManager, onNoCalibrationNeededId, status);
                }
            }
        }
    }
    return gListenForSensorDataEvents ? 1 : 0;
}

void register_listener_with_delay(JNIEnv *env, jobject jObjNativeSensorManager, jint delay) {
    // Get the class of the object
    jclass cls = env->GetObjectClass(jObjNativeSensorManager);

    // Get long term references, otherwise they might get deleted or the memory might move inside
    // the environment and the references will then become invalid
    gClsNativeSensorManager = (_jclass *) env->NewGlobalRef(cls);
    gObjNativeSensorManager = env->NewGlobalRef(jObjNativeSensorManager);

    // Release memory
    env->DeleteLocalRef(cls);

    // Get the looper associated with the current thread
    ALooper *looper = ALooper_forThread();

    if (looper == NULL) {
        // Prepares a looper associated with the current thread. If there was already a looper,
        // new one will not be created, but the existing one will be returned instead.
        looper = ALooper_prepare(ALOOPER_PREPARE_ALLOW_NON_CALLBACKS);
    }

    // This is the actual native sensor manager, provided by the Android NDK :)
    ASensorManager *sensorManager = ASensorManager_getInstance();
    const ASensor *magSensor =
            ASensorManager_getDefaultSensor(sensorManager, ASENSOR_TYPE_MAGNETIC_FIELD);

    if (magSensor == NULL) {
        LOGE("Magnetic sensor was not loaded");
        return;
    }

    void *sensor_data = malloc(BUFFER_SIZE);

    // Create a new sensor event queue and associate it with the looper
    gSensorEventQueue =
            ASensorManager_createEventQueue(sensorManager, looper, 3, get_sensor_events,
                                            sensor_data);
    int status = ASensorEventQueue_enableSensor(gSensorEventQueue, magSensor);
    if (status < 0) {
        LOGE("Magnetic sensor was not enabled. Error code: %d", status);
        return;
    }

    gIntCurrentSensorDelay = delay;
    status = ASensorEventQueue_setEventRate(gSensorEventQueue, magSensor, delay);

    if (status < 0) {
        LOGE("Event rate was not properly set. Error code: %d", status);
        return;
    }

    gListenForSensorDataEvents = true;

    LOGI("Magnetic sensor was successfully enabled. Event rate set to: %d", delay);
}

void register_listener(JNIEnv *env, jobject jObjNativeSensorManager) {
    register_listener_with_delay(env, jObjNativeSensorManager, (int) DEFAULT_SENSOR_DELAY);
}

void unregister_listener(JNIEnv *env) {
    if(!gListenForSensorDataEvents)
        return;

    ASensorManager *sensorManager = ASensorManager_getInstance();
    const ASensor *magSensor =
            ASensorManager_getDefaultSensor(sensorManager, ASENSOR_TYPE_MAGNETIC_FIELD);

    if (magSensor == NULL) {
        LOGE("Magnetic sensor was not loaded");
        return;
    }

    int status = ASensorEventQueue_disableSensor(gSensorEventQueue, magSensor);
    if(status < 0) {
        LOGE("An error occurred while disabling the magnetic sensor. Code: %d", status);
    }

    LOGD("Unregister for sensor data events");
    gListenForSensorDataEvents = false;
    gIntSensorStatus = 92;
}

void free_memory(JNIEnv *env) {
    // Release memory
    env->DeleteGlobalRef(gClsNativeSensorManager);
    env->DeleteGlobalRef(gObjNativeSensorManager);
}

/**
 * JNI Exports
 */

void Java_com_stetcho_smartomagneter_framework_measurement_model_NativeMagneticSensorManager_nativeUnregisterListener(
        JNIEnv *env,
        jobject jObjNativeSensorManager) {
    unregister_listener(env);
}

void Java_com_stetcho_smartomagneter_framework_measurement_model_NativeMagneticSensorManager_nativeRegisterListenerWithDelay(
        JNIEnv *env,
        jobject jObjNativeSensorManager,
        jint sensorDelay) {
    register_listener_with_delay(env, jObjNativeSensorManager, (int) sensorDelay);
}

void Java_com_stetcho_smartomagneter_framework_measurement_model_NativeMagneticSensorManager_nativeSetSensorDelay(
        JNIEnv *env,
        jobject jObjNativeSensorManager,
        jint sensorDelay) {
    unregister_listener(env);
    register_listener_with_delay(env, jObjNativeSensorManager, (int) sensorDelay);
}

void Java_com_stetcho_smartomagneter_framework_measurement_model_NativeMagneticSensorManager_nativeRegisterListener(
        JNIEnv *env,
        jobject jObjNativeSensorManager) {
    register_listener(env, jObjNativeSensorManager);
}

jint Java_com_stetcho_smartomagneter_framework_measurement_model_NativeMagneticSensorManager_nativeGetCurrentSensorDelay(
        JNIEnv *env,
        jobject jObjNativeSensorManager) {
    return (jint) gIntCurrentSensorDelay;
}

jint Java_com_stetcho_smartomagneter_framework_measurement_model_NativeMagneticSensorManager_nativeGetMinSensorDelay(
        JNIEnv *env,
        jobject jObjNativeSensorManager) {
    ASensorManager *sensorManager = ASensorManager_getInstance();
    const ASensor *magSensor =
            ASensorManager_getDefaultSensor(sensorManager, ASENSOR_TYPE_MAGNETIC_FIELD);

    if (magSensor == NULL) {
        LOGE("Magnetic sensor was not loaded");
        return (jint) DEFAULT_SENSOR_DELAY;
    }

    return ASensor_getMinDelay(magSensor);
}

jint Java_com_stetcho_smartomagneter_framework_measurement_model_NativeMagneticSensorManager_nativeGetMaxSensorDelay(
        JNIEnv *env,
        jobject jObjNativeSensorManager) {
    return (jint) SENSOR_DELAY_NORMAL;
}

jint Java_com_stetcho_smartomagneter_framework_measurement_model_NativeMagneticSensorManager_nativeGetDefaultSensorDelay(
        JNIEnv *env,
        jobject jObjNativeSensorManager) {
    return (jint) DEFAULT_SENSOR_DELAY;
}
}