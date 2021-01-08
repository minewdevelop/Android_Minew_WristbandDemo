package com.minew.wristbanddemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.minew.wristband.ble.bean.TemperatureHistory;
import com.minew.wristband.ble.bean.WristbandHistory;
import com.minew.wristband.ble.bean.WristbandModule;
import com.minew.wristband.ble.interfaces.outside.OnReadHistoryDataListener;
import com.minew.wristband.ble.manager.MinewWristbandManager;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private MinewWristbandManager manager;
    private String macAddress;
    private HistoryDataAdapter adapter;
    private OperationViewModel mViewModel;
    private WristbandModule module;
    private TextView tvReadInfo;
    private TextView tvHistoryNum;
    private ReadDeviceInfo readDeviceInfo;
    private boolean isInitialComplete = false;

    private OnReadHistoryDataListener<WristbandHistory> historyListener = new OnReadHistoryDataListener<WristbandHistory>() {

        @Override
        public void receiverDataCompletely(String s, ArrayList<WristbandHistory> arrayList) {
            isInitialComplete = true;
            adapter.addData(arrayList);
        }
    };

    private OnReadHistoryDataListener<TemperatureHistory> tempHistoryListener = new OnReadHistoryDataListener<TemperatureHistory>() {
        @Override
        public void receiverDataCompletely(String s, ArrayList<TemperatureHistory> arrayList) {
            isInitialComplete = true;
            Toast.makeText(DetailActivity.this, "read temp history success, " + arrayList.size() + " items", Toast.LENGTH_SHORT).show();
            Log.e("WristbandDemo", "readTempHistory size: " + arrayList.size());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setTitle("Detail");
        tvReadInfo = findViewById(R.id.tv_read_info);
        tvHistoryNum = findViewById(R.id.tv_history_num);

        macAddress = getIntent().getStringExtra("macAddress");
        manager = MinewWristbandManager.getInstance(this);

        RecyclerView rvHistory = findViewById(R.id.rv_history);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryDataAdapter();
        rvHistory.setAdapter(adapter);

        module = manager.getDeviceByAddress(macAddress);
        int totalRecord = module.getTotalRecord();
        if (module.hasTemperatureSensor()) {
            tvHistoryNum.setText("totalNum: " + totalRecord + ", and the  temperatureTotalNum: " + module.getTotalTemperatureRecord());
        } else {
            tvHistoryNum.setText("totalNum: " + totalRecord);
        }

        mViewModel = new ViewModelProvider(this).get(OperationViewModel.class);
        mViewModel.readDeviceInfo(module);

        initLiveData();
    }

    private void initLiveData() {
        mViewModel.getDeviceInfoLiveData().observe(this, new Observer<ReadDeviceInfo>() {
            @Override
            public void onChanged(ReadDeviceInfo info) {
                readDeviceInfo = info;
                if (info.getMeasureInterval() != null) {
                    //this measureInterval is  is in seconds!
                    tvReadInfo.setText(info.toString() + ", which is " + info.getMeasureInterval() / 60 + "minutes!");
                } else {
                    tvReadInfo.setText(info.toString());
                }
                if (module.getFirmwareVersionCode() == 2) {
                    //no temperature sensor
                    manager.readHistoryData(module, 0, 6, historyListener);
                } else if (module.getFirmwareVersionCode() == 3) {
                    if (!module.hasTemperatureSensor()) {
                        //no temperature sensor
                        manager.readHistoryData(module, 0, 6, historyListener);
                    } else {
                        //has temperature sensor
                        manager.readTemperatureHistory(module, 0, 6, tempHistoryListener);
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.setting) {
            if (!isInitialComplete) {
                Toast.makeText(DetailActivity.this, "need initialize! Please wait.", Toast.LENGTH_SHORT).show();
                return true;
            }
            Intent intent = new Intent(this, SettingActivity.class);
            intent.putExtra("macAddress", macAddress);
            intent.putExtra("readDeviceInfo", readDeviceInfo);
            startActivity(intent);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.disConnect(macAddress);
    }
}
