package com.example.bajpai.avdtest;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

/**
 * Created by bajpai on 14/7/16.
 */
public class WifiActivity extends AppCompatActivity {

    private WifiManager mWifiManager;
    private Button enable;
    private Button disable;
    private BroadcastReceiver receiver;
    private ListView lv;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.wifi_activity);
        enable = (Button) findViewById(R.id.enable);
        disable = (Button)findViewById(R.id.disable);
        setOnClickListeners();
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        lv = (ListView) findViewById(R.id.listView);

    }

    @Override
    public void onResume() {
        super.onResume();
        checkForAccessWifiStatePermission();
        if(isWifiEnabled()) {
            showScanResults();
        }
        registerBroadcastReceiver();
    }

    private void checkForAccessWifiStatePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_WIFI_STATE},
                    100);
        }
    }

    private void checkForChangeWifiStatePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CHANGE_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CHANGE_WIFI_STATE},
                    101);
        }
    }

    public void onStop() {
        super.onStop();
        unregisterBoradcastReceiver();
    }

    private void unregisterBoradcastReceiver() {
        if(receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    private void registerBroadcastReceiver() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver((receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                showScanResults();
            }
        }), intentFilter);
    }

    private void showScanResults() {
        String[] wifis;
        List<ScanResult> wifiScanList = mWifiManager.getScanResults();
        wifis = new String[wifiScanList.size()];

        for(int i = 0; i < wifiScanList.size(); i++){
            wifis[i] = ((i+1)+".   "+(wifiScanList.get(i).SSID)+"      Signal level: "+ mWifiManager.calculateSignalLevel(wifiScanList.get(i).level, 5));
        }
        lv.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item,wifis));
    }


    private void setOnClickListeners() {
        enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableWifi();
            }
        });

        disable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableWifi();
            }
        });
    }

    private void enableWifi() {
        checkForChangeWifiStatePermission();
        mWifiManager.setWifiEnabled(true);
    }

    private void disableWifi() {
        checkForChangeWifiStatePermission();
        mWifiManager.setWifiEnabled(false);
        lv.setAdapter(null);
    }

    private boolean isWifiEnabled() {
        return mWifiManager.isWifiEnabled();
    }
}
