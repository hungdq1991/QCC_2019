package com.speedata.uhf.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.speedata.uhf.R;

import java.util.List;
import java.util.Objects;

/**
 * Card search list adapter
 *
 * @author 张智超
 * @date 2019/3/7
 */
public class UhfCardAdapter extends ArrayAdapter<UhfCardBean> {
    private int newResourceId;

    public UhfCardAdapter(Context context, int resourceId, List<UhfCardBean> uhfCardBeanList) {
        super( context, resourceId, uhfCardBeanList );
        newResourceId = resourceId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        UhfCardBean uhfCardBean = getItem( position );
        @SuppressLint("ViewHolder") View view = LayoutInflater.from( getContext() ).inflate( newResourceId, parent, false );

        TextView tvEpc = (TextView) view.findViewById( R.id.item_epc_tv );
        TextView tvvRssi = (TextView) view.findViewById( R.id.item_valid_rssi_tv );
        TextView tvTidUser = (TextView) view.findViewById( R.id.item_tid_user_tv );

        tvEpc.setText( Objects.requireNonNull( uhfCardBean ).getTvEpc() );
        tvvRssi.setText( uhfCardBean.getTvvRssi() );
        if (uhfCardBean.getTidUser() == null || uhfCardBean.getTidUser().isEmpty()) {
            tvTidUser.setVisibility( View.GONE );
        } else {
            tvTidUser.setVisibility( View.VISIBLE );
            tvTidUser.setText( uhfCardBean.getTvTidUser() );
        }

        return view;
    }
}
