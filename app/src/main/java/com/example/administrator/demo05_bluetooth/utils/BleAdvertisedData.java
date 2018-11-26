package com.example.administrator.demo05_bluetooth.utils;

import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2018/9/21 0021.
 */

public class BleAdvertisedData {

        private List<UUID> mUuids;
        private String mName;
        public BleAdvertisedData(List<UUID> uuids, String name){
            mUuids = uuids;
            mName = name;
        }

        public List<UUID> getUuids(){
            return mUuids;
        }

        public String getName(){
            return mName;
        }

    }
