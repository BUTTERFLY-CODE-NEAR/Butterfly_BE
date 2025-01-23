package com.codenear.butterfly.global.util;


import java.util.HashMap;

public class HashMapUtil<K,V> extends HashMap<K, V> {

    // value에 값이 null이라면 맵에 넣지 않는다
    @Override
    public V put(K key, V value) {
        if(value != null){
            return super.put(key, value);
        }
        return null;
    }
}
