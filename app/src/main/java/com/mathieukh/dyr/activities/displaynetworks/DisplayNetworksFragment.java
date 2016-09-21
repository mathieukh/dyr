package com.mathieukh.dyr.activities.displaynetworks;

import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kennyc.view.MultiStateView;
import com.mathieukh.dyr.activities.edittasks.EditTasksActivity;
import com.mathieukh.dyr.R;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by sylom on 24/08/2016.
 */
public class DisplayNetworksFragment extends Fragment implements DisplayNetworksContract.View {

    private DisplayNetworksContract.Presenter mPresenter;
    private RecyclerView mNetworksList;
    private MultiStateView mStateView;
    private NetworkAdapter mAdapter;
    private CardView mCurrentNetCard;

    public DisplayNetworksFragment() {

    }

    public static DisplayNetworksFragment newInstance() {
        return new DisplayNetworksFragment();
    }

    @Override
    public void setPresenter(DisplayNetworksContract.Presenter presenter) {
        this.mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new NetworkAdapter(new ArrayList<>(0));
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.display_net_frag, container, false);
        mCurrentNetCard = (CardView) view.findViewById(R.id.card_view);
        mStateView = (MultiStateView) view.findViewById(R.id.stateView);
        mNetworksList = (RecyclerView) view.findViewById(R.id.networksList);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNetworksList.setAdapter(mAdapter);
        mNetworksList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mNetworksList.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void setViewState(int viewState) {
        mStateView.setViewState(viewState);
    }

    @Override
    public void showNetworks(List<WifiConfiguration> mConfiguredNetworks, WifiInfo mNetwork) {
        mAdapter.replaceData(mConfiguredNetworks);
        if (mNetwork == null) {
            mCurrentNetCard.setVisibility(View.GONE);
        } else {
            mCurrentNetCard.setVisibility(View.VISIBLE);
            mCurrentNetCard.setOnClickListener(view -> getTasksNetworkActivity(mNetwork.getSSID()));
        }
    }

    public void getTasksNetworkActivity(String ssid) {
        Intent detailsIntent =
                new Intent(getActivity(), EditTasksActivity.class);
        detailsIntent.putExtra("SSID", ssid);
        startActivity(detailsIntent);
    }

    public class NetworkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<WifiConfiguration> networks;

        public NetworkAdapter(List<WifiConfiguration> data) {
            this.networks = data;
        }

        public void replaceData(List<WifiConfiguration> data) {
            checkNotNull(data);
            this.networks = data;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.item_simple, parent, false);
            return new NetworkItem(itemView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            WifiConfiguration network = networks.get(position);
            ((NetworkItem) holder).vNetwork.setText(network.SSID.replaceAll("\"", ""));
            holder.itemView.setOnClickListener(view -> getTasksNetworkActivity(network.SSID));
        }

        @Override
        public int getItemCount() {
            return networks.size();
        }


        class NetworkItem extends RecyclerView.ViewHolder {

            private final TextView vNetwork;

            public NetworkItem(View itemView) {
                super(itemView);
                vNetwork = (TextView) itemView.findViewById(R.id.text1);
            }
        }
    }
}
