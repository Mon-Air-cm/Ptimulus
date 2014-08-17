package com.ptimulus.event;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.ptimulus.PtimulusService;

public class AccelerometerEvent implements SensorEventListener, IEvent {

    private final PtimulusService ptimulusService;
	private final SensorManager sensorManager;
    private final Sensor accelerometer;

	private SensorEvent lastSensorEvent;
    private long lastSensorEventTime;

	public AccelerometerEvent(PtimulusService ptimulusService, Context ctx) {
        this.ptimulusService = ptimulusService;
        this.lastSensorEvent = null;
        this.lastSensorEventTime = 0;
		
		// Find the accelerometer
		sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}

    /**
     * Enable the event source.
     */
    @Override
    public void startListening() {
        boolean accelSupported = sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        if (!accelSupported) {
            sensorManager.unregisterListener(this, accelerometer);
        }
    }

    /**
     * Disable the event source.
     */
    @Override
    public void stopListening() {
        sensorManager.unregisterListener(this);
    }

   /**
     * Timer tick from the service. Assumed to be 1Hz.
     */
    @Override
    public void tick() {
        if(lastSensorEvent != null)
            ptimulusService.sensorEvent(lastSensorEvent);
    }

    @Override
    public long dataAge() {
        return System.currentTimeMillis() - lastSensorEventTime;
    }

    @Override
    public String toString() {
        if(lastSensorEvent == null)
            return "No Telephony event yet";
        
        float x = lastSensorEvent.values[0];
        float y = lastSensorEvent.values[1];
        float z = lastSensorEvent.values[2];
       
        double magn = Math.sqrt(x*x + y*y + z*z) / 9.81d; 
        return String.format("%d sec | %.3f G",
        		Math.round(dataAge() / 1000f),
        		magn);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        lastSensorEventTime = System.currentTimeMillis();
        lastSensorEvent = sensorEvent;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}