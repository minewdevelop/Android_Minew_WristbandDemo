package com.minew.wristbanddemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public final class DataUtil {
    private DataUtil() {

    }

    @Nullable
    @WorkerThread
    public static byte[] getData(@NonNull InputStream inputStream) {
        byte[] data = null;
        BufferedInputStream bufferedInputStream = null;
        try {
            bufferedInputStream = new BufferedInputStream(inputStream);
            byte[] bytes = new byte[46];
            int readLength = 0;
            int totalLength = 0;
            List<Byte> byteArrayList = new ArrayList<>();
            while ((readLength = bufferedInputStream.read(bytes)) != -1) {
                totalLength += readLength;

                byte[] temp = new byte[readLength];
                System.arraycopy(bytes, 0, temp, 0, readLength);

                for (int i = 0; i < readLength; i++) {
                    byteArrayList.add(bytes[i]);
                }
            }

            data = new byte[byteArrayList.size()];
            for (int i = 0; i < byteArrayList.size();i++) {
                data[i] = byteArrayList.get(i);
            }
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
