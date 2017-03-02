package com.jdaddressselector;

import java.util.List;

/**
 * Created by dun on 17/2/9.
 */

public interface DataProvider {
    void provideData(int currentDeep, int preId, ISelectAble selectAble, DataReceiver receiver);


    interface DataReceiver {
        void send(List<ISelectAble> data);
    }
}
