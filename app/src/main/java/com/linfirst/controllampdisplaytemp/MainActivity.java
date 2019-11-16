package com.linfirst.controllampdisplaytemp;

import android.annotation.SuppressLint;
import android.app.Service;

import android.graphics.Color;

import android.os.Handler;

import android.os.Message;
import android.os.Vibrator;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


/**
 * @author linfirst
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button connection;

    private TextView ip_text;
    private TextView port_text;
    private EditText ip_edit;
    private EditText port_edit;

    private TextView sigh_text;
    private TextView temp_text;
    private TextView c_text;

    private ImageButton light_1;
    private ImageButton light_2;
    private ImageButton light_3;
    private ImageButton light_4;

    private Vibrator mVibrator;

    int sign = 1;
    boolean clickButton1 = true, clickButton2 = true, clickButton3 =
            true, clickButton4 = true, connect = false, clickConnect = false;

    int beat_flag = 0;

    MySocketClient mySocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    /**
     * 初始化View
     */
    private void initView() {
        connection = findViewById(R.id.connection);

        sigh_text = findViewById(R.id.sigh);
        temp_text = findViewById(R.id.temp);
        c_text = findViewById(R.id.c);

        ip_text = findViewById(R.id.ip_text);
        port_text = findViewById(R.id.port_text);
        ip_edit = findViewById(R.id.ip_edit);
        port_edit = findViewById(R.id.port_edit);

        light_1 = findViewById(R.id.light_1);
        light_2 = findViewById(R.id.light_2);
        light_3 = findViewById(R.id.light_3);
        light_4 = findViewById(R.id.light_4);

        //连接
        connection.setOnClickListener(this);

        light_1.setOnClickListener(this);
        light_2.setOnClickListener(this);
        light_3.setOnClickListener(this);
        light_4.setOnClickListener(this);

        ip_edit.setSelection(ip_edit.getText().length());
        port_edit.setSelection(port_edit.getText().length());

        setConnection();

        //调用震动
        mVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
    }

    /**
     * 处理点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connection: {

                if (clickConnect) {
                    Message message3 = new Message();
                    message3.what = 3;
                    handler.sendMessage(message3);
                    break;
                } else {
                    mySocketClient.initSocket(ip_edit.getText().toString(), Integer.valueOf(port_edit.getText().toString()));
                    break;
                }
            }
            case R.id.light_1:
                if (mySocketClient == null || !connect) {
                    Toast.makeText(this, "未连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (clickButton1) {
                    mySocketClient.sendMessage("1");
                    light_1.setImageResource(R.mipmap.turn_on_lights);
                    clickButton1 = false;
                } else {
                    mySocketClient.sendMessage("5");
                    light_1.setImageResource(R.mipmap.turn_off_lights);
                    clickButton1 = true;
                }
                break;
            case R.id.light_2:
                if (mySocketClient == null || !connect) {
                    Toast.makeText(this, "未连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (clickButton2) {
                    mySocketClient.sendMessage("2");
                    light_2.setImageResource(R.mipmap.turn_on_lights);
                    clickButton2 = false;
                } else {
                    mySocketClient.sendMessage("6");
                    light_2.setImageResource(R.mipmap.turn_off_lights);
                    clickButton2 = true;
                }
                break;
            case R.id.light_3:
                if (mySocketClient == null || !connect) {
                    Toast.makeText(this, "未连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (clickButton3) {
                    mySocketClient.sendMessage("3");
                    light_3.setImageResource(R.mipmap.turn_on_lights);
                    clickButton3 = false;
                } else {
                    mySocketClient.sendMessage("7");
                    light_3.setImageResource(R.mipmap.turn_off_lights);
                    clickButton3 = true;
                }
                break;
            case R.id.light_4:
                if (mySocketClient == null || !connect) {
                    Toast.makeText(this, "未连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (clickButton4) {
                    mySocketClient.sendMessage("4");
                    light_4.setImageResource(R.mipmap.turn_on_lights);
                    clickButton4 = false;
                } else {
                    mySocketClient.sendMessage("8");
                    light_4.setImageResource(R.mipmap.turn_off_lights);
                    clickButton4 = true;
                }
                break;
            default:
                break;
        }
    }

    /**
     * 监听是否连接成功
     */
    private void setConnection() {
        if (mySocketClient == null) {
            mySocketClient = new MySocketClient();
        }
        mySocketClient.setOnConnectionListener(new OnConnectionListener() {
            @Override
            public void success() {
                mySocketClient.receiveMessage();
                connect = true;
                Log.i("SSSS", "连接成功。");
                Message message1 = new Message();
                message1.what = 1;
                handler.sendMessage(message1);
                getTemp();
            }

            @Override
            public void failure() {
                Log.i("SSSS", "连接失败。");
                connect = false;
                Message message2 = new Message();
                message2.what = 2;
                handler.sendMessage(message2);
            }
        });
    }

    /**
     * 监听是否成功收消息，到获取温度
     */
    private void getTemp() {
        mySocketClient.setOnReceivesLisrtner(new OnReceivesListener() {
            @Override
            public void success(String message) {
                Message message4 = new Message();
                message4.what = 4;
                message4.obj = message;
                handler.sendMessage(message4);
                Log.e("MMMMM", message);
            }

            @Override
            public void failure(String message) {
                Message message5 = new Message();
                message5.what = 5;
                message5.obj = message;
                handler.sendMessage(message5);
            }
        });
    }

    /**
     * 在子线程更新UI使用
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                    connection.setText("断开连接");
                    ip_edit.setEnabled(false);
                    port_edit.setEnabled(false);
                    Log.i("开始连接", sign + "");
                    connection.setBackgroundResource(R.drawable.afterbutton);
                    ip_text.setTextColor(Color.rgb(0, 0, 0));
                    port_text.setTextColor(Color.rgb(0, 0, 0));
                    clickConnect = true;
                    break;
                case 2:
                    Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_SHORT).show();

                    break;
                case 3:
                    Toast.makeText(MainActivity.this, "断开连接", Toast.LENGTH_SHORT).show();
                    connection.setText("开始连接");
                    ip_edit.setEnabled(true);
                    port_edit.setEnabled(true);
                    Log.i("开始连接", sign + "");
                    connection.setBackgroundResource(R.drawable.button);
                    ip_text.setTextColor(Color.rgb(0, 0, 0));
                    port_text.setTextColor(Color.rgb(0, 0, 0));
                    clickConnect = false;
                    connect = false;
                    mySocketClient.close();
                    break;
                case 4:
                    setTempText(msg.obj + "");
                    break;
                case 5:
                    temp_text.setText("-");
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 对收到的温度字符串进行处理
     * @param msg
     */
    @SuppressLint("MissingPermission")
    public void setTempText(String msg) {
        if (msg.length() > 6) {
            temp_text.setText(msg.substring(4));
            if (Float.parseFloat(msg.substring(4)) > 32.5) {
                temp_text.setTextColor(Color.rgb(255, 0, 0));
                c_text.setTextColor(Color.rgb(255, 0, 0));
                sigh_text.setTextColor(Color.rgb(255, 0, 0));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //execute the task
                        sigh_text.setTextColor(Color.rgb(255, 255, 255));
                    }
                }, 2000);

                if (beat_flag == 0) {
                    beat_flag = 1;
                    beating();
                }
            } else {
                beat_flag = 0;
               closeBeating();
                sigh_text.setTextColor(Color.rgb(255, 255, 255));
                temp_text.setTextColor(Color.rgb(0, 148, 255));
                c_text.setTextColor(Color.rgb(0, 148, 255));
            }
        } else if (!"".equals(msg)) {
            temp_text.setText(msg);
            if (Float.parseFloat(msg) > 32) {
                temp_text.setTextColor(Color.rgb(255, 0, 0));
                c_text.setTextColor(Color.rgb(255, 0, 0));
                sigh_text.setTextColor(Color.rgb(255, 0, 0));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //execute the task
                        sigh_text.setTextColor(Color.rgb(255, 255, 255));
                    }
                }, 2000);
                if (beat_flag == 0) {
                    beat_flag = 1;
                    beating();
                }
            } else {
                closeBeating();
                sigh_text.setTextColor(Color.rgb(255, 255, 255));
                temp_text.setTextColor(Color.rgb(0, 148, 255));
                c_text.setTextColor(Color.rgb(0, 148, 255));
            }
        }
    }

    /**
     * 调用手机震动
     */
    @SuppressLint("MissingPermission")
    private void beating() {
        mVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
        /**
         * 四个参数就是——停止 开启 停止 开启
         * -1不重复，非-1为从pattern的指定下标开始重复
         */
        mVibrator.vibrate(new long[]{1000, 10000, 1000, 10000}, 0);
        //停止1秒，开启震动10秒，然后又停止1秒，又开启震动10秒，重复.
        //-1表示不重复, 如果不是-1, 比如改成1, 表示从前面这个long数组的下标为1的元素开始重复.
    }

    /**
     * 关闭手机震动
     */
    private void closeBeating(){
        mVibrator.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mySocketClient.close();
    }
}



