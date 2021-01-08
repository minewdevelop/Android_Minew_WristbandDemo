package com.minew.wristbanddemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.minew.wristband.ble.bean.WristbandModule;
import com.minew.wristband.ble.enums.ConnectionState;
import com.minew.wristband.ble.interfaces.outside.OnConnStateListener;
import com.minew.wristband.ble.interfaces.outside.OnScanWristbandResultListener;
import com.minew.wristband.ble.manager.MinewWristbandManager;
import com.minew.wristband.ble.utils.BLETool;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, OnScanWristbandResultListener {

    private SwipeRefreshLayout srlScan;
    private RecyclerView rvScan;
    private ScanListAdapter adapter;
    private MinewWristbandManager manager;

    private Handler handler = new Handler();
    private Runnable stopScanRunnable = this::stopScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        manager = MinewWristbandManager.getInstance(this);
        if (BLETool.isBluetoothTurnOn(this)) {
            requestLocationPermissions();
        } else {
            Toast.makeText(this, "Attention BLE!!", Toast.LENGTH_SHORT).show();
        }

        initListener();
    }

    private void initListener() {

        manager.setOnConnStateListener(new OnConnStateListener() {
            @Override
            public void onUpdateConnState(String macAddress, ConnectionState connectionState) {
                showToast(connectionState.name());
                switch (connectionState) {
                    case Disconnect:

                        break;
                    case Inactivated:

                        break;

                    case PasswordError:

                        break;
                    case Verify_Password:
                        //show dialog and input password.
                        manager.sendPassword(macAddress, "minew123");
                        break;
                    case Connect_Complete:

                        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                        intent.putExtra("macAddress", macAddress);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void requestLocationPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            startScan();
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 5);
            } else {
                startScan();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 5);
            } else {
                startScan();
            }
        }
    }

    private void initView() {
        srlScan = findViewById(R.id.srl_scan);
        rvScan = findViewById(R.id.rv_scan);

        adapter = new ScanListAdapter();
        srlScan.setOnRefreshListener(this);
        rvScan.setLayoutManager(new LinearLayoutManager(this));
        rvScan.setAdapter(adapter);

        adapter.setOnItemChildClickListener(new ScanListAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(View view, int position) {
                showToast("Start activate!");
                if (manager.isSupportAdvertisement()) {
                    manager.startAwaken(adapter.getItem(position).getMacAddress());
                } else {
                    Toast.makeText(MainActivity.this, "Not Support Awaken!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        adapter.setOnItemClickListener(new ScanListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                WristbandModule itemData = adapter.getItem(position);
                if (itemData.isAwakened()) {
                    stopScan();
                    manager.connect(MainActivity.this, itemData);
                } else {
                    showToast("device is inactivated!");
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        stopScan();
        adapter.clearData();
        startScan();
        srlScan.setRefreshing(false);
    }

    private void startScan() {
        manager.startScan(this);
        handler.postDelayed(stopScanRunnable, 60 * 1000 * 1000);
    }

    private void stopScan() {
        manager.stopScan();
        manager.stopAwaken();
        handler.removeCallbacks(stopScanRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScan();
        handler.removeCallbacks(stopScanRunnable);
    }

    @Override
    public void onScanWristbandResult(ArrayList<WristbandModule> arrayList) {
        adapter.addNewData(arrayList);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 5) {

            startScan();
        }
    }

    private Toast toast;

    private void showToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }
}
