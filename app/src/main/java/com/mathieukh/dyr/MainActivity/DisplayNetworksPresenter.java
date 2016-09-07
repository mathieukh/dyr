package com.mathieukh.dyr.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.kennyc.view.MultiStateView;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by sylom on 24/08/2016.
 */
public class DisplayNetworksPresenter implements DisplayNetworksContract.Presenter {

    private final DisplayNetworksFragment mView;
    private final WifiManager mWifiManager;
    private BroadcastReceiver mBroadcastReceiver;
    private List<WifiConfiguration> mConfiguredNetworks;
    private WifiInfo mNetwork;

    public DisplayNetworksPresenter(DisplayNetworksFragment networksFragment, WifiManager wifiManager) {
        this.mView = checkNotNull(networksFragment);
        this.mWifiManager = checkNotNull(wifiManager);
        mBroadcastReceiver = new WifiReceiver();
        this.mView.setPresenter(this);
    }

    @Override
    public void start() {
        setupView();
    }

    private void setupView() {
        mView.setViewState(MultiStateView.VIEW_STATE_LOADING);
        mConfiguredNetworks = mWifiManager.getConfiguredNetworks();
        mNetwork = mWifiManager.getConnectionInfo();
        if (!mNetwork.getSupplicantState().equals(SupplicantState.COMPLETED) || mNetwork.getSSID().equals(""))
            mNetwork = null;
        if (mConfiguredNetworks == null) {
            mView.setViewState(MultiStateView.VIEW_STATE_ERROR);
        } else {
            mConfiguredNetworks = Stream.of(mConfiguredNetworks)
                    .filterNot(wc -> wc.SSID == null || wc.SSID.isEmpty())
                    .sorted((wcl, wcr) -> wcl.SSID.compareTo(wcr.SSID))
                    .collect(Collectors.toList());
            if (mConfiguredNetworks.isEmpty() && mNetwork == null) {
                mView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
            } else {
                mView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
            }
            mView.showNetworks(mConfiguredNetworks, mNetwork);
        }
    }

    @Override
    public BroadcastReceiver getReceiver() {
        return mBroadcastReceiver;
    }

    public class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            setupView();
        }
    }

}
