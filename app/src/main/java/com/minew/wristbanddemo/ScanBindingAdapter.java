package com.minew.wristbanddemo;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;

public class ScanBindingAdapter {

    @BindingAdapter("isActivated")
    public static void setActivate(TextView textView, boolean isActivated) {
        textView.setText(isActivated ? "Activated" : "Inactivated");
    }

    @BindingAdapter("power")
    public static void setPower(TextView textView, int power) {
        textView.setText(power+"");
    }

    @BindingAdapter("rssi")
    public static void setRssi(TextView textView, int rssi) {
        textView.setText(rssi+"");
    }
}
