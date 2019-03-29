package com.anand.assignmenttorch;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Sensor m_LightSensor;
    SensorManager m_SensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Make sure you are in a dark place!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        m_SensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        m_LightSensor = m_SensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_SensorManager.registerListener(eventListener, m_LightSensor, 2*900*900);
    }

    @Override
    protected void onPause() {
        super.onPause();
        m_SensorManager.unregisterListener(eventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        m_SensorManager.unregisterListener(eventListener);
        turnOffFlash();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void turnOnFlash(){
        try {
            CameraManager cameraManager = (CameraManager) getApplicationContext().getSystemService(Context.CAMERA_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (String id : cameraManager.getCameraIdList()) {
                    if (cameraManager.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            cameraManager.setTorchMode(id, true);
                        }
                    }
                }
            }
        } catch (Exception e2) {
            Toast.makeText(getApplicationContext(), "Flash light couldn't be toggled: " + e2.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void turnOffFlash(){
        try {
            CameraManager cameraManager = (CameraManager) getApplicationContext().getSystemService(Context.CAMERA_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (String id : cameraManager.getCameraIdList()) {
                    if (cameraManager.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            cameraManager.setTorchMode(id, false);
                        }
                    }
                }
            }
        } catch (Exception e2) {
            Toast.makeText(getApplicationContext(), "Flash light couldn't be toggled: " + e2.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private SensorEventListener eventListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            float light = event.values[0];
            if (light < 4f) {
                turnOnFlash();
                Toast.makeText(MainActivity.this, "Flash on!", Toast.LENGTH_SHORT).show();
            }
            else
                turnOffFlash();
        }
    };

}