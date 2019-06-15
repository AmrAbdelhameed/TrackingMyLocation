package com.example.trackingmylocation.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.example.trackingmylocation.model.LocationHelper;

public class LocationViewModel extends ViewModel {

    public LocationHelper getLocationHelper(Context mContext) {
        return LocationHelper.getInstance(mContext);
    }
}
