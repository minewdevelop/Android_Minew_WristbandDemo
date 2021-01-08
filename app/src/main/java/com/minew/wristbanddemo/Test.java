package com.minew.wristbanddemo;

import android.net.Uri;

import androidx.fragment.app.Fragment;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class Test extends Fragment {

    public void test(Uri uri) throws FileNotFoundException {
        InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
        byte[] data = DataUtil.getData(inputStream);
    }
}
