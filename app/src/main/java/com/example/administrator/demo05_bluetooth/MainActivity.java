package com.example.administrator.demo05_bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.demo05_bluetooth.adapter.MyAdapter;
import com.example.administrator.demo05_bluetooth.interfaces.OnItemBackListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements OnItemBackListener{
    private static final String TAG = "MainActivity";
    private TextView tvDevices;
    private ListView listView;
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_BLUEBOOTH_ENABLE = 1;
    private List<BluetoothDevice> blueToothList;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        blueToothDetail();
    }

    private void initView() {
        blueToothList = new ArrayList<>();
        tvDevices = (TextView)findViewById(R.id.tv_devices);
        listView = (ListView) findViewById(R.id.listView);
        myAdapter = new MyAdapter(this);
        listView.setAdapter(myAdapter);
        //获取蓝牙适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //判断当前蓝牙是否打开
        if (!mBluetoothAdapter.isEnabled()) {
            //如果蓝牙不可用,弹出提示询问用户是否打开蓝牙
            Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enabler, REQUEST_BLUEBOOTH_ENABLE);
        }

        myAdapter.setOnItemBackListener(this);
    }

    /**
     * 蓝牙信息
     */
    private void blueToothDetail() {
        //获取本机蓝牙名称
        String name = mBluetoothAdapter.getName();
        //获取本机蓝牙地址
        String address = mBluetoothAdapter.getAddress();
        Log.d(TAG, "蓝牙名称: " + name + "蓝牙地址: " + address);
    }


    public void onClick(View view){
        switch (view.getId()){
            case R.id.button:
                myAdapter.removeAll();
                //已经配对的蓝牙设备
                Set<BluetoothDevice> paireDevices=mBluetoothAdapter.getBondedDevices();
                if (paireDevices.size()>0){
                    for (BluetoothDevice device:paireDevices) {
                        myAdapter.addBluetoothDevice(device);
                    }
                }
                mBluetoothAdapter.startDiscovery();
                registerReceiver();
                break;
            default:
                break;
        }
    }




    private void registerReceiver() {
        /**
         * 定义广播接收器
         */
        //设置广播信息过滤
        IntentFilter filter=new IntentFilter();
        //每搜到一个设备就会发送一个该广播
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        //当全部搜索完成后发送该广播
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //(配对时)绑定状态发生变化
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //设置优先级
        filter.setPriority(Integer.MAX_VALUE);
        //注册蓝牙搜索广播接收者，接收并处理搜索结果
        registerReceiver(receiver,filter);
    }


    /**
     * 定义广播接收器
     * @param view
     */
    private final BroadcastReceiver receiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState()!=BluetoothDevice.BOND_BONDED){
                        myAdapter.addBluetoothDevice(device);
                }
            }else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_NONE:
                        Log.e("******", "取消配对");
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        Log.e("******", "配对中");
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Log.e("******", "配对成功");
                        myAdapter.removeDevice(device);
                        myAdapter.addBluetoothTopDevice(device);
                        break;
                }
            }

        }
    };

    @Override
    public void listener(BluetoothDevice device) {
        if (device.getBondState()==BluetoothDevice.BOND_BONDED){
            Toast.makeText(this,"已配对",Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            //如果想要取消已经配对的设备，只需要将creatBond改为removeBond
            Method method = BluetoothDevice.class.getMethod("createBond");
            Log.e("******", "开始配对");
            method.invoke(device);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
