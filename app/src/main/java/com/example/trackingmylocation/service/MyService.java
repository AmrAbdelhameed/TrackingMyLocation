package com.example.trackingmylocation.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.trackingmylocation.model.FirebaseLocationHelper;

public class MyService extends Service {
    private FirebaseLocationHelper firebaseLocationHelper;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        firebaseLocationHelper = FirebaseLocationHelper.getInstance(this);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        firebaseLocationHelper.stopLocationUpdates();
    }
}
