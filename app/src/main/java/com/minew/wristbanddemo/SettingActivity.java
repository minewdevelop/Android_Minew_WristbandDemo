package com.minew.wristbanddemo;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.minew.wristband.ble.bean.WristbandModule;
import com.minew.wristband.ble.interfaces.outside.OnChangeListener;
import com.minew.wristband.ble.interfaces.outside.OnConnStateListener;
import com.minew.wristband.ble.manager.MinewWristbandManager;

public class SettingActivity extends AppCompatActivity {

    private String macAddress;
    private MinewWristbandManager manager;
    private WristbandModule mModule;
    private ReadDeviceInfo mReadDeviceInfo;

    private LinearLayout llDistance;
    private LinearLayout llAlarmTemp;
    private LinearLayout llInterval;
    private LinearLayout llReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        macAddress = getIntent().getStringExtra("macAddress");
        mReadDeviceInfo = getIntent().getParcelableExtra("readDeviceInfo");
        Log.e("WristbandDemo", "setting: " + mReadDeviceInfo);
        manager = MinewWristbandManager.getInstance(this);
        mModule = manager.getDeviceByAddress(macAddress);

        llDistance = findViewById(R.id.ll_distance_value);
        llInterval = findViewById(R.id.ll_interval_time);
        llAlarmTemp = findViewById(R.id.ll_alarm_temp);
        llReset = findViewById(R.id.ll_reset);

        if (mModule.getFirmwareVersionCode() == 2) {
            llDistance.setVisibility(View.GONE);
            llAlarmTemp.setVisibility(View.GONE);
            llInterval.setVisibility(View.GONE);
            llReset.setVisibility(View.GONE);
        } else if (mModule.getFirmwareVersionCode() == 3) {
            llDistance.setVisibility(View.VISIBLE);
            llReset.setVisibility(View.VISIBLE);
            if (!mModule.hasTemperatureSensor()) {
                llAlarmTemp.setVisibility(View.GONE);
                llInterval.setVisibility(View.GONE);
            } else {
                llAlarmTemp.setVisibility(View.VISIBLE);
                llInterval.setVisibility(View.VISIBLE);
            }
        }

        initListener();
    }

    private void initListener() {
        llDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.setAlarmGear(mModule, 3, new OnChangeListener() {
                    @Override
                    public void onModifyResult(boolean b) {
                        showToast("setAlarmGear " + (b ? "success" : "failed"));
                    }
                });
            }
        });
        llInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //The unit is seconds
                manager.setTempMeasureInterval(mModule, 600, new OnChangeListener() {
                    @Override
                    public void onModifyResult(boolean b) {
                        showToast("setTempMeasureInterval " + (b ? "success" : "failed"));
                    }
                });
            }
        });
        llAlarmTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.setTempAlarmValue(mModule, 32.0F, new OnChangeListener() {
                    @Override
                    public void onModifyResult(boolean b) {
                        showToast("setTempAlarmValue " + (b ? "success" : "failed"));
                    }
                });
            }
        });
        llReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.resetDevice(mModule, new OnChangeListener() {
                    @Override
                    public void onModifyResult(boolean b) {
                        showLongToast("resetDevice " + (b ? "success" : "failed") + " ,device is disconnect, activity will finish!");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 200);

                        /**
                         * At this time, the device has been disconnected, and the current page needs to be closed!
                         * And call back the {@link com.minew.wristband.ble.enums.ConnectionState#Reset_Device} state
                         * in the {@link MinewWristbandManager#setOnConnStateListener(OnConnStateListener)} method.
                         *
                         * So we need to return to the {@link MainActivity} page.
                         */
                    }
                });
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(DemoApp.getInstance(), msg, Toast.LENGTH_SHORT).show();
    }

    private void showLongToast(String msg) {
        Toast.makeText(DemoApp.getInstance(), msg, Toast.LENGTH_LONG).show();
    }
}