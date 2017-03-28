package com.siriuxliu.myapplication1;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    /** Called when the activity is first created. */
    private TextView allNetWork;
    private Button scan;
    private Button start;
    private Button stop;
    private Button check;
    private WifiAdmin mWifiAdmin;
    // 扫描结果列表
    private List<ScanResult> list;
    private ScanResult mScanResult;
    private StringBuffer sb=new StringBuffer();

    //Sensors
    private TextView tvAccelerometer;
    private TextView tvMag;
    private TextView tvGravity;
    private SensorManager mSensorManager;
    private float [] gravity = new float[3];

    int flag=0;
    Timer timer = new Timer("gForceUpdate"); //SARAH ADDED

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWifiAdmin = new WifiAdmin(MainActivity.this);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        tvAccelerometer = (TextView) findViewById(R.id.Acc);
        tvMag = (TextView) findViewById(R.id.Mag);
        tvGravity = (TextView) findViewById(R.id.Gra);
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                updateGUI();
            }
        }, 0, 500);
        init();
    }
    public void init(){
        allNetWork = (TextView) findViewById(R.id.allNetWork);
        scan = (Button) findViewById(R.id.scan);
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        check = (Button) findViewById(R.id.check);
        scan.setOnClickListener(new MyListener());
        start.setOnClickListener(new MyListener());
        stop.setOnClickListener(new MyListener());
        check.setOnClickListener(new MyListener());
    }



    private class MyListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.scan://扫描网络
                    getAllNetWorkList();
                    //onSensorChanged();
                    break;
                case R.id.start://打开Wifi
                    mWifiAdmin.openWifi();
                   // timer.schedule(new firstTask(), 0,500);
                    Toast.makeText(MainActivity.this, "Current Status："+mWifiAdmin.checkState(), Toast.LENGTH_LONG).show();
                    break;
                case R.id.stop://关闭Wifi
                    stopMeasure();
                    mWifiAdmin.closeWifi();
                    Toast.makeText(MainActivity.this, "Current Status："+mWifiAdmin.checkState(), Toast.LENGTH_LONG).show();
                    break;
                case R.id.check://Wifi状态
                    Toast.makeText(MainActivity.this, "Current Status："+mWifiAdmin.checkState(), Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }

    }
    /**********************************************************************************************/
    /***************      WIFI                                 *******************/
    /**********************************************************************************************/
    String accelerometer;
    String mag;
    String grav;
    public boolean getAllNetWorkList(){
        flag=1;

        return true;
    }
    public boolean stopMeasure(){
        flag=0;

        return true;
    }


    /**********************************************************************************************/
    /*************** Accelerometer/Magneticmeter                                *******************/
    /**********************************************************************************************/
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    /**
     * 传感器数据变化时回调
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        //判断传感器类别

        switch (event.sensor.getType()) {


            case Sensor.TYPE_ACCELEROMETER: //加速度传感器
                accelerometer = "Accelerometer\n" + "x:"
                        + event.values[0] + "\n" + "y:"
                        + event.values[1]+ "\n" + "z:"
                        + event.values[2]+"\n";
                break;
            case Sensor.TYPE_GRAVITY://重力传感器
                //gravity[0] = event.values[0];//单位m/s^2
                //gravity[1] = event.values[1];
               // gravity[2] = event.values[2];
                grav = "Gravity\n" + "x:"
                        + event.values[0] + "\n" + "y:"
                        + event.values[1] + "\n" + "z:"
                        + event.values[2] +"\n";
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mag="Magnetic\n"+"x:"
                              +event.values[0]+"uT\n"+"y:"
                              +event.values[1]+"uT\n"+"z:"
                              +event.values[2]+"uT\n"+"\n";
                //tvAccelerometer.setText(accelerometer);
                break;
            default:
                break;
        }
    }
    /**
     * 界面获取焦点，按钮可以点击时回调
     */
    protected void onResume() {
        super.onResume();
        //注册加速度传感器
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),//传感器TYPE类型
                SensorManager.SENSOR_DELAY_UI);//采集频率
        //注册重力传感器
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_UI);
    }
    /**
     * 暂停Activity，界面获取焦点，按钮可以点击时回调
     */
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private void updateGUI() {
            /*
             * 推荐的一个刷新UI的方法
             * Activity.runOnUiThread（Runnable）
             * 在新的线程中更新UI
             * Runnable是一个接口，需要你实现run方法，上面的TimerTask就是实现了这个接口同样需要实现run方法
             * */
        runOnUiThread(new Runnable() {
            public void run() {
                if (flag==1){
                    // String currentG = currentAcceleration/SensorManager.STANDARD_GRAVITY
                    //         + "Gs";
                    tvAccelerometer.setText(accelerometer);
                    tvAccelerometer.invalidate();
                    tvMag.setText(mag);
                    tvMag.invalidate();
                    tvGravity.setText(grav);
                    tvGravity.invalidate();
                    // 每次点击扫描之前清空上一次的扫描结果
                    if(sb!=null){
                        sb=new StringBuffer();
                    }

                    //开始扫描网络
                    mWifiAdmin.startScan();
                    list=mWifiAdmin.getWifiList();
                    if(list!=null) {
                        for (int i = 0; i < list.size(); i++) {
                            //得到扫描结果
                            mScanResult = list.get(i);
                            sb = sb.append(mScanResult.BSSID + "  ").append(mScanResult.SSID + "   ")
                                    .append(mScanResult.capabilities + "   ").append(mScanResult.frequency + "   ")
                                    .append(mScanResult.level + "\n\n");
                        }
                        allNetWork.setText("The scanned Wifi Network: \n" + sb.toString());
                        allNetWork.invalidate();

                    }

                }
            }
        });

    }

}
