package com.example.administrator.demo05_bluetooth.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.demo05_bluetooth.R;
import com.example.administrator.demo05_bluetooth.interfaces.OnItemBackListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fyl on 2018/9/21 0021.
 */

public class MyAdapter extends BaseAdapter{
    private Context context;
    private List<BluetoothDevice> list;
    private OnItemBackListener onItemBackListener;
    public MyAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    public void addBluetoothDevice(BluetoothDevice bluetoothDevice) {
        list.add(bluetoothDevice);
        notifyDataSetChanged();
    }

    public void addBluetoothTopDevice(BluetoothDevice bluetoothDevice) {
        list.add(0,bluetoothDevice);
        notifyDataSetChanged();
    }

    public void setOnItemBackListener(OnItemBackListener onItemBackListener) {
        this.onItemBackListener = onItemBackListener;
    }

    public void removeAll() {
        if (null != list && !list.isEmpty()) {
            list.clear();
        }
        notifyDataSetChanged();
    }

    public void removeDevice(BluetoothDevice device) {
        if (null != list && !list.isEmpty()) {
            if (list.contains(device)){
                list.remove(device);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        //  没有可利用的View对象
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.item = (LinearLayout) convertView.findViewById(R.id.item);
            viewHolder.num = (TextView) convertView.findViewById(R.id.num);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.address = (TextView) convertView.findViewById(R.id.address);
            viewHolder.state = (TextView) convertView.findViewById(R.id.state);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.num.setText(String.valueOf(position + 1));
        if(list.get(position).getName()==null){
            viewHolder.name.setVisibility(View.GONE);
        }else {
            viewHolder.name.setVisibility(View.VISIBLE);
        }
        viewHolder.name.setText(list.get(position).getName());
        viewHolder.address.setText(list.get(position).getAddress());
        if (list.get(position).getBondState() == BluetoothDevice.BOND_NONE){
            viewHolder.state.setText("未配对");
        }else if (list.get(position).getBondState() == BluetoothDevice.BOND_BONDED){
            viewHolder.state.setText("已配对");
        }
        viewHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemBackListener!=null){
                    onItemBackListener.listener(list.get(position));
                }
            }
        });
        return convertView;
    }


    public static class ViewHolder {
        private TextView num;
        private TextView name;
        private TextView address;
        private TextView state;
        private LinearLayout item;
    }
}
