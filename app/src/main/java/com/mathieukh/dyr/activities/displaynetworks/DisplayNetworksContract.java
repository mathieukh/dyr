package com.mathieukh.dyr.activities.displaynetworks;

import android.content.BroadcastReceiver;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;

import com.mathieukh.dyr.BasePresenter;
import com.mathieukh.dyr.BaseView;

import java.util.List;

/**
 * Created by sylom on 24/08/2016.
 */
public interface DisplayNetworksContract {

    interface View extends BaseView<Presenter> {

        void setViewState(int viewState);

        void showNetworks(List<WifiConfiguration> mConfiguredNetworks, WifiInfo mNetwork);
    }

    interface Presenter extends BasePresenter {

        BroadcastReceiver getReceiver();

    }
}
