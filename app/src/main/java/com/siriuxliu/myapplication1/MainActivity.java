package com.siriuxliu.myapplication1;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static android.R.attr.gravity;

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
    private SensorManager mSensorManager;
    private float[] gravity = new float[3];
    //private float[] mag = new float[3];
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWifiAdmin = new WifiAdmin(MainActivity.this);
        tvAccelerometer = (TextView) findViewById(R.id.Acc);
        tvMag = (TextView) findViewById(R.id.Mag);
        //获取传感器SensorManager对象
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
       // shackPhone = (TextView)findViewById(R.id.shack);
        init();
        //initWithAcceler();
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
                    Toast.makeText(MainActivity.this, "Current Status："+mWifiAdmin.checkState(), Toast.LENGTH_LONG).show();
                    break;
                case R.id.stop://关闭Wifi
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
    /***************      WIFI                                                  *******************/
    /**********************************************************************************************/
    String accelerometer;
    String mag;
    public boolean getAllNetWorkList(){//throws Exception {
        // 每次点击扫描之前清空上一次的扫描结果
        if(sb!=null){
            sb=new StringBuffer();
        }

        //开始扫描网络
        mWifiAdmin.startScan();
        list=mWifiAdmin.getWifiList();
        if(list!=null){
            for(int i=0;i<list.size();i++){
                //得到扫描结果
                mScanResult=list.get(i);
                sb=sb.append(mScanResult.BSSID+"  ").append(mScanResult.SSID+"   ")
                        .append(mScanResult.capabilities+"   ").append(mScanResult.frequency+"   ")
                        .append(mScanResult.level+"\n\n");
            }
            allNetWork.setText("The scanned Wifi Network: \n"+sb.toString());
            tvAccelerometer.setText(accelerometer);
            tvMag.setText(mag);
        }
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

                //break;
            case Sensor.TYPE_GRAVITY://重力传感器
                gravity[0] = event.values[0];//单位m/s^2
                gravity[1] = event.values[1];
                gravity[2] = event.values[2];
                break;
            case Sensor.TYPE_ACCELEROMETER: //加速度传感器
                final float alpha = (float) 1;
                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

                accelerometer = "Accelerometer\n" + "x:"
                        + (event.values[0] - gravity[0]) + "\n" + "y:"
                        + (event.values[1] - gravity[1]) + "\n" + "z:"
                        + (event.values[2] - gravity[2])+"\n";
                // tvAccelerometer.setText(accelerometer);
                //重力加速度9.81m/s^2，只受到重力作用的情况下，自由下落的加速度
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
                SensorManager.SENSOR_DELAY_FASTEST);//采集频率
        //注册重力传感器
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST);
    }
    /**
     * 暂停Activity，界面获取焦点，按钮可以点击时回调
     */
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


}



//String txt = BSSID+"    "+SSID+"    "+Capab+"   "+freq+"    "+level+"\n";
          /* String txt=sb.toString();

            try {
                // File file = new File("/data/data/com.liwei.loginview/info.txt");

                File file = new File("/sdcard/Data/info.txt"); //获取路径 如 "/data/data/com.liwei.loginview/files / 创建文件  info.txt
                // context.getFilesDir();//返回一个目录/data/data/com.liwei.loginview+
                // files
                FileOutputStream fos = new FileOutputStream(file);
                // zhangsan ## 123
                fos.write(txt.getBytes());
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {

                e.printStackTrace();
                return false;
            }*/

// fos.write(txt.getBytes());
// fos.flush();
// fos.close();




    /*public void onSensorChanged(SensorEvent event) {
        //判断传感器类别
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER: //加速度传感器
                final float alpha = (float) 0.8;
                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

                String accelerometer = "加速度传感器\n" + "x:"
                        + (event.values[0] - gravity[0]) + "\n" + "y:"
                        + (event.values[1] - gravity[1]) + "\n" + "z:"
                        + (event.values[2] - gravity[2]);
                tvAccelerometer.setText(accelerometer);
                //重力加速度9.81m/s^2，只受到重力作用的情况下，自由下落的加速度
                break;
            case Sensor.TYPE_GRAVITY://重力传感器
                gravity[0] = event.values[0];//单位m/s^2
                gravity[1] = event.values[1];
                gravity[2] = event.values[2];
                break;
            default:
                break;
        }
    }
    /**
     * 界面获取焦点，按钮可以点击时回调
     */
   /* protected void onResume() {
        super.onResume();
        //注册加速度传感器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),//传感器TYPE类型
                SensorManager.SENSOR_DELAY_UI);//采集频率
        //注册重力传感器
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_FASTEST);
    }
    /**
     * 暂停Activity，界面获取焦点，按钮可以点击时回调
     */
    /*@Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    */

//}
