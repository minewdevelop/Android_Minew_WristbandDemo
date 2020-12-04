package com.minew.wristbanddemo;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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

    private OnReadHistoryDataListener<WristbandHistory> historyListener = new OnReadHistoryDataListener<WristbandHistory>(){

        @Override
        public void receiverDataCompletely(String s, ArrayList<WristbandHistory> arrayList) {
            adapter.addData(arrayList);
        }
    };

    private OnReadHistoryDataListener<TemperatureHistory> tempHistoryListener = new OnReadHistoryDataListener<TemperatureHistory>() {
        @Override
        public void receiverDataCompletely(String s, ArrayList<TemperatureHistory> arrayList) {
            Toast.makeText(DetailActivity.this,"read temp history success, " + arrayList.size()+ " items",Toast.LENGTH_SHORT).show();
            Log.e("WristbandDemo", "readTempHistory size: " + arrayList.size());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setTitle("Detail");
        macAddress = getIntent().getStringExtra("macAddress");
        manager = MinewWristbandManager.getInstance(this);

        RecyclerView rvHistory = findViewById(R.id.rv_history);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryDataAdapter();
        rvHistory.setAdapter(adapter);

        WristbandModule module = manager.getDeviceByAddress(macAddress);

        if (module.getFirmwareVersionCode() == 2) {
            //no temperature sensor
            manager.readHistoryData(module, 0, 6, historyListener);
        } else if (module.getFirmwareVersionCode() == 3) {
            if (!module.hasTemperatureSensor()) {
                //no temperature sensor
                manager.readHistoryData(module, 0, 6, historyListener);
            }else {
                //has temperature sensor
                manager.readTemperatureHistory(module,0,6,tempHistoryListener);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.disConnect(macAddress);
    }
}
