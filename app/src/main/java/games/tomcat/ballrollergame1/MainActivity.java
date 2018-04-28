package games.tomcat.ballrollergame1;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ConfigurationInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    /** Hold a reference to our GLSurfaceView */
    private MyGLSurfaceView mGLSurfaceView;
    private MainRenderer mRenderer;
    private SensorManager sensorManager;

    private float xPos, xAccel, xVel = 0.0f;
    private float yPos, yAccel, yVel = 0.0f;
    private float xMax, yMax;

    private TextView text, name;

    Context activityContext;
    CurrentPlayer currentPlayer;
    int count = 0;
    int score = 0;
    Config config;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        activityContext = this.getApplicationContext();

        //mGLSurfaceView = new MyGLSurfaceView(activityContext);


        // Check if the system supports OpenGL ES 2.0.
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.


            final DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            //getWindowManager().getDefaultDisplay().getSize(size);

            //activityContext = this.Context;
            // Set the renderer to our demo renderer, defined below.
            setContentView(R.layout.activity_main);
            mGLSurfaceView = (MyGLSurfaceView) findViewById(R.id.glSurfaceViewID);
            mGLSurfaceView.setEGLContextClientVersion(2);
            mRenderer = new MainRenderer(activityContext);
            mGLSurfaceView.setRenderer(mRenderer, displayMetrics.density);
            config = new Config(activityContext);
            currentPlayer = new CurrentPlayer(activityContext);
            config.loadConfig();
            String player = currentPlayer.getPlayer();

            text = (TextView) findViewById(R.id.scoreText);
            text.setText("Score = ");
            name = (TextView) findViewById(R.id.nameText);
            name.setText(player);


            Thread t = new Thread() {
                @Override
                public void run() {
                    while (!isInterrupted()) {
                        try {
                            Thread.sleep(500); //half second
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    score = mRenderer.getScore();
                                    text.setText("Score = " + String.valueOf(score));
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            t.start();






            //Button button = (Button) findViewById(R.id.buttonID);
            //button.setText("NOPE");






            //xMax = (float) size.x - 100;
            //yMax = (float) size.y - 100;

            //sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        }
        else
        {
            // This is where you could create an OpenGL ES 1.x compatible
            // renderer if you wanted to support both ES 1 and ES 2.
            return;
        }

        //setContentView(mGLSurfaceView);

    }

    @Override
    protected void onResume()
    {
        // The activity must call the GL surface view's onResume() on activity onResume().
        super.onResume();
        mGLSurfaceView = (MyGLSurfaceView) findViewById(R.id.glSurfaceViewID);
        mGLSurfaceView.onResume();
        if(config.music) mRenderer.mediaPlayer.start();

    }

    @Override
    protected void onPause()
    {
        // The activity must call the GL surface view's onPause() on activity onPause().
        super.onPause();
        mGLSurfaceView = (MyGLSurfaceView) findViewById(R.id.glSurfaceViewID);
        mGLSurfaceView.onPause();
        if(config.music) mRenderer.mediaPlayer.pause();
    }

    protected void onStart(){
        super.onStart();
        //sensorManager.registerListener((SensorEventListener) this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onStop() {
        super.onStop();
        //sensorManager.unregisterListener((SensorEventListener) this);
    }


    /*
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            xAccel = sensorEvent.values[0];
            yAccel = -sensorEvent.values[1];
            updateBall();
        }
    }

    public void updateBall(){
        float frameTime = 0.666f;
        xVel += (xAccel * frameTime);
        yVel += (yAccel * frameTime);

        float xS = (xVel / 2) * frameTime;
        float yS = (yVel / 2) * frameTime;

        xPos -= xS;
        yPos -= yS;

        /*
        if (xPos > xMax) {
            xPos = xMax;
        } else if (xPos < 0) {
            xPos = 0;
        }

        if (yPos > yMax) {
            yPos = yMax;
        } else if (yPos < 0) {
            yPos = 0;
        }

    }
    */

    public TextView getScoreText(){
        TextView  text1 = (TextView) findViewById(R.id.scoreText);
        return text1;

    }


    /*
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onFlushCompleted(Sensor sensor) {

    }
    */
}
