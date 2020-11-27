package com.minew.wristbanddemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.minew.wristband.ble.bean.TemperatureHistory;
import com.minew.wristband.ble.bean.WristbandHistory;
import com.minew.wristband.ble.bean.WristbandModule;
import com.minew.wristband.ble.interfaces.outside.OnChangeListener;
import com.minew.wristband.ble.interfaces.outside.OnReadAllHistoryDataListener;
import com.minew.wristband.ble.interfaces.outside.OnReadHistoryDataListener;
import com.minew.wristband.ble.interfaces.outside.OnReadValueListener;
import com.minew.wristband.ble.manager.MinewWristbandManager;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity implements OnReadHistoryDataListener<WristbandHistory> {

    private MinewWristbandManager manager;
    private String macAddress;
    private HistoryDataAdapter adapter;

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
        manager.readHistoryData(module, 0, 6, this);
    }

    @Override
    public void receiverDataCompletely(String macAddress, ArrayList<WristbandHistory> arrayList) {
        adapter.addData(arrayList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.disConnect(macAddress);
    }
}
